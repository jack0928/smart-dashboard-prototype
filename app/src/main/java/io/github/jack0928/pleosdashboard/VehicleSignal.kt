package io.github.jack0928.pleosdashboard

import android.car.VehiclePropertyIds

/**
 * 대시보드에 표시할 차량 신호 정의.
 *
 * Pleos Connect 의 "Vehicle SDK" 가 제공하는 차량 상태 항목들을,
 * 표준 AAOS 의 VehiclePropertyIds 상수에 직접 매핑한 것입니다.
 * (예: "차량 속도" -> PERF_VEHICLE_SPEED)
 *
 * [Kotlin 문법] enum class : 정해진 값들의 집합. Java 의 enum 과 같은 개념입니다.
 *   여기서는 각 enum 값이 (이름, 속성ID, 단위) 3개의 데이터를 함께 가집니다.
 */
enum class VehicleSignal(
    val label: String,     // [Kotlin] val = 읽기 전용 변수 (Java 의 final). 한 번 정하면 못 바꿈
    val propertyId: Int,   // 타입을 변수 "뒤"에 적습니다 (Java 와 반대: String label  ->  label: String)
    val unit: String
) {
    SPEED("차량 속도", VehiclePropertyIds.PERF_VEHICLE_SPEED, "m/s"),
    FUEL("연료 잔량", VehiclePropertyIds.FUEL_LEVEL, "mL"),
    EV_BATTERY("EV 배터리", VehiclePropertyIds.EV_BATTERY_LEVEL, "Wh"),
    GEAR("기어", VehiclePropertyIds.GEAR_SELECTION, ""),
    IGNITION("시동 상태", VehiclePropertyIds.IGNITION_STATE, ""),
    OUTSIDE_TEMP("외기 온도", VehiclePropertyIds.ENV_OUTSIDE_TEMPERATURE, "°C");

    /**
     * [Kotlin] companion object : 클래스에 속한 "정적(static) 영역".
     * Java 의 static 메서드 자리라고 보면 됩니다.
     */
    companion object {
        /** 차량에서 받은 속성 ID(Int) 로 어떤 신호인지 거꾸로 찾아주는 헬퍼 */
        fun fromPropertyId(id: Int): VehicleSignal? =
            entries.firstOrNull { it.propertyId == id }
        // [Kotlin] 끝의 ? : 결과가 없을 수도(null) 있음을 타입에 명시
        // entries : enum 의 모든 값 목록 / it : 람다에서 "현재 항목"을 가리키는 기본 이름
    }
}
