import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("jvm")
  id("java-test-fixtures")
  alias(libs.plugins.mavenPublish)
}

dependencies {
  implementation(libs.kotlin.stdlib.jdk8)

  testFixturesImplementation(libs.kotlin.stdlib)
}

kotlin { compilerOptions { jvmTarget = JvmTarget.JVM_1_8 } }

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}
