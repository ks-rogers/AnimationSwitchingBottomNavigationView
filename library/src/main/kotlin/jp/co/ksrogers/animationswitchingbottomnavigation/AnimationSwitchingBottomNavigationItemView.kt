package jp.co.ksrogers.animationswitchingbottomnavigation

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.NavigationMenu

class AnimationSwitchingBottomNavigationItemView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  private val icon: ImageView

  lateinit var navigationItem: NavigationMenu

  init {
    LayoutInflater.from(context)
      .inflate(R.layout.animation_switching_bottom_navigation_item, this, true)

    layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)

    icon = findViewById(R.id.icon)
  }

  fun initialize(navigationItem: NavigationMenu) {
    this.navigationItem = navigationItem
    setIcon(resources.getDrawable(navigationItem.iconDrawableRes, null))
  }

  private fun setIcon(drawable: Drawable?) = icon.setImageDrawable(drawable)
}