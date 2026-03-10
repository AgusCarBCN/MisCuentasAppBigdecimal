


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id ("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

    }

android {
    namespace = "carnerero.agustin.cuentaappandroid"
    compileSdk = 36

    defaultConfig {
        applicationId = "carnerero.agustin.cuentaappandroid"
        minSdk = 26
        targetSdk = 35
        versionCode = 58
        versionName = "5.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"



    }
    buildFeatures {
        buildConfig = true
        compose=true
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }


}
secrets {
    // This production secrets file and going to contains real secrets
    propertiesFileName = "secrets.properties"


}
dependencies {

    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.window)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.compose.material.core)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.protolite.well.known.types)
    implementation(libs.androidx.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    // To use Kotlin annotation processing tool (kapt)
    ksp(libs.androidx.room.compiler)
    //room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.compose.material3)
    annotationProcessor(libs.androidx.room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil.compose)
    runtimeOnly(libs.androidx.material.icons.extended)
    implementation(libs.androidx.constraintlayout.v220beta01)
    implementation(libs.compose.modified.snackbar)
  
    implementation (libs.androidx.compose.material.icons.extended)
    // To use constraintlayout in compose
    implementation(libs.androidx.constraintlayout.compose)
    implementation (libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation (libs.commons.csv)
    implementation(libs.kotlinx.serialization.json)
    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation (libs.circleindicator)
    //Dagger Hilt
    implementation (libs.hilt.android)
    ksp (libs.hilt.compiler)
    implementation (libs.androidx.hilt.navigation.compose.v100)
    //Chart
    implementation (libs.mpandroidchart)
    implementation (libs.accompanist.permissions)
    //Google adMob
    implementation (libs.play.services.ads)
}
hilt {
    enableAggregatingTask = true
}




