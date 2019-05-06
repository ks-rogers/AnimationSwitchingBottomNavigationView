package jp.co.ksrogers.animationswitchingbottomnavigation

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.IdRes

class AnimationSwitchingBottomNavigationSelectedButton @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : ImageButton(context, attrs) {

  private var ovalDrawable: Drawable? = null
  private var buttonColor: ColorStateList? = null
  @IdRes
  private var menuViewId: Int = 0

  // TODO for click check logging
  private val onClickListener = OnClickListener {
    Toast.makeText(context, "クリック", Toast.LENGTH_SHORT).show()
  }

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
    background = ovalDrawable
    setOnClickListener(onClickListener)
  }

  override fun setBackgroundTintList(colorStateList: ColorStateList?) {
    ovalDrawable?.setTintList(colorStateList)
  }
}