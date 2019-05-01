package jp.co.ksrogers.animationswitchingbottomnavigation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

/**
 * TODO 矩形の正確な描画は後回しです
 */
class UnderWartView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {

  private val bottomMargin =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_under_wart_bottom_margin)
      .toFloat()

  private val colorRed = ContextCompat.getColor(context, R.color.red)
  private val paintRed = Paint().apply { color = colorRed }

  private val underWartPath = Path()

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val maxWidth = measuredWidth.toFloat()
    val figureHeight = measuredHeight - bottomMargin
    val tan = 0F
//    // スタート地点を移動
    underWartPath.moveTo(0F, 0F)
//    // 制御点1 X, 制御点1 Y, 制御点2 X, 制御点2Y, 終点X, 終点Y
//    underWartPath.cubicTo(
//      maxWidth / 6,
//      0F,
//      maxWidth / 4 - maxWidth / 12,
//      figureHeight / 2 - figureHeight / 6,
//      maxWidth / 4,
//      figureHeight / 2
//    )
//    underWartPath.cubicTo(
//      maxWidth / 4 + maxWidth / 12,
//      figureHeight / 2 + figureHeight / 6,
//      maxWidth / 2 - maxWidth / 6,
//      figureHeight,
//      maxWidth / 2,
//      figureHeight
//    )
//    underWartPath.cubicTo(
//      maxWidth / 2 + maxWidth / 6,
//      figureHeight,
//      3 * maxWidth / 4 - maxWidth / 12,
//      figureHeight / 2 + figureHeight / 6,
//      3 * maxWidth / 4,
//      figureHeight / 2
//    )
//    underWartPath.cubicTo(
//      3 * maxWidth / 4 + maxWidth / 12,
//      figureHeight / 2 - figureHeight / 6,
//      5 * maxWidth / 6,
//      0F,
//      maxWidth,
//      0F
//    )
    // 左上から矩形の中心下までのベジェ曲線
    underWartPath.cubicTo(
      maxWidth / 6,
      figureHeight / 12,
      maxWidth / 12,
      figureHeight,
      maxWidth / 2,
      figureHeight
    )
    // 矩形中心下から右上までのベジェ曲線
    underWartPath.cubicTo(
      11 * maxWidth / 12,
      figureHeight,
      5 * maxWidth / 6,
      figureHeight / 12,
      maxWidth,
      0F
    )
    underWartPath.lineTo(0F, 0F)

//    underWartPath.cubicTo(maxWidth/6F, maxHeight/6f)
    canvas.clipPath(underWartPath)

    canvas.drawPaint(paintRed)
  }
}