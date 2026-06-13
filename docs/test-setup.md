# 테스트 환경 설정

## 1) 표준 AAOS 에뮬레이터 (지금 바로 가능)

이 앱의 차량 데이터 조회 부분(`android.car`)은 일반 Automotive 에뮬레이터에서 동작합니다.

1. Android Studio → **Tools > SDK Manager**
   - **SDK Platforms** 탭: "Automotive" 관련 시스템 이미지 설치 (예: Automotive with Google APIs)
2. **Tools > Device Manager → Create device → Automotive** → AVD 생성 후 실행
3. 앱 실행 → 차량 권한 허용 → 대시보드 확인

### 차량 데이터 시뮬레이션
에뮬레이터의 차량 값(속도/연료 등)은 에뮬레이터 확장 콘솔 또는 `adb` 로 주입할 수 있습니다.
(에뮬레이터별 지원 범위가 다르므로, 지원하지 않는 신호는 앱에서 "N/A" 로 표시됩니다.)

## 2) Pleos Connect Emulator (실제 Pleos SDK 사용 시)

> 시스템 이미지 다운로드 URL 은 공개되지 않으며 `partnership@pleos.ai` 로 요청해야 합니다.

1. **SDK Manager → SDK Update Sites → Add**
   - Name: `Pleos Connect System Image`
   - URL: *(Pleos 에서 받은 비공개 URL)*
2. 시스템 이미지(Pleos Connect, API 34) 설치
3. **Device Manager → Create device → Automotive → Pleos Connect** AVD 생성 → 실행

### CRN 등록 (실행 중인 Pleos 에뮬레이터에 1회)

```bash
adb root
adb shell su 0 "echo 'propId: 554696961 areaId: 0 values: {CRN}' > /data/vendor/vsomeip/vhal_fifo"
adb reboot
```

- `{CRN}` 자리에 `secrets.properties` 의 `PLEOS_CRN` 값을 넣는다.
- `propId: 554696961` 은 CRN 주입 전용 속성 ID (고정).
- 반드시 **에뮬레이터가 실행 중**이어야 한다 (`adb devices` 에 떠야 함).

## 참고: adb 경로
`adb` 는 Android SDK 의 `platform-tools` 에 있습니다.
```bash
export PATH="$PATH:$HOME/Library/Android/sdk/platform-tools"
```
