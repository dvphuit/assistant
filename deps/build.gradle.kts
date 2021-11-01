repositories {
    mavenCentral()
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

gradlePlugin {
    plugins.register("module.deps") {
        id = "module.deps"
        implementationClass = "ClassLoaderPlugin"
    }
}