package jp.co.ksrogers.animationswitchingbottomnavigation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.SelectedButtonSize
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.SelectedButtonSize.NORMAL
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.SelectedButtonSize.SMALL

/**
 * 選択状態のボタンの後ろの矩形を持ったView
 */
@RestrictTo(LIBRARY_GROUP)
@SuppressLint("CustomViewStyleable")
class AnimationSwitchingBottomNavigationSelectedBackgroundView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : View(context, attrs) {

  private val bottomMargin =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_background_bottom_margin)
      .toFloat()

  @ColorInt
  var color: Int = 0
    set(@ColorInt value) {
      field = value
      paint = Paint().apply { color = field }
      invalidate()
    }
  private var paint =
    Paint().apply { color = this@AnimationSwitchingBottomNavigationSelectedBackgroundView.color }

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

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val maxWidth = measuredWidth.toFloat()
    val figureHeight = measuredHeight - bottomMargin

    underWartPath.cubicTo(
      maxWidth / 4,
      0F,
      maxWidth / 8,
      figureHeight,
      maxWidth / 2,
      figureHeight
    )
    underWartPath.cubicTo(
      7 * maxWidth / 8,
      figureHeight,
      3 * maxWidth / 4,
      0F,
      maxWidth,
      0F
    )
    underWartPath.lineTo(0F, 0F)
    canvas.clipPath(underWartPath)

    canvas.drawPaint(paint)
  }
}