plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    id("maven-publish")
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
}

android {
    namespace = "com.fulfillmenttools.danieltestapplication"
    setCompileSdkVersion(33)

    defaultConfig {
        applicationId = "com.fulfillmenttools.danieltestapplication"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.core:core-ktx:1.10.1")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2022.10.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2022.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

publishing {
    publications {
        register<MavenPublication>("Daniel") {
            groupId = "com.fulfillmenttools"
            artifactId = "danieltestapplication"
            version = "1.2"

            afterEvaluate {
                android.applicationVariants.all {
                    this.outputs.forEach { output ->
                        if (output.name == "release") {
                            artifact("$buildDir/outputs/apk_from_bundle/release/app-release-universal-unsigned.apk") {
                                extension = "apk"
                            }
                            artifact("$buildDir/outputs/bundle/release/app-release.aab") {
                                extension = "aab"
                            }
                        }
                    }
                }
            }
        }
    }

    repositories {
        maven {
            //url = uri("${project.buildDir}/repo")
            url = uri("artifactregistry://europe-west1-maven.pkg.dev/ocff-infra-app-artifacts-prd/android-registry")
        }
    }
}
