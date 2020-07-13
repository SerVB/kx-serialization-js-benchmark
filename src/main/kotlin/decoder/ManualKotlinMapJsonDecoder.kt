package decoder

import protocol.*
import protocol.data.*
import kotlin.js.Json
import kotlin.math.roundToLong

object ManualKotlinMapJsonDecoder : Decoder {

  override fun decode(string: String): List<ServerEvent> {
    val jsonArray = JSON.parse<Array<Array<Any>>>(string)
    return jsonArray.map { it.toEvent() }
  }

  private val seMap = mapOf<String, (content: Json) -> ServerEvent>(
    "a" to { content ->
      ServerImageDataReplyEvent(
        content["a"].unsafeCast<Array<Any>>().toImageId(),
        content["b"].unsafeCast<Array<Any>>().toImageData()
      )
    },
    "b" to { content -> ServerPingReplyEvent(content["a"] as Int, content["b"] as Int) },
    "c" to { content -> ServerClipboardEvent(content["a"] as String) },
    "d" to { content -> ServerWindowSetChangedEvent(content["a"].unsafeCast<Array<Json>>().map { it.toWindowData() }) },
    "e" to { content ->
      ServerDrawCommandsEvent(
        content["a"].unsafeCast<Array<Any>>().toTarget(),
        content["b"].unsafeCast<Array<Array<Any>>>().map { it.toWindowEvent() }
      )
    },
    "f" to { content -> ServerCaretInfoChangedEvent(content["a"].unsafeCast<Array<Any>>().toCaretInfoChange()) },
    "g" to { content -> ServerMarkdownEvent.ServerMarkdownShowEvent(content["a"] as Int, content["b"] as Boolean) },
    "h" to { content ->
      ServerMarkdownEvent.ServerMarkdownResizeEvent(
        content["a"] as Int,
        content["b"].unsafeCast<Json>().toCommonIntSize()
      )
    },
    "i" to { content -> ServerMarkdownEvent.ServerMarkdownMoveEvent(content["a"] as Int, content["b"].unsafeCast<Json>().toPoint()) },
    "j" to { content -> ServerMarkdownEvent.ServerMarkdownDisposeEvent(content["a"] as Int) },
    "k" to { content -> ServerMarkdownEvent.ServerMarkdownPlaceToWindowEvent(content["a"] as Int, content["b"] as Int) },
    "l" to { content -> ServerMarkdownEvent.ServerMarkdownSetHtmlEvent(content["a"] as Int, content["b"] as String) },
    "m" to { content -> ServerMarkdownEvent.ServerMarkdownSetCssEvent(content["a"] as Int, content["b"] as String) },
    "n" to { content -> ServerMarkdownEvent.ServerMarkdownScrollEvent(content["a"] as Int, content["b"] as Int) },
    "o" to { content -> ServerMarkdownEvent.ServerMarkdownBrowseUriEvent(content["a"] as String) },
    "p" to { content -> ServerWindowColorsEvent() }  // todo: decode map
  )

  private fun Array<Any>.toEvent(): ServerEvent {
    val type = this[0] as String
    val content = this[1].unsafeCast<Json>()

    return seMap[type]?.invoke(content) ?: throw IllegalArgumentException("Unsupported event type: ${JSON.stringify(this)}")
  }

  private fun Array<Any>.toCaretInfoChange(): ServerCaretInfoChangedEvent.CaretInfoChange {
    val type = this[0] as String
    val content = this[1].unsafeCast<Json>()

    return when (type) {
      "a" -> ServerCaretInfoChangedEvent.CaretInfoChange.NoCarets
      "b" -> ServerCaretInfoChangedEvent.CaretInfoChange.Carets(
        content["a"].unsafeCast<Array<Json>>().map { it.toCaretInfo() },
        content["b"] as Short?,
        content["c"] as Int,
        content["d"] as Int,
        content["e"] as Float,
        content["f"] as Int,
        content["g"].unsafeCast<Json>().toCommonRectangle()
      )
      else -> throw IllegalArgumentException("Unsupported caret info type: ${JSON.stringify(this)}")
    }
  }

  private fun Json.toCaretInfo(): CaretInfo {
    return CaretInfo(this["a"].unsafeCast<Json>().toPoint())
  }

