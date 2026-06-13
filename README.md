# Smart Dashboard Prototype

현대자동차그룹의 차세대 차량 OS **Pleos Connect** 환경을 학습하기 위해 만든 **차량 데이터 대시보드** 앱입니다.
Pleos Connect 가 기반하는 **Android Automotive OS(AAOS)** 의 표준 API(`android.car` / `CarPropertyManager`)로
차량 상태(속도·연료·기어 등)를 조회·표시합니다.

> 개인 학습/포트폴리오용 프로젝트입니다.

---

## 무엇을 만들었나

- AAOS 차량 앱으로서, 차량 서비스(Car service)에 연결해 **6가지 차량 신호**를 읽어 화면에 표시
- 값이 바뀌면 **실시간 콜백**으로 갱신, 권한이 없는 신호는 안전하게 "권한 필요"로 표시
- 언어: **Kotlin** / 빌드: **Gradle (AGP 8.5.2)**

| 신호 | AAOS 속성 (`VehiclePropertyIds`) |
|------|----------------------------------|
| 차량 속도 | `PERF_VEHICLE_SPEED` |
| 연료 잔량 | `FUEL_LEVEL` |
| EV 배터리 | `EV_BATTERY_LEVEL` |
| 기어 | `GEAR_SELECTION` |
| 시동 상태 | `IGNITION_STATE` |
| 외기 온도 | `ENV_OUTSIDE_TEMPERATURE` |

## Pleos Connect 와의 관계

Pleos Connect 는 **AAOS 기반**이며, Pleos 의 **Vehicle SDK** 는 내부적으로 표준 AAOS 의
차량 데이터 API 위에서 동작합니다. 이 프로젝트는 실제 Pleos SDK 대신
**그 토대인 `android.car` 를 직접 사용**해 동일한 개념을 구현했습니다.

자세한 매핑: [`docs/pleos-aaos-mapping.md`](docs/pleos-aaos-mapping.md)

> 실제 Pleos SDK / 에뮬레이터는 Pleos Playground 승인(파트너십 문의)이 필요합니다.
> 자격증명을 얻으면 의존성과 import 만 교체하는 구조로 설계했습니다.

## 프로젝트 구조

```
app/src/main/
├── AndroidManifest.xml                  # 자동차 앱 선언 + 차량 권한
├── java/io/github/jack0928/pleosdashboard/
│   ├── VehicleSignal.kt                 # 표시할 차량 신호 정의 (enum)
│   ├── CarConnectionManager.kt          # Car API 연결 + CarPropertyManager 확보
│   └── MainActivity.kt                  # 화면 + 데이터 조회/갱신 로직
└── res/layout, res/values               # 화면 레이아웃 / 문자열 / 테마
```

## 빌드 & 실행

### 요구사항
- Android Studio (JDK 21 내장)
- Android SDK Platform 36 (또는 `app/build.gradle.kts` 의 `compileSdk` 를 설치된 버전으로 조정)
- **Automotive(AAOS) 에뮬레이터** — 일반 폰 에뮬레이터에서는 동작하지 않음

### 명령줄 빌드
```bash
./gradlew assembleDebug
# 결과: app/build/outputs/apk/debug/app-debug.apk
```

### 에뮬레이터에서 실행
1. Android Studio → **Tools > SDK Manager** 에서 Automotive 시스템 이미지 설치
2. **Device Manager** 에서 Automotive AVD 생성 후 실행
3. 앱 실행 → 차량 권한 허용

차량 데이터 시뮬레이션 및 (실제 Pleos 사용 시) CRN 등록 절차는
[`docs/test-setup.md`](docs/test-setup.md) 참고.

## 문서
- [Pleos ↔ AAOS 매핑](docs/pleos-aaos-mapping.md)
- [테스트 환경 설정 (에뮬레이터 / CRN)](docs/test-setup.md)
- [Kotlin 치트시트 (Java 경험자용)](docs/kotlin-for-java-devs.md)

## 메모

- `registerCallback`/`unregisterCallback` 은 최신 AAOS 에서 deprecated (동작엔 문제 없음) — 추후 `subscribePropertyEvents` 로 현대화 가능
- 비밀키(`secrets.properties`) 는 `.gitignore` 처리되어 저장소에 포함되지 않음 (`secrets.properties.template` 참고)
