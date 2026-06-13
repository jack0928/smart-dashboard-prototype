package io.github.jack0928.pleosdashboard

import android.car.Car
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

/**
 * Car API 연결과 CarPropertyManager 확보를 담당하는 클래스.
 *
 * 개념 흐름 (Pleos 의 Vehicle SDK 초기화와 동일):
 *   1) 차량 서비스(Car service)에 연결한다
 *   2) 연결되면 CarPropertyManager 를 받아온다  <- 차량 데이터를 읽는 "리모컨"
 *   3) 끊기면 알려준다
 *
 * [Kotlin 문법] 생성자 파라미터를 클래스 선언 괄호 안에 바로 적습니다.
 *   onReady, onDisconnected 는 "함수를 담는 변수"입니다 (콜백). 연결 결과를 바깥에 알려줄 때 호출.
 */
class CarConnectionManager(
    context: Context,
    private val onReady: (CarPropertyManager) -> Unit,  // CarPropertyManager 를 받아 아무것도 반환 안 하는 함수
    private val onDisconnected: () -> Unit
) {
    private val tag = "PleosDashboard"

    // Car.createCar : 차량 서비스에 연결을 시도.
    // 마지막 { car, ready -> ... } 가 "연결 상태가 바뀔 때마다" 호출되는 콜백입니다.
    // [Kotlin] 함수의 마지막 인자가 람다면 괄호 밖으로 빼서 { } 로 쓸 수 있습니다.
    private val car: Car = Car.createCar(
        context,
        Handler(Looper.getMainLooper()),            // 콜백을 메인(UI) 스레드에서 받겠다는 의미
        Car.CAR_WAIT_TIMEOUT_WAIT_FOREVER
    ) { car, ready ->
        if (ready) {
            // 연결됨 -> 차량 데이터를 읽는 매니저를 꺼낸다
            val manager = car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
            Log.i(tag, "Car service connected")
            onReady(manager)
        } else {
            Log.w(tag, "Car service disconnected")
            onDisconnected()
        }
    }

    /** 화면이 종료될 때 연결을 정리 (자원 누수 방지) */
    fun disconnect() {
        if (car.isConnected) car.disconnect()
    }
}
