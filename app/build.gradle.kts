import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

// Load local.properties manually
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

android {
    namespace = "com.mavilon.mongomovies"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mavilon.mongomovies"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Secure API key injection from local.properties or project properties
        val tmdbApiKey = localProperties.getProperty("TMDB_API_KEY") 
            ?: project.findProperty("TMDB_API_KEY") as String? 
            ?: ""
            
        // TMDB v4 Read Access Token (Bearer token)
        val tmdbReadToken = localProperties.getProperty("TMDB_READ_TOKEN")
            ?: project.findProperty("TMDB_READ_TOKEN") as String?
            ?: ""
        
        // Debug: Verify token is loaded (log presence only, not the value)
        println("=== TMDB Configuration ===")
        println("TMDB_API_KEY present: ${tmdbApiKey.isNotEmpty()}")
        println("TMDB_READ_TOKEN present: ${tmdbReadToken.isNotEmpty()}")
        println("TMDB_READ_TOKEN length: ${tmdbReadToken.length}")
        println("==========================")
            
        if (tmdbApiKey.isEmpty() && tmdbReadToken.isEmpty()) {
            println("WARNING: Neither TMDB_API_KEY nor TMDB_READ_TOKEN is set in local.properties")
            println("Add TMDB_READ_TOKEN=your_token to local.properties for v4 authentication")
        }

        buildConfigField("String", "TMDB_API_KEY", "\"$tmdbApiKey\"")
        buildConfigField("String", "TMDB_READ_TOKEN", "\"$tmdbReadToken\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    
    // Coil for image loading
    implementation(libs.coil.compose)
    
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Paging 3
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // Accompanist
    implementation(libs.accompanist.systemuicontroller)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}