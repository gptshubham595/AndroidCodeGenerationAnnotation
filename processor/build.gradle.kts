plugins {
    id("java-library")
    kotlin("kapt")
    alias(libs.plugins.jetbrainsKotlinJvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
dependencies {
    implementation(project(":annotation"))
    implementation(libs.javaPoet)
    implementation(libs.dagger.android)

    compileOnly(libs.auto.service)
    kapt(libs.auto.service)

    compileOnly(libs.incap)
    kapt(libs.incap.processors)
}
