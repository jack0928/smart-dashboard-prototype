package io.github.jack0928.pleosdashboard

import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * 앱의 메인 화면.
 *
 * 전체 흐름:
 *   1) 화면(UI) 구성
 *   2) 차량 데이터 권한 요청
 *   3) 차량 서비스에 연결 (CarConnectionManager)
 *   4) 연결되면 6개 신호를 읽어 화면에 표시 + 값이 바뀌면 실시간 갱신
 */
class MainActivity : AppCompatActivity() {

    private val tag = "PleosDashboard"
    private val reqCarPerms = 1001

    // 매니페스트에 선언한 차량 권한들. 실행 중에 사용자에게 허용을 요청합니다.
    private val carPermissions = arrayOf(
        "android.car.permission.CAR_SPEED",
        "android.car.permission.CAR_ENERGY",
        "android.car.permission.CAR_POWERTRAIN",
        "android.car.permission.CAR_EXTERIOR_ENVIRONMENT"
    )

    // [Kotlin] var = 바꿀 수 있는 변수 / ? = null 가능.
    private var carConnection: CarConnectionManager? = null
    private var propertyManager: CarPropertyManager? = null

    // [Kotlin] lateinit = "나중에 초기화하겠다"는 약속 (onCreate 에서 채움)
    private lateinit var statusText: TextView
    private lateinit var signalContainer: LinearLayout

    // 각 신호별 "값 표시용 TextView" 를 보관 (나중에 값만 갈아끼우기 위해)
    private val valueViews = HashMap<VehicleSignal, TextView>()

    // 값이 실시간으로 바뀔 때 시스템이 호출해주는 콜백
    private val propertyCallback = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<*>) {
            // 콜백은 별도 스레드에서 올 수 있으므로, UI 변경은 메인 스레드에서
            runOnUiThread { updateValue(value.propertyId, value.value) }
        }

        override fun onErrorEvent(propertyId: Int, zone: Int) {
            Log.w(tag, "property error: $propertyId")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buildUi()

        // 자동차(AAOS) 환경이 아니면 안내만 표시
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
            statusText.text = "⚠️ Automotive(AAOS) 기기가 아닙니다. Automotive 에뮬레이터에서 실행하세요."
        }

        requestCarPermissions()
        connectToCar()
    }

    /** 화면 요소를 찾고, 신호 6개의 행을 동적으로 만들어 붙인다 */
    private fun buildUi() {
        statusText = findViewById(R.id.statusText)
        signalContainer = findViewById(R.id.signalContainer)

        // [Kotlin] VehicleSignal.entries = enum 의 모든 값. for 한 줄로 6개 순회.
        for (signal in VehicleSignal.entries) {
            val row = layoutInflater.inflate(R.layout.row_signal, signalContainer, false) as LinearLayout
            row.findViewById<TextView>(R.id.rowLabel).text = signal.label
            val valueView = row.findViewById<TextView>(R.id.rowValue)
            valueView.text = "—"
            valueViews[signal] = valueView
            signalContainer.addView(row)
        }

        findViewById<Button>(R.id.refreshButton).setOnClickListener { readAllOnce() }
    }

    /** 차량 서비스에 연결 시도 */
    private fun connectToCar() {
        try {
            carConnection = CarConnectionManager(
                context = this,
                onReady = { manager ->
                    propertyManager = manager
                    statusText.text = "✅ 차량 서비스 연결됨"
                    registerCallbacks()
                    readAllOnce()
                },
                onDisconnected = {
                    propertyManager = null
                    statusText.text = "❌ 차량 서비스 연결 끊김"
                }
            )
        } catch (e: Exception) {
            Log.e(tag, "Car 연결 실패", e)
            statusText.text = "Car API 연결 실패: ${e.message}"
        }
    }

    /** 6개 신호의 실시간 변경 콜백을 등록 (권한 없는 신호는 건너뜀) */
    private fun registerCallbacks() {
        val manager = propertyManager ?: return
        for (signal in VehicleSignal.entries) {
            try {
                manager.registerCallback(
                    propertyCallback,
                    signal.propertyId,
                    CarPropertyManager.SENSOR_RATE_ONCHANGE
                )
            } catch (e: SecurityException) {
                Log.w(tag, "${signal.label}: 권한 없음 — 콜백 미등록")
            } catch (e: Exception) {
                Log.w(tag, "${signal.label}: 콜백 등록 실패 - ${e.message}")
            }
        }
    }

    /** 6개 신호를 지금 즉시 한 번씩 읽어온다 (새로고침 버튼) */
    private fun readAllOnce() {
        val manager = propertyManager
        if (manager == null) {
            statusText.text = "아직 차량 서비스에 연결되지 않았습니다."
            return
        }
        for (signal in VehicleSignal.entries) {
            try {
                val prop = manager.getProperty<Any>(signal.propertyId, 0)
                updateValue(signal.propertyId, prop.value)
            } catch (e: SecurityException) {
                valueViews[signal]?.text = "권한 필요"
            } catch (e: Exception) {
                // 이 에뮬레이터/차량이 지원하지 않는 신호일 수 있음
                valueViews[signal]?.text = "N/A"
            }
        }
    }

    /** 속성 ID 와 값을 받아, 해당 신호 행의 값 텍스트를 갱신 */
    private fun updateValue(propertyId: Int, raw: Any?) {
        val signal = VehicleSignal.fromPropertyId(propertyId) ?: return
        valueViews[signal]?.text = format(signal, raw)
    }

    /** 값 보기 좋게 변환 (소수점/단위 처리) */
    private fun format(signal: VehicleSignal, raw: Any?): String {
        if (raw == null) return "—"
        // 기어/시동은 정수 enum 값. 학습용으로 원시 값을 그대로 보여줍니다.
        // [Kotlin] when = Java 의 switch 강화판. 값의 타입으로 분기 가능.
        val text = when (raw) {
            is Float -> String.format("%.1f", raw)
            else -> raw.toString()
        }
        return if (signal.unit.isEmpty()) text else "$text ${signal.unit}"
    }

    /** 아직 허용되지 않은 차량 권한을 사용자에게 요청 */
    private fun requestCarPermissions() {
        val notGranted = carPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGranted.toTypedArray(), reqCarPerms)
        }
    }

    /** 권한 응답이 오면 다시 읽기 시도 */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == reqCarPerms) {
            readAllOnce()
        }
    }

    /** 화면 종료 시 콜백 해제 + 연결 정리 (자원 누수 방지) */
    override fun onDestroy() {
        super.onDestroy()
        try {
            propertyManager?.unregisterCallback(propertyCallback)
        } catch (e: Exception) {
            // 이미 연결이 끊겼으면 무시
        }
        carConnection?.disconnect()
    }
}
