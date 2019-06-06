package jp.co.ksrogers.animationswitchingbottomnavigation

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.NavigationMenuItem

/**
 *
 * A layout of item in bottom navigation.
 *
 * This layout is very simple, it has only one ImageView.
 * Use drawable resource of [NavigationMenuItem] passed from [initialize] for internal ImageView.
 *
 */
@RestrictTo(LIBRARY_GROUP)
class AnimationSwitchingBottomNavigationItemView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  private val icon: ImageView

  lateinit var item: NavigationMenuItem

  init {
    LayoutInflater.from(context)
      .inflate(R.layout.animation_switching_bottom_navigation_item, this, true)

    layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)

    icon = findViewById(R.id.icon)
  }

  /**
   *
   * Setup with [NavigationMenuItem].
   * In this method, keep argument's menu item internal and set menu icon with it.
   *
   * @param item menu item corresponding to this layout.
   *
   */
  fun initialize(item: NavigationMenuItem) {
    this.item = item
    setIcon(resources.getDrawable(item.iconDrawableRes, null))
  }

  private fun setIcon(drawable: Drawable?) = icon.setImageDrawable(drawable)
}