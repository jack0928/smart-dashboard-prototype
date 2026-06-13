# Pleos Connect ↔ Android Automotive OS 매핑

Pleos Connect 는 Google 의 **Android Automotive OS(AAOS)** 를 기반으로 구축되었습니다.
Pleos 가 제공하는 확장 SDK 들은 대부분 표준 AAOS API 위에 편의 계층을 얹은 형태입니다.
이 프로젝트는 실제 Pleos SDK 대신 **그 토대가 되는 표준 AAOS API** 를 사용합니다.

## SDK 모듈 대응

| Pleos Connect SDK | 역할 | 대응되는 표준 AAOS / Android API |
|-------------------|------|----------------------------------|
| **Vehicle SDK** | 차량 상태 조회·제어 | `android.car.Car`, `android.car.hardware.property.CarPropertyManager`, `VehiclePropertyIds` |
| **NaviHelper SDK** | 내비 제어/정보 | (앱별) Intent / 내비 앱 연동 API |
| **ADAS SDK** | 주행 보조 정보 | 차량 속성(`CarPropertyManager`) 기반 ADAS 신호 |
| **Gleo AI SDK** | 음성인식/합성, LLM | Android 음성 API + Pleos 전용 AI 서비스 |
| **Fused Location SDK** | 차량 위치 | `FusedLocationProviderClient` (Google Play Services Location) |

## 이 프로젝트에서 실제로 쓴 부분 (Vehicle SDK 대응)

```kotlin
// 1) 차량 서비스 연결  →  Pleos: Vehicle SDK 초기화에 해당
val car = Car.createCar(context, handler, Car.CAR_WAIT_TIMEOUT_WAIT_FOREVER) { car, ready -> ... }

// 2) 데이터 매니저 확보
val manager = car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager

// 3) 차량 속성 읽기  →  Pleos: vehicle.getProperty(...) 류에 해당
val speed = manager.getProperty<Any>(VehiclePropertyIds.PERF_VEHICLE_SPEED, 0).value

// 4) 실시간 구독
manager.registerCallback(callback, propertyId, CarPropertyManager.SENSOR_RATE_ONCHANGE)
```

## 실제 Pleos SDK 로 전환 시 바뀌는 것

1. `app/build.gradle.kts` 의 `dependencies` 에 Pleos SDK 의존성 추가 (Maven 좌표 또는 `.aar`)
2. `import android.car.*` → `import (Pleos SDK 패키지).*`
3. `secrets.properties` 의 `PLEOS_CLIENT_ID` / `PLEOS_CLIENT_SECRET` 로 SDK 인증
4. 테스트는 Pleos Connect Emulator + CRN 등록 ([`test-setup.md`](test-setup.md))

> 구조(연결 → 매니저 → 속성 조회 → 구독)는 동일하므로, SDK 가 들어와도 골격은 그대로 유지됩니다.

## 참고
- 공식 문서: https://document.pleos.ai/
- Pleos Playground: https://pleos.ai/playground
