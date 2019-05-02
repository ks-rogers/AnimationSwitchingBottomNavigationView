package jp.co.ksrogers.animationswitchingbottomnavigation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.SelectedButtonSize
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.SelectedButtonSize.NORMAL
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.SelectedButtonSize.SMALL

/**
 * TODO 矩形の正確な描画は後回しです
 */
@SuppressLint("CustomViewStyleable")
class AnimationSwitchingBottomNavigationSelectedBackgroundView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {

  private val bottomMargin =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_background_bottom_margin)
      .toFloat()

  // TODO 色が固定になっているので、変える
  private val colorRed = ContextCompat.getColor(context, R.color.red)
  private val paintRed = Paint().apply { color = colorRed }

  private val underWartPath = Path()

  private var selectedWidth =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_normal_width)
  private var selectedHeight =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_normal_height)

  init {
    attrs?.let {
      val a = context.obtainStyledAttributes(
        it,
        R.styleable.AnimationSwitchingBottomNavigationLayout
      )

      val selectedButtonSize = SelectedButtonSize.fromIndex(
        a.getInt(
          R.styleable.AnimationSwitchingBottomNavigationLayout_selectedButtonSize,
          0
        )
      )
      when (selectedButtonSize) {
        NORMAL -> {
          selectedWidth =
            resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_normal_width)
          selectedHeight =
            resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_normal_height)
        }
        SMALL -> {
          selectedWidth =
            resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_small_width)
          selectedHeight =
            resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_small_height)
        }
      }

      a.recycle()
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val widthMode = MeasureSpec.getMode(widthMeasureSpec)
    val heightMode = MeasureSpec.getMode(heightMeasureSpec)

    if (widthMode != MeasureSpec.UNSPECIFIED || heightMode != MeasureSpec.EXACTLY) {
      throw IllegalArgumentException("Unexpected measure mode.")
    }

    val width = selectedWidth * 2
    val height = MeasureSpec.getSize(heightMeasureSpec)

    setMeasuredDimension(width, height)
  }

  // TODO 3次べエジェ曲線で描画しないときれいな曲線にならないことがわかったので、置き換える。数学解く必要あり
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val maxWidth = measuredWidth.toFloat()
    val figureHeight = measuredHeight - bottomMargin

    // スタート地点を移動
    underWartPath.moveTo(0F, 0F)
    // 左上から矩形の中心下までのベジェ曲線
    underWartPath.cubicTo(
      maxWidth / 8,
      0F,
      maxWidth / 4,
      figureHeight,
      3 * maxWidth / 8,
      figureHeight
    )
    // 下部に直線を入れる
    underWartPath.lineTo(5 * maxWidth / 8, figureHeight)
    // 矩形中心下から右上までのベジェ曲線
    underWartPath.cubicTo(
      3 * maxWidth / 4,
      figureHeight,
      7 * maxWidth / 8,
      0F,
      maxWidth,
      0F
    )
    underWartPath.lineTo(0F, 0F)
    canvas.clipPath(underWartPath)

    canvas.drawPaint(paintRed)
  }
}