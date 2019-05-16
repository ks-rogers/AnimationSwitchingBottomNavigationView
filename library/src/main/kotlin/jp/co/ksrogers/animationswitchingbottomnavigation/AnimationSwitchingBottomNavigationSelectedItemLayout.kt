package jp.co.ksrogers.animationswitchingbottomnavigation

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.NavigationMenuItem
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.OnNavigationMenuItemReselectedListener
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.OnNavigationMenuItemSelectedListener

/**
 * [AnimationSwitchingBottomNavigationLayout]において、選択中のmenu itemの位置に表示されるLayout
 */
class AnimationSwitchingBottomNavigationSelectedItemLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs),
  OnNavigationMenuItemSelectedListener,
  OnNavigationMenuItemReselectedListener {

  private var onNavigationMenuItemSelectedListener: OnNavigationMenuItemSelectedListener? = null
  private var onNavigationMenuItemReselectedListener: OnNavigationMenuItemReselectedListener? = null

  init {
    attrs?.let {
      val a = context.obtainStyledAttributes(
        it,
        R.styleable.AnimationSwitchingBottomNavigationSelectedItemLayout
      )

      val useRoundedBackground = a.getBoolean(
        R.styleable.AnimationSwitchingBottomNavigationSelectedItemLayout_useRoundedBackground,
        false
      )
      val roundedBackgroundColor = a.getColor(
        R.styleable.AnimationSwitchingBottomNavigationSelectedItemLayout_roundedBackgroundColor,
        Color.WHITE
      )

      if (useRoundedBackground) {
        val buttonColor = ColorStateList.valueOf(roundedBackgroundColor)
        val ovalDrawable = resources.getDrawable(
          R.drawable.animation_switching_bottom_navigation_selected_background,
          null
        ).apply {
          setTintList(buttonColor)
        }
        background = ovalDrawable
      }

      a.recycle()
    }
  }

  override fun onNavigationItemSelected(item: NavigationMenuItem) {
    onNavigationMenuItemSelectedListener?.onNavigationItemSelected(item)
  }

  override fun onNavigationItemReselected(item: NavigationMenuItem) {
    onNavigationMenuItemReselectedListener?.onNavigationItemReselected(item)
  }

  @Suppress("unused")
  fun setOnNavigationItemSelectedListener(l: OnNavigationMenuItemSelectedListener) {
    onNavigationMenuItemSelectedListener = l
  }

  @Suppress("unused")
  fun setOnNavigationItemSelectedListener(l: (NavigationMenuItem) -> Unit) {
    setOnNavigationItemSelectedListener(
      object : OnNavigationMenuItemSelectedListener {
        override fun onNavigationItemSelected(item: NavigationMenuItem) {
          l.invoke(item)
        }
      }
    )
  }

  @Suppress("unused")
  fun setOnNavigationItemReselectedListener(l: OnNavigationMenuItemReselectedListener) {
    onNavigationMenuItemReselectedListener = l
  }

  @Suppress("unused")
  fun setOnNavigationItemReselectedListener(l: (NavigationMenuItem) -> Unit) {
    setOnNavigationItemReselectedListener(
      object : OnNavigationMenuItemReselectedListener {
        override fun onNavigationItemReselected(item: NavigationMenuItem) {
          l.invoke(item)
        }
      }
    )
  }
}