  private fun Array<Any>.toTarget(): ServerDrawCommandsEvent.Target {
    val type = this[0] as String
    val content = this[1].unsafeCast<Json>()

    return when (type) {
      "a" -> ServerDrawCommandsEvent.Target.Onscreen(content["a"] as Int)
      "b" -> ServerDrawCommandsEvent.Target.Offscreen(
        (content["a"] as Double).roundToLong(), content["b"] as Int,
        content["c"] as Int
      )  // todo: is it a correct way of decoding Long?
      else -> throw IllegalArgumentException("Unsupported target type: ${JSON.stringify(this)}")
    }
  }

  private fun Json.toCommonIntSize(): CommonIntSize {
    return CommonIntSize(this["a"] as Int, this["b"] as Int)
  }

  private fun Json.toWindowData(): WindowData {
    return WindowData(
      this["a"] as Int,
      this["b"] as String?,
      this["c"].unsafeCast<Array<Array<Any>>>().map { it.toImageId() },
      this["d"] as Boolean,
      this["e"] as Int,
      this["f"].unsafeCast<Json>().toCommonRectangle(),
      (this["g"] as String?)?.toCursorType(),
      this["h"] as Boolean,
      this["i"] as Boolean,
      this["j"] as Boolean,
      (this["k"] as String).toWindowType(),
      this["l"] as Int?
    )
  }

  private fun String.toWindowType(): WindowType {
    return when (this) {
      "a" -> WindowType.WINDOW
      "b" -> WindowType.POPUP
      "c" -> WindowType.IDEA_WINDOW
      else -> throw IllegalArgumentException("Unsupported window type: $this")
    }
  }

  private val cursorTypes = mapOf(
    "a" to CursorType.DEFAULT,
    "b" to CursorType.CROSSHAIR,
    "c" to CursorType.TEXT,
    "d" to CursorType.WAIT,
    "e" to CursorType.SW_RESIZE,
    "f" to CursorType.SE_RESIZE,
    "g" to CursorType.NW_RESIZE,
    "h" to CursorType.NE_RESIZE,
    "i" to CursorType.N_RESIZE,
    "j" to CursorType.S_RESIZE,
    "k" to CursorType.W_RESIZE,
    "l" to CursorType.E_RESIZE,
    "m" to CursorType.HAND,
    "n" to CursorType.MOVE
  )

  private fun String.toCursorType(): CursorType {
    return cursorTypes[this] ?: throw IllegalArgumentException("Unsupported cursor type: $this")
  }

  private fun Json.toCommonRectangle(): CommonRectangle {
    return CommonRectangle(this["a"] as Double, this["b"] as Double, this["c"] as Double, this["d"] as Double)
  }

  private val sweMap = mapOf<String, (content: Json) -> ServerWindowEvent>(
    "a" to { content ->
      ServerPaintArcEvent(
        (content["a"] as String).toPaintType(),
        content["b"] as Int,
        content["c"] as Int,
        content["d"] as Int,
        content["e"] as Int,
        content["f"] as Int,
        content["g"] as Int
      )
    },

    "b" to { content ->
      ServerPaintOvalEvent(
        (content["a"] as String).toPaintType(),
        content["b"] as Int,
        content["c"] as Int,
        content["d"] as Int,
        content["e"] as Int
      )
    },

    "c" to { content ->
      ServerPaintRoundRectEvent(
        (content["a"] as String).toPaintType(),
        content["b"] as Int,
        content["c"] as Int,
        content["d"] as Int,
        content["e"] as Int,
        content["f"] as Int,
        content["g"] as Int
      )
    },

    "d" to { content ->
      ServerPaintRectEvent(
        (content["a"] as String).toPaintType(),
        content["b"] as Double,
        content["c"] as Double,
        content["d"] as Double,
        content["e"] as Double
      )
    },

    "e" to { content ->
      ServerDrawLineEvent(
        content["a"] as Int,
        content["b"] as Int,
        content["c"] as Int,
        content["d"] as Int
      )
    },

    "f" to { content ->
      ServerCopyAreaEvent(
        content["a"] as Int,
        content["b"] as Int,
        content["c"] as Int,
        content["d"] as Int,
        content["e"] as Int,
        content["f"] as Int
      )
    },

    "g" to { content -> ServerSetFontEvent(content["a"] as Short, content["b"] as Int) },

    "h" to { content -> ServerSetClipEvent(content["a"].unsafeCast<Array<Any>?>()?.toCommonShape()) },

    "i" to { content -> ServerSetStrokeEvent(content["a"].unsafeCast<Array<Any>>().toStrokeData()) },

    "j" to { content -> ServerDrawRenderedImageEvent },

    "k" to { content -> ServerDrawRenderableImageEvent },

    "l" to { content ->
      ServerDrawImageEvent(
        content["a"].unsafeCast<Array<Any>>().toImageId(),
        content["b"].unsafeCast<Array<Any>>().toImageEventInfo()
      )
    },

    "m" to { content ->
      ServerDrawStringEvent(
        content["a"] as String,
        content["b"] as Double,
        content["c"] as Double,
        content["d"] as Double
      )
    },

    "n" to { content ->
      ServerPaintPolygonEvent(
        (content["a"] as String).toPaintType(),
        content["b"].unsafeCast<Array<Json>>().map { it.toPoint() })
    },

    "o" to { content -> ServerDrawPolylineEvent(content["a"].unsafeCast<Array<Json>>().map { it.toPoint() }) },

    "p" to { content -> ServerSetTransformEvent(content["a"].unsafeCast<Array<Double>>().toList()) },

    "q" to { content -> ServerPaintPathEvent((content["a"] as String).toPaintType(), content["b"].unsafeCast<Json>().toCommonPath()) },

    "r" to { content -> ServerSetCompositeEvent(content["a"].unsafeCast<Array<Any>>().toCommonComposite()) },

    "s" to { content -> ServerSetPaintEvent(content["a"].unsafeCast<Array<Any>>().toPaintValue()) },

    "t" to { content -> ServerSetUnknownStrokeEvent(content["a"] as String) }
  )

