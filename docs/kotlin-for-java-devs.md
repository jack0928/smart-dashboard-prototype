# Kotlin 치트시트 (Java 경험자용)

이 프로젝트 코드를 읽다가 막힐 때 보는 문법 메모. Java 와 개념은 같고 문법만 다릅니다.

## 변수
```kotlin
val name = "kim"     // 읽기 전용 (Java: final var)
var count = 0        // 변경 가능
val label: String    // 타입은 변수 "뒤"에 (Java: String label)
```

## null 안전성 (Kotlin 의 핵심)
```kotlin
var manager: CarPropertyManager? = null   // ? = null 가능
manager?.disconnect()                      // manager 가 null 이면 호출 안 함 (NPE 방지)
val m = manager ?: return                  // null 이면 그 자리에서 return ("엘비스 연산자")
```

## 함수
```kotlin
fun add(a: Int, b: Int): Int = a + b       // 반환 타입은 뒤에
fun greet() { println("hi") }              // 반환 없으면 생략 (Java void)
```

## 람다 / 콜백
```kotlin
button.setOnClickListener { readAllOnce() }          // { } 가 람다(익명 함수)
list.firstOrNull { it.id == 3 }                      // it = 현재 항목 기본 이름
// 마지막 인자가 람다면 괄호 밖으로 뺄 수 있음
Car.createCar(ctx, handler, timeout) { car, ready -> ... }
```

## when (= 강화된 switch)
```kotlin
val text = when (raw) {
    is Float -> String.format("%.1f", raw)   // 타입으로 분기
    else -> raw.toString()
}
```

## 클래스
```kotlin
class CarConnectionManager(
    context: Context,                         // 생성자 파라미터를 클래스 선언에 바로
    private val onReady: (CarPropertyManager) -> Unit   // 함수를 파라미터로
)

enum class VehicleSignal(val label: String) { SPEED("속도"), GEAR("기어") }

companion object { ... }   // 클래스의 static 영역
```

## 문자열 템플릿
```kotlin
val msg = "속도: $speed m/s"        // 변수 바로 삽입
val msg2 = "에러: ${e.message}"      // 식은 ${ } 로
```

## lateinit
```kotlin
private lateinit var statusText: TextView   // "나중에 초기화" 약속 (onCreate 에서 채움)
```
