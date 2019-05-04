package jp.co.ksrogers.animationswitchingbottomnavigation

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.SelectedButtonSize.NORMAL
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.SelectedButtonSize.SMALL
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.animatorAlpha
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.animatorX
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.playSequentiallyExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.playTogetherExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setDurationExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setListener
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setStartDelayExt

/**
 * 最終的にライブラリとして開発者に提供されるLayout。
 * 内部的に [AnimationSwitchingBottomNavigationView], [AnimationSwitchingBottomNavigationSelectedButton]を生成し、メインコンテンツと両立させる。
 *
 */
class AnimationSwitchingBottomNavigationLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  /**
   * XMLで使用するAttributeのためのEnum Class
   */
  internal enum class SelectedButtonSize {
    NORMAL,
    SMALL;

    companion object {
      fun fromIndex(index: Int): SelectedButtonSize = when (index) {
        0 -> NORMAL
        1 -> SMALL
        else -> throw IllegalArgumentException("Unexpected attribute: selectedButtonSize=$index")
      }
    }
  }

  /**
   * 外部からセットするためのdata class
   */
  data class NavigationMenuItem(
    val id: Int,
    @DrawableRes val iconDrawableRes: Int,
    val selectedBackgroundColor: Int
  )

  companion object {
    const val MAX_CHILD_COUNT = 3
    const val SLIDE_ANIMATION_DURATION = 300L
    const val SLIDE_ANIMATION_START_DELAY = 5L
    const val FADE_ANIMATION_DURATION = 100L
    const val FADE_ANIMATION_START_DELAY = 150L
  }

  // NavigationViewでイベントが発火されたときに呼び出されるリスナー
  private val onNavigationClickListener =
    object : AnimationSwitchingBottomNavigationView.OnNavigationClickListener {
      override fun onClick(navigationItemItem: NavigationMenuItem, newPosition: Int) {
        if (selectedItemPosition == newPosition) return

        val menuView = navigationView.menuView
        val fromItemView = menuView.itemViews[selectedItemPosition]
        val toItemView = menuView.itemViews[newPosition]
        val selectedBackgroundView = navigationView.selectedBackgroundView

        if (animator?.isRunning != false) {
          animator?.cancel()
          animator = null
        }

        animator = AnimatorSet().playTogetherExt(
          AnimatorSet().playSequentiallyExt(
            createAnimatorFadeOutMenuItem(toItemView),
            createAnimatorFadeInMenuItem(fromItemView).setStartDelayExt(
              FADE_ANIMATION_START_DELAY
            )
          ),
          AnimatorSet().playTogetherExt(
            createAnimatorMoveToSelectedPosition(selectedButton, menuView, newPosition),
            createAnimatorMoveToSelectedBackgroundPosition(
              selectedBackgroundView,
              menuView,
              newPosition
            )
          )
        ).setListener(
          onCancel = {
            fromItemView.alpha = 1F
            toItemView.alpha = 1F
          }
        )

        selectedItemPosition = newPosition

        animator?.start()
      }
    }

  private val navigationView: AnimationSwitchingBottomNavigationView =
    AnimationSwitchingBottomNavigationView(context, attrs).apply {
      onNavigationClickListener =
        this@AnimationSwitchingBottomNavigationLayout.onNavigationClickListener
    }
  private val selectedButton =
    AnimationSwitchingBottomNavigationSelectedButton(context, attrs)

  private var navigationViewHeight =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_default_height)
  private var selectedWidth =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_normal_width)
  private var selectedHeight =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_normal_height)
  private var selectedBottomMargin =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_default_bottom_margin)

  private var buttonBackgroundColor: ColorStateList? = null
  private var selectedItemPosition: Int = 0
  private var animator: Animator? = null

  /**
   * 外部からセットする
   */
  private val items = mutableListOf<NavigationMenuItem>()

  init {
    attrs?.let {
      val a = context.obtainStyledAttributes(
        it,
        R.styleable.AnimationSwitchingBottomNavigationLayout
      )
      buttonBackgroundColor =
        a.getColorStateList(R.styleable.AnimationSwitchingBottomNavigationLayout_buttonBackgroundColor)
      navigationViewHeight =
        a.getDimensionPixelSize(
          R.styleable.AnimationSwitchingBottomNavigationLayout_navigationViewHeight,
          resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_default_height)
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

    selectedButton.backgroundTintList = buttonBackgroundColor
  }

  // 描画順序を考慮して、XMLのパースが終わった後にNavigationViewとSelectedButtonを追加する
  override fun onFinishInflate() {
    super.onFinishInflate()

    if (childCount < MAX_CHILD_COUNT) {
      addView(
        navigationView,
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM)
      )
      addView(selectedButton, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }

    if (childCount > MAX_CHILD_COUNT) throw IllegalStateException("This Layout can not have views more than 3.")
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val layoutWidthMode = MeasureSpec.getMode(widthMeasureSpec)
    val layoutWidth = MeasureSpec.getSize(widthMeasureSpec)
    val layoutHeightMode = MeasureSpec.getMode(heightMeasureSpec)
    val layoutHeight = MeasureSpec.getSize(heightMeasureSpec)

    // modeがEXACTLYであるかチェック
    if (layoutWidthMode != MeasureSpec.EXACTLY || layoutHeightMode != MeasureSpec.EXACTLY) {
      throw IllegalStateException("Please set 'match_parent' to AnimationSwitchingBottomNavigationLayout's width and height.")
    }

    // NavigationViewのサイズを確定
    val navigationWidthSpec = MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.AT_MOST)
    val navigationHeightSpec =
      MeasureSpec.makeMeasureSpec(navigationViewHeight, MeasureSpec.EXACTLY)
    navigationView.measure(navigationWidthSpec, navigationHeightSpec)

    // SelectedButtonのサイズを確定
    val selectedWidthSpec = MeasureSpec.makeMeasureSpec(selectedWidth, MeasureSpec.EXACTLY)
    val selectedHeightSpec = MeasureSpec.makeMeasureSpec(selectedHeight, MeasureSpec.EXACTLY)
    selectedButton.measure(selectedWidthSpec, selectedHeightSpec)

    // メインコンテンツのサイズを確定
    val content = getChildAt(0)
    val contentWidthSpec = MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.EXACTLY)
    val contentHeightSpec = MeasureSpec.makeMeasureSpec(
      layoutHeight - navigationViewHeight,
      MeasureSpec.EXACTLY
    )
    content.measure(contentWidthSpec, contentHeightSpec)

    setMeasuredDimension(layoutWidth, layoutHeight)
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    // itemViewsがない場合itemViews[0]で落ちるのでチェック
    if (navigationView.menuView.itemViews.size == 0) return

    // SelectedButtonの位置を確定
    val selectedWidth = selectedButton.measuredWidth
    val selectedHeight = selectedButton.measuredHeight
    val itemViewWidth = navigationView.menuView.getChildAt(0).measuredWidth
    val differenceBetweenSelectedAndItem = (itemViewWidth - selectedWidth) / 2
    selectedButton.layout(
      differenceBetweenSelectedAndItem,
      measuredHeight - selectedHeight - selectedBottomMargin,
      differenceBetweenSelectedAndItem + selectedWidth,
      measuredHeight - selectedBottomMargin
    )
  }

  private fun createAnimatorFadeOutMenuItem(
    itemView: AnimationSwitchingBottomNavigationItemView
  ): Animator {
    return itemView.animatorAlpha(1F, 0F)
      .setDurationExt(FADE_ANIMATION_DURATION)
  }

  private fun createAnimatorFadeInMenuItem(
    itemView: AnimationSwitchingBottomNavigationItemView
  ): Animator {
    return itemView.animatorAlpha(0F, 1F)
      .setDurationExt(FADE_ANIMATION_DURATION)
  }

  private fun createAnimatorMoveToSelectedPosition(
    selectedButton: AnimationSwitchingBottomNavigationSelectedButton,
    menuView: AnimationSwitchingBottomNavigationMenuView,
    newPosition: Int
  ): Animator {
    // animate animationSwitchingBottomNavigationSelectedButtonView
    val selectedButtonWidth = selectedButton.measuredWidth
    val itemViewWidth = menuView.getChildAt(newPosition).measuredWidth
    val startX = selectedButton.x
    val targetX = menuView.getChildAt(newPosition).x + (itemViewWidth - selectedButtonWidth) / 2

    return selectedButton.animatorX(startX, targetX)
      .setDurationExt(SLIDE_ANIMATION_DURATION)
      .setStartDelayExt(SLIDE_ANIMATION_START_DELAY)
  }

  private fun createAnimatorMoveToSelectedBackgroundPosition(
    selectedBackground: AnimationSwitchingBottomNavigationSelectedBackgroundView,
    menuView: AnimationSwitchingBottomNavigationMenuView,
    newPosition: Int
  ): Animator {
    // animate selectedBackground
    val selectedBackgroundWidth = navigationView.selectedBackgroundView.measuredWidth
    val itemViewWidth = menuView.getChildAt(newPosition).measuredWidth
    val startX = selectedBackground.x
    val targetX = menuView.getChildAt(newPosition).x + (itemViewWidth - selectedBackgroundWidth) / 2

    return selectedBackground.animatorX(startX, targetX)
      .setDurationExt(SLIDE_ANIMATION_DURATION)
  }

  fun addNavigationMenuItem(item: NavigationMenuItem) {
    items.add(item)
    dispatchItem()
  }

  fun addNavigationMenuItem(items: List<NavigationMenuItem>) {
    this.items.addAll(items)
    dispatchItem()
  }

  private fun dispatchItem() {
    navigationView.addNavigationMenuItem(items)
  }

  // TODO 以下、イメージしてるinterface
//  interface OnNavigationItemReselectedListener {
//    fun onNavigationItemReselected(@NonNull item: MenuItem): Boolean
//  }
//
//  interface OnNavigationItemSelectedListener {
//    fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean
//  }
//
//  interface OnNavigationAnimateLister {
//    fun onAnimationStart()
//    fun onAnimationCancel()
//    fun onAnimationEnd()
//  }
}