  private fun Array<Any>.toWindowEvent(): ServerWindowEvent {
    val type = this[0] as String
    val content = this[1].unsafeCast<Json>()

    return sweMap[type]?.invoke(content) ?: throw IllegalArgumentException("Unsupported event type: ${JSON.stringify(this)}")
  }

  private val alphaCompositeRuleMap = mapOf(
    "a" to AlphaCompositeRule.SRC_OVER,
    "b" to AlphaCompositeRule.DST_OVER,
    "c" to AlphaCompositeRule.SRC_IN,
    "d" to AlphaCompositeRule.CLEAR,
    "e" to AlphaCompositeRule.SRC,
    "f" to AlphaCompositeRule.DST,
    "g" to AlphaCompositeRule.DST_IN,
    "h" to AlphaCompositeRule.SRC_OUT,
    "i" to AlphaCompositeRule.DST_OUT,
    "j" to AlphaCompositeRule.SRC_ATOP,
    "k" to AlphaCompositeRule.DST_ATOP,
    "l" to AlphaCompositeRule.XOR
  )

  private fun Array<Any>.toCommonComposite(): CommonComposite {
    val type = this[0] as String
    val content = this[1].unsafeCast<Json>()

    return when (type) {
      "a" -> CommonAlphaComposite(
        alphaCompositeRuleMap[content["a"] as String] ?: throw IllegalArgumentException("Unsupported rule: ${content["a"]}"),
        content["b"] as Float
      )
      "b" -> UnknownComposite(content["a"] as String)
      else -> throw IllegalArgumentException("Unsupported common composite type: ${JSON.stringify(this)}")
    }
  }

  private fun String.toPaintType(): PaintType {
    return when (this) {
      "a" -> PaintType.DRAW
      "b" -> PaintType.FILL
      else -> throw IllegalArgumentException("Unsupported paint type: $this")
    }
  }

  private fun Array<Any>.toPaintValue(): PaintValue {
    val type = this[0] as String
    val content = this[1].unsafeCast<Json>()

    return when (type) {
      "a" -> PaintValue.Color(content["a"] as Int)
      "b" -> PaintValue.Gradient(
        content["a"].unsafeCast<Json>().toPoint(), content["b"].unsafeCast<Json>().toPoint(),
        content["c"] as Int, content["d"] as Int
      )
      "c" -> PaintValue.Unknown(content["a"] as String)
      else -> throw IllegalArgumentException("Unsupported paint value type: ${JSON.stringify(this)}")
    }
  }

