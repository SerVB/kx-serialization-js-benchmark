plugins {
  kotlin("js") version "1.3.72"
  kotlin("plugin.serialization") version "1.3.72"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  implementation(kotlin("stdlib-js", "1.3.72"))
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:0.20.0")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf-js:0.20.0")
  implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.1")
}

kotlin.target.browser()
