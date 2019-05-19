package jp.co.ksrogers.animationswitchingbottomnavigation

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP

@RestrictTo(LIBRARY_GROUP)
class AnimationSwitchingBottomNavigationSelectedButton @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  private val buttonView = ImageView(context, attrs)
  private var ovalDrawable: Drawable? = null
  private var buttonColor: ColorStateList? = null
  @IdRes
  private var menuViewId: Int = 0

  init {
    attrs?.let {
      val a = context.obtainStyledAttributes(
        it,
        R.styleable.AnimationSwitchingBottomNavigationSelectedButton
      )
      buttonColor =
        a.getColorStateList(R.styleable.AnimationSwitchingBottomNavigationSelectedButton_buttonColor)
      menuViewId =
        a.getResourceId(R.styleable.AnimationSwitchingBottomNavigationSelectedButton_menuViewId, 0)
      a.recycle()
    }

    ovalDrawable = resources.getDrawable(
      R.drawable.animation_switching_bottom_navigation_selected_background,
      null
    ).apply {
      setTintList(buttonColor)
    }

    buttonView.apply {
      background = ovalDrawable
    }.also {
      addView(it)
    }
  }

  override fun setBackgroundTintList(tint: ColorStateList?) {
    ovalDrawable?.setTintList(tint)
  }

  fun setOvalTintList(tint: ColorStateList?) {
    ovalDrawable?.setTintList(tint)
  }
}