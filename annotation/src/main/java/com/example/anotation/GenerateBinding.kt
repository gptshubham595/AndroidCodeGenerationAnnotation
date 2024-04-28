package com.example.anotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
//@Retention(AnnotationRetention.SOURCE)
annotation class GenerateBinding(val type: KClass<*>) {
}

@Target(AnnotationTarget.FUNCTION)
annotation class SampleMethod {
}