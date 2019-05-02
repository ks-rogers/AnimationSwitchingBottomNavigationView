package jp.co.ksrogers.animationswitchingbottomnavigation

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import jp.co.ksrogers.animationswitchingbottomnavigation.internal.MenuItem

class AnimationSwitchingBottomNavigationItemView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  private val icon: ImageView

  lateinit var itemData: MenuItem

  init {
    LayoutInflater.from(context)
      .inflate(R.layout.animation_switching_bottom_navigation_item, this, true)

    layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)

    icon = findViewById(R.id.icon)
  }

  fun initialize(menuItem: MenuItem) {
    this.itemData = menuItem
    setIcon(itemData.iconDrawable)
  }

  private fun setIcon(drawable: Drawable?) = icon.setImageDrawable(drawable)

  fun setChecked(isChecked: Boolean) {
    itemData.isChecked = isChecked
  }
}