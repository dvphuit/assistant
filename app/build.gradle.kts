plugins {
    androidApplication
    kotlinAndroid
    kotlinKapt
    daggerHiltAndroid
    id("kotlin-parcelize")
}

android {

    compileSdk = appConfig.compileSdkVersion
    buildToolsVersion = appConfig.buildToolsVersion

    defaultConfig {
        applicationId = ("dvp.app.assistant")
        minSdk = appConfig.minSdkVersion
        targetSdk = appConfig.targetSdkVersion
        testInstrumentationRunner = ("androidx.test.runner.AndroidJUnitRunner")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                ("proguard-rules.pro")
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = deps.compose.version
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(deps.jetbrains.coroutinesCore)
    implementation(deps.jetbrains.coroutinesAndroid)

    implementation(deps.androidx.coreKtx)
    implementation(deps.androidx.material)

    implementation(deps.data.dataStore.preferences)
    implementation(deps.data.parser.gson)
    implementation(deps.data.parser.jsoup)

    implementation(deps.accompanist.pager)
    implementation(deps.accompanist.pagerIndicator)
    implementation(deps.accompanist.placeholder)
    implementation(deps.accompanist.systemUI)
    implementation(deps.accompanist.insets)
    implementation(deps.accompanist.insetsUI)
    implementation(deps.accompanist.flowlayout)
    implementation(deps.accompanist.drawablepainter)

    implementation(deps.compose.constraintLayout)
    implementation(deps.compose.livedata)
    implementation(deps.compose.util)
    implementation(deps.compose.unit)

    implementation("com.google.mlkit:text-recognition:16.0.0-beta1")
    implementation("com.google.mlkit:text-recognition-chinese:16.0.0-beta1")
    implementation("com.google.mlkit:text-recognition-japanese:16.0.0-beta1")

    implementCompose()
    implementRoom()
    implementHilt()
    implementSquareup()
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

}