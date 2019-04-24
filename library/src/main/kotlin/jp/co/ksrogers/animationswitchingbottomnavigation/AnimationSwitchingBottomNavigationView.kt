package jp.co.ksrogers.animationswitchingbottomnavigation

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.annotation.NonNull
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.animatorAlpha
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.animatorX
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.playSequentiallyExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setDurationExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setListener
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setStartDelayExt
import jp.co.ksrogers.animationswitchingbottomnavigation.internal.MenuItem

class AnimationSwitchingBottomNavigationView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  companion object {
    const val MENU_VIEW_INDEX = 0
    const val SELECTED_BACKGROUND_VIEW_INDEX = 1

    const val ANIMATION_DURATION = 150L
    const val ANIMATION_STARY_DELAY = 90L
  }

  private val menuView: AnimationSwitchingBottomNavigationMenuView
  private val underWartView: UnderWartView

  private var selectedItemId: Int = 0
  private var selectedItemPosition: Int = 0

  private var animator: Animator? = null

  private val onMenuItemClickListener =
    object : AnimationSwitchingBottomNavigationMenuView.OnMenuItemClickListener {
      override fun onClick(
        menuView: AnimationSwitchingBottomNavigationMenuView,
        newPosition: Int
      ) {
        if (selectedItemPosition == newPosition) return

        val itemView = menuView.itemViews[selectedItemPosition]
        val newItemView = menuView.itemViews[newPosition]

        if (animator?.isRunning != false) {
          animator?.cancel()
          animator = null
        }

        animator = AnimatorSet().playSequentiallyExt(
          createAnimatorFadeOutMenuItem(newItemView),
          createAnimatorMoveToSelectedPosition(menuView, newPosition),
          createAnimatorFadeInMenuItem(itemView)
        )

        selectedItemPosition = newPosition

        animator?.start()
      }
    }

  init {
    menuView = AnimationSwitchingBottomNavigationMenuView(context).apply {
      this.onMenuItemClickListener =
        this@AnimationSwitchingBottomNavigationView.onMenuItemClickListener
    }
    underWartView = UnderWartView(context)

    val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM)
    menuView.layoutParams = params

    addView(menuView, params)
    addView(underWartView)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    // UnderWartViewのサイズを決定する
    val menuView = getChildAt(MENU_VIEW_INDEX) as AnimationSwitchingBottomNavigationMenuView
    val underWartWidth = menuView.getChildAt(0).measuredWidth
    val underWartHeight = menuView.measuredHeight

    getChildAt(SELECTED_BACKGROUND_VIEW_INDEX).measure(
      MeasureSpec.makeMeasureSpec(underWartWidth, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(underWartHeight, MeasureSpec.EXACTLY)
    )
  }

  fun createAnimatorFadeOutMenuItem(
    itemView: AnimationSwitchingBottomNavigationItemView
  ): Animator {
    return itemView.animatorAlpha(1F, 0F)
    .setDurationExt(50L)
  }

  fun createAnimatorFadeInMenuItem(
    itemView: AnimationSwitchingBottomNavigationItemView
  ): Animator {
    return itemView.animatorAlpha(0F, 1F)
      .setDurationExt(50L)
      .setListener(
        onCancel = {
          itemView.alpha = 1F
        }
      )
  }

  fun createAnimatorMoveToSelectedPosition(
    menuView: AnimationSwitchingBottomNavigationMenuView,
    newPosition: Int
  ): Animator {
    // animate underWartView
    val startX = underWartView.x
    val targetX = menuView.itemViews[newPosition].x

    return underWartView.animatorX(startX, targetX)
      .setDurationExt(ANIMATION_DURATION)
  }

  interface OnNavigationItemReselectedListener {
    fun onNavigationItemReselected(@NonNull item: MenuItem): Boolean
  }

  interface OnNavigationItemSelectedListener {
    fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean
  }
}