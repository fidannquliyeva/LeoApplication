// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.gms.google.services) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.hilt) apply false


}
//buildscript {
//    repositories {
//        google()
//        mavenCentral()
//    }
//    dependencies {
//        classpath("com.android.tools.build:gradle-kotlin:8.13.0") // AGP versiyan
//        classpath("com.google.gms:google-services:4.4.0") // <- buraya əlavə et
//    }
//}