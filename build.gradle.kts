import java.net.URI

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id ("module.deps")
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven {
            this.setUrl("https://jitpack.io")
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0")
//        classpath(deps.daggerHilt.gradlePlugin)
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
        classpath(kotlin("gradle-plugin", version = "1.5.10"))
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