  private fun Array<Any>.toImageEventInfo(): ImageEventInfo {
    val type = this[0] as String
    val content = this[1].unsafeCast<Json>()

    return when (type) {
      "a" -> ImageEventInfo.Xy(content["a"] as Int, content["b"] as Int, content["c"] as Int?)
      "b" -> ImageEventInfo.XyWh(
        content["a"] as Int,
        content["b"] as Int,
        content["c"] as Int,
        content["d"] as Int,
        content["e"] as Int?
      )
      "c" -> ImageEventInfo.Ds(
        content["a"] as Int,
        content["b"] as Int,
        content["c"] as Int,
        content["d"] as Int,
        content["e"] as Int,
        content["f"] as Int,
        content["g"] as Int,
        content["h"] as Int,
        content["i"] as Int?
      )
      "d" -> ImageEventInfo.Transformed(content["a"].unsafeCast<Array<Double>>().toList())
      else -> throw IllegalArgumentException("Unsupported image info type: ${JSON.stringify(this)}")
    }
  }

  private fun Array<Any>.toStrokeData(): StrokeData {
    val thisType = this[0] as String
    val content = this[1].unsafeCast<Json>()

    return when (thisType) {
      "a" -> StrokeData.Basic(
        content["a"] as Float,
        when (val type = content["b"] as String) {
          "a" -> StrokeData.Basic.JoinType.MITER
          "b" -> StrokeData.Basic.JoinType.ROUND
          "c" -> StrokeData.Basic.JoinType.BEVEL
          else -> throw IllegalArgumentException("Unsupported join type: $type")
        },
        when (val type = content["c"] as String) {
          "a" -> StrokeData.Basic.CapType.BUTT
          "b" -> StrokeData.Basic.CapType.ROUND
          "c" -> StrokeData.Basic.CapType.SQUARE
          else -> throw IllegalArgumentException("Unsupported cap type: $type")
        },
        content["d"] as Float,
        content["e"] as Float,
        content["f"].unsafeCast<Array<Float>?>()?.toList()
      )

      else -> throw IllegalArgumentException("Unsupported stroke type: ${JSON.stringify(this)}")
    }
  }

  private fun Array<Any>.toImageId(): ImageId {
    val type = this[0] as String
    val content = this[1].unsafeCast<Json>()

    return when (type) {
      "a" -> ImageId.BufferedImageId(content["a"] as Int, content["b"] as Int)
      "b" -> ImageId.PVolatileImageId((content["a"] as Double).roundToLong())  // todo: is it a correct way?
      else -> throw IllegalArgumentException("Invalid image id type: ${JSON.stringify(this)}")
    }
  }

  private fun Array<Any>.toImageData(): ImageData {
    val type = this[0] as String
    val content = this[1].unsafeCast<Json>()

    return when (type) {
      "a" -> ImageData.PngBase64(content["a"] as String)
      "b" -> ImageData.Empty
      else -> throw IllegalArgumentException("Invalid image data type: $${JSON.stringify(this)}")
    }
  }

  private fun Array<Any>.toCommonShape(): CommonShape {
    val type = this[0] as String
    val content = this[1].unsafeCast<Json>()

    return when (type) {
      "a" -> content.toCommonRectangle()
      "b" -> content.toCommonPath()
      else -> throw IllegalArgumentException("Unsupported common shape: ${JSON.stringify(this)}")
    }
  }

  private fun Json.toCommonPath(): CommonPath {
    val segments = this["a"].unsafeCast<Array<Array<Any>>>().map { it.toPathSegment() }
    val winding = when (val type = this["b"] as String) {
      "a" -> CommonPath.WindingType.EVEN_ODD
      "b" -> CommonPath.WindingType.NON_ZERO
      else -> throw IllegalArgumentException("Invalid winding type: $type")
    }

    return CommonPath(segments, winding)
  }

  private fun Array<Any>.toPathSegment(): PathSegment {
    val type = this[0] as String
    val content = this[1].unsafeCast<Json>()

    return when (type) {
      "a" -> PathSegment.MoveTo(content["a"].unsafeCast<Json>().toPoint())
      "b" -> PathSegment.LineTo(content["a"].unsafeCast<Json>().toPoint())
      "c" -> PathSegment.QuadTo(content["a"].unsafeCast<Json>().toPoint(), content["b"].unsafeCast<Json>().toPoint())
      "d" -> PathSegment.CubicTo(
        content["a"].unsafeCast<Json>().toPoint(),
        content["b"].unsafeCast<Json>().toPoint(),
        content["c"].unsafeCast<Json>().toPoint()
      )
      "e" -> PathSegment.Close
      else -> throw IllegalArgumentException("Unsupported path segment: ${JSON.stringify(this)}")
    }
  }

  private fun Json.toPoint(): Point {
    return Point(this["a"] as Double, this["b"] as Double)
  }
}
