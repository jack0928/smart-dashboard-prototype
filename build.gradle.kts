// 루트 빌드 스크립트.
// 여기서는 플러그인 버전만 선언하고(apply false), 실제 적용은 app 모듈에서 합니다.
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}
