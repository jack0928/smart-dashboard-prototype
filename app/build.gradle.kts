plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.jack0928.pleosdashboard"
    compileSdk = 36   // 이 PC에 설치된 플랫폼(android-36.1)에 맞춤

    // android.car.* (CarPropertyManager 등) 플랫폼 API 를
    // 컴파일 단계에서 사용하기 위한 선언. AAOS 앱의 표준 방식입니다.
    useLibrary("android.car")

    defaultConfig {
        applicationId = "io.github.jack0928.pleosdashboard"
        minSdk = 30          // android.car 생명주기 콜백 API 가 30(Android 11)부터 안정적
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
