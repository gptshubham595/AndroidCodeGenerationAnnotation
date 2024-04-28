package com.example.processor

import com.example.anotation.GenerateBinding
import com.google.auto.service.AutoService
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import dagger.Binds
import dagger.Module
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.ISOLATING)
class GenerateBindingProcessor : AbstractProcessor() {
    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(GenerateBinding::class.qualifiedName!!)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
    }

    override fun process(annotation: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(GenerateBinding::class.java)
            .filterIsInstance<TypeElement>().forEach { typeElement ->
                generateHiltModuleWithBinding(typeElement)
            }

        return true
    }

    private fun generateHiltModuleWithBinding(type: TypeElement) {
        val firstInterface = type.interfaces.firstOrNull() ?: type.superclass
        val boundClassType = TypeName.get(firstInterface)
        val boundClassTypeName =
            processingEnv.typeUtils.asElement(firstInterface).simpleName

        /**
         * Will replace this
         * @Module
         * @InstallIn(SingletonComponent::class)
         * abstract class HiltModule {
         *
         *     @Binds
         *     abstract fun bindMainRepository(mainRepositoryImpl: MainRepositoryImpl): MainRepository
         * }
         */


        // created a method spec that will be added to the generated module dynamic function
        val methodSpec = MethodSpec.methodBuilder("bind$boundClassTypeName")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) // public abstract
            .addAnnotation(Binds::class.java) // @Binds
            .addParameter(
                TypeName.get(type.asType()),
                "${boundClassTypeName}Impl"
            ) // (variable : TypeElement)
            .returns(boundClassType) // : TypeElement

        // create a class spec that will be added to the generated module
        val classSpec = TypeSpec.classBuilder("${boundClassTypeName}Module")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT) // public
            .addAnnotation(Module::class.java) // @Module
            .addAnnotation( // @InstallIn(SingletonComponent::class)
                AnnotationSpec.builder(ClassName.get("dagger.hilt", "InstallIn"))
                    .addMember(
                        "value",
                        "\$T.class",
                        ClassName.get("dagger.hilt.components", "SingletonComponent")
                    ).build()
            )
            .addMethod(methodSpec.build()) // add the method to the class
            .addOriginatingElement(type) // track origin of the generated class
            .build()

        // write the class to the file
        val javaFile = JavaFile.builder(
            ClassName.get(type).packageName(), // file name
            classSpec // write the class to the file
        ).build()

        javaFile.writeTo(processingEnv.filer) // write the file to the file system
    }

}