import decoder.*
import jsonData.DataJson200KB
import kotlinx.html.button
import kotlinx.html.dom.append
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import kotlin.browser.document
import kotlin.browser.window

private inline fun measureTimeMs(crossinline block: () -> Unit): Double {
  val startMs = window.performance.now()
  block()
  val endMs = window.performance.now()
  return endMs - startMs
}

fun addResult(place: HTMLElement, repetitions: Int, decoder: Decoder, jsonData: String, name: String): (Event) -> Unit = {
  // warm-up:
  repeat(repetitions) {
    val timeMs = measureTimeMs {
      decoder.decode(jsonData)
    }
  }

  window.setTimeout(
    handler = {
      val times = mutableListOf<Double>()

      repeat(repetitions) {
        val timeMs = measureTimeMs {
          decoder.decode(jsonData)
        }
        times.add(timeMs)
      }

      val avg = times.sum() / times.size
      place.append.div { +"$name: avg = $avg ms, (min, max) = (${times.min()} ms, ${times.max()} ms)" }
    },
    timeout = 1000
  )
}

fun main() {
  val repetitions = 10

  document.body!!.append.div {
    button {
      onClickFunction = addResult(document.body!!, repetitions, KotlinxJsonDecoder, DataJson200KB.data, "kotlinx 200KB")
      +"kotlinx"
    }
    button {
      onClickFunction = addResult(document.body!!, repetitions, KotlinxDopJsonDecoder, DataJson200KB.data, "kotlinx DOP 200KB")
      +"kotlinx DynamicObjectParser"
    }
    button {
      onClickFunction = addResult(document.body!!, repetitions, ManualWhenJsonDecoder, DataJson200KB.data, "manual when 200KB")
      +"manual when"
    }
    button {
      onClickFunction = addResult(document.body!!, repetitions, ManualKotlinMapJsonDecoder, DataJson200KB.data, "manual kotlin map 200KB")
      +"manual kotlin map"
    }
    button {
      onClickFunction = addResult(document.body!!, repetitions, ManualKotlinMapJsonDecoder, DataJson200KB.data, "manual js map 200KB")
      +"manual js map"
    }
  }
}
