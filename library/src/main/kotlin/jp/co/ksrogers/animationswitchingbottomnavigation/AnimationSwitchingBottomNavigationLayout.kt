package jp.co.ksrogers.animationswitchingbottomnavigation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.core.view.children
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.SelectedButtonSize.NORMAL
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.SelectedButtonSize.SMALL
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.addUpdateLister
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.animatorAlpha
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.animatorX
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.playSequentiallyExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.playTogetherExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setDurationExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setEvaluatorExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setIntValuesExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setListener
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setStartDelayExt

/**
 *
 * A Layout to provide bottom navigation w/ animation-able button and menu items.
 *
 * This layout has several views.
 *  - [AnimationSwitchingBottomNavigationView]
 *  - [AnimationSwitchingBottomNavigationSelectedButton]
 *    - If you want to customize button, you can use [AnimationSwitchingBottomNavigationSelectedItemLayout]
 *
 */
class AnimationSwitchingBottomNavigationLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  /**
   *
   * Enum class for specify size of [AnimationSwitchingBottomNavigationSelectedButton] or [AnimationSwitchingBottomNavigationSelectedItemLayout] from layout XML.
   *
   * <p> For use in layout XML only.
   *
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
   *
   * Data class for set navigation menu item from outside.
   *
   * @param id An id of menu item.
   * @param iconDrawableRes A resource id of menu item icon.
   * @param selectedBackgroundColor A single color value in the form 0xAARRGGBB.
   *
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

  // Listener that called when event fired by [AnimationSwitchingBottomNavigationView]
  private val onNavigationClickListener =
    object : AnimationSwitchingBottomNavigationView.OnNavigationClickListener {
      override fun onClick(navigationItemItem: NavigationMenuItem, newPosition: Int) {
        if (selectedItemPosition == newPosition) {
          onNavigationMenuItemReselectedListener?.onNavigationItemReselected(items[selectedItemPosition])
          return
        }

        animateToSelectedPosition(newPosition)

        selectedItemPosition = newPosition

        onNavigationMenuItemSelectedListeners.forEach {
          it.onNavigationItemSelected(items[selectedItemPosition])
        }
      }
    }

  private val navigationView: AnimationSwitchingBottomNavigationView =
    AnimationSwitchingBottomNavigationView(context, attrs).apply {
      onNavigationClickListener =
        this@AnimationSwitchingBottomNavigationLayout.onNavigationClickListener
    }
  private lateinit var selectedItemLayout: FrameLayout

  private var navigationViewHeight =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_default_height)
  private var selectedWidth =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_normal_width)
  private var selectedHeight =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_normal_height)
  private var selectedBottomMargin =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_default_bottom_margin)

  private var selectedItemPosition: Int = 0
  private var animator: Animator? = null

  private val items = mutableListOf<NavigationMenuItem>()

  var onNavigationMenuItemReselectedListener: OnNavigationMenuItemReselectedListener? = null
  var onNavigationMenuItemSelectedListeners: MutableList<OnNavigationMenuItemSelectedListener> =
    mutableListOf()

  init {
    val selectedButton = AnimationSwitchingBottomNavigationSelectedButton(context, attrs)

    attrs?.let {
      val a = context.obtainStyledAttributes(
        it,
        R.styleable.AnimationSwitchingBottomNavigationLayout
      )
      navigationViewHeight =
        a.getDimensionPixelSize(
          R.styleable.AnimationSwitchingBottomNavigationLayout_navigationViewHeight,
          resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_default_height)
        )
      selectedButton.setOvalTintList(
        a.getColorStateList(
          R.styleable.AnimationSwitchingBottomNavigationLayout_selectedButtonBackgroundColor
        )
      )
      SelectedButtonSize.fromIndex(
        a.getInt(
          R.styleable.AnimationSwitchingBottomNavigationLayout_selectedButtonSize,
          0
        )
      ).also { selectedButtonSize ->
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
      }
      a.recycle()
    }
    selectedItemLayout = selectedButton
  }

  /**
   *
   * Add [AnimationSwitchingBottomNavigationView] and [AnimationSwitchingBottomNavigationSelectedButton]
   * after finished parse layout XML because considering drawing order.
   *
   * In this method, if this layout has [AnimationSwitchingBottomNavigationSelectedItemLayout],
   * replace it and [AnimationSwitchingBottomNavigationSelectedButton].
   *
   */
  override fun onFinishInflate() {
    super.onFinishInflate()

    if (childCount < MAX_CHILD_COUNT) {
      val layout = children.firstOrNull {
        it is AnimationSwitchingBottomNavigationSelectedItemLayout
      } as? AnimationSwitchingBottomNavigationSelectedItemLayout

      layout?.let {
        setSelectedItemLayout(it)
      }

      addView(
        navigationView,
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM)
      )

      // default selectedItemLayout may addView() not yet, so do it here.
      if (!children.contains(selectedItemLayout)) {
        addView(selectedItemLayout)
      }
    }

    if (childCount > MAX_CHILD_COUNT) throw IllegalStateException("This Layout can not have views more than 3.")
  }

  /**
   *
   * Adjust each child views size dynamically considering w/ navigation layout size.
   *
   */
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val layoutWidthMode = MeasureSpec.getMode(widthMeasureSpec)
    val layoutWidth = MeasureSpec.getSize(widthMeasureSpec)
    val layoutHeightMode = MeasureSpec.getMode(heightMeasureSpec)
    val layoutHeight = MeasureSpec.getSize(heightMeasureSpec)

    if (layoutWidthMode != MeasureSpec.EXACTLY || layoutHeightMode != MeasureSpec.EXACTLY) {
      throw IllegalStateException("Please set 'match_parent' to AnimationSwitchingBottomNavigationLayout's width and height.")
    }

    // adjust size of NavigationView
    val navigationWidthSpec = MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.AT_MOST)
    val navigationHeightSpec =
      MeasureSpec.makeMeasureSpec(navigationViewHeight, MeasureSpec.EXACTLY)
    navigationView.measure(navigationWidthSpec, navigationHeightSpec)

    // adjust size of SelectedItemLayout
    val selectedWidthSpec = MeasureSpec.makeMeasureSpec(selectedWidth, MeasureSpec.EXACTLY)
    val selectedHeightSpec = MeasureSpec.makeMeasureSpec(selectedHeight, MeasureSpec.EXACTLY)
    selectedItemLayout.measure(selectedWidthSpec, selectedHeightSpec)

    // adjust size of main contents (that not generated by this library)
    val content = getChildAt(0)
    val contentWidthSpec = MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.EXACTLY)
    val contentHeightSpec = MeasureSpec.makeMeasureSpec(
      layoutHeight - navigationViewHeight,
      MeasureSpec.EXACTLY
    )
    content.measure(contentWidthSpec, contentHeightSpec)

    setMeasuredDimension(layoutWidth, layoutHeight)
  }

  /**
   *
   * Adjust each child views position dynamically considering w/ navigation layout size.
   *
   */
  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    // check itemViews has children for prevent crash
    if (navigationView.menuView.itemViews.size == 0) return

    // adjust position of SelectedItemLayout
    val selectedWidth = selectedItemLayout.measuredWidth
    val selectedHeight = selectedItemLayout.measuredHeight
    val itemViewWidth = navigationView.menuView.getChildAt(0).measuredWidth
    val differenceBetweenSelectedAndItem = (itemViewWidth - selectedWidth) / 2
    selectedItemLayout.layout(
      differenceBetweenSelectedAndItem,
      measuredHeight - selectedHeight - selectedBottomMargin,
      differenceBetweenSelectedAndItem + selectedWidth,
      measuredHeight - selectedBottomMargin
    )
  }

  /**
   *
   * Set layout for navigation button that indicate selected menu item.
   *
   * This method doesn't need to use because [AnimationSwitchingBottomNavigationLayout] has a
   * [AnimationSwitchingBottomNavigationSelectedButton] by default.
   *
   * If you customize [AnimationSwitchingBottomNavigationSelectedButton],
   * we recommend to use [AnimationSwitchingBottomNavigationSelectedItemLayout] and implement at the
   * layout XML.
   *
   * @param layout layout of navigation button that extends [FrameLayout]
   *
   */
  @Suppress("unused")
  fun setSelectedItemLayout(layout: FrameLayout) {
    selectedItemLayout = layout
    (layout as? OnNavigationMenuItemSelectedListener)?.let { listener ->
      onNavigationMenuItemSelectedListeners.add(listener)
    }
    (layout as? OnNavigationMenuItemReselectedListener)?.let { listener ->
      onNavigationMenuItemReselectedListener = listener
    }
  }

  fun getSelectedItemLayout() = selectedItemLayout

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
    selectedItemLayout: FrameLayout,
    menuView: AnimationSwitchingBottomNavigationMenuView,
    newPosition: Int
  ): Animator {
    // animate animationSwitchingBottomNavigationSelectedButtonView
    val selectedItemLayoutWidth = selectedItemLayout.measuredWidth
    val itemViewWidth = menuView.getChildAt(newPosition).measuredWidth
    val startX = selectedItemLayout.x
    val targetX = menuView.getChildAt(newPosition).x + (itemViewWidth - selectedItemLayoutWidth) / 2

    return selectedItemLayout.animatorX(startX, targetX)
      .setDurationExt(SLIDE_ANIMATION_DURATION)
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

  fun getItems() = items

  /**
   *
   * Add menu item to end of menu list.
   *
   * ```
   * before: {A, B, C}
   * param: D
   * after: {A, B, C, D}
   * ```
   *
   * @param item menu item
   *
   */
  @Suppress("unused")
  fun addNavigationMenuItem(item: NavigationMenuItem) {
    items.add(item)
    dispatchItem()
  }

  /**
   *
   * Add menu items to end of menu list.
   *
   * ```
   * before: {A, B, C}
   * param: {X, Y, Z}
   * after: {A, B, C, X, Y, Z}
   * ```
   *
   * @param items list of menu items
   *
   */
  @Suppress("unused")
  fun addNavigationMenuItem(items: List<NavigationMenuItem>) {
    this.items.addAll(items)
    dispatchItem()
  }

  private fun dispatchItem() {
    navigationView.addNavigationMenuItem(items)
  }

  private fun animateToSelectedPosition(position: Int) {
    val menuView = navigationView.menuView
    val fromItemView = menuView.itemViews[selectedItemPosition]
    val toItemView = menuView.itemViews[position]
    val selectedBackgroundView = navigationView.selectedBackgroundView
    val fromColor = items[selectedItemPosition].selectedBackgroundColor
    val toColor = items[position].selectedBackgroundColor

    if (animator?.isRunning != false) {
      animator?.cancel()
      animator = null
    }

    // avoid flicking
    fromItemView.alpha = 0.0F

    animator = AnimatorSet().playTogetherExt(
      AnimatorSet().playSequentiallyExt(
        createAnimatorFadeOutMenuItem(toItemView),
        createAnimatorFadeInMenuItem(fromItemView).setStartDelayExt(FADE_ANIMATION_START_DELAY)
      ),
      AnimatorSet().playTogetherExt(
        createAnimatorMoveToSelectedPosition(selectedItemLayout, menuView, position),
        createAnimatorMoveToSelectedBackgroundPosition(
          selectedBackgroundView,
          menuView,
          position
        ),
        ValueAnimator().setIntValuesExt(
          fromColor,
          toColor
        ).setEvaluatorExt(ArgbEvaluator()).addUpdateLister {
          it?.let { animator ->
            val color: Int = animator.animatedValue as Int
            selectedBackgroundView.color = color
          }
        }.setDuration(FADE_ANIMATION_DURATION)
      )
    ).setListener(
      onCancel = {
        fromItemView.alpha = 1F
        toItemView.alpha = 1F
      }
    )

    animator?.start()
  }

  /**
   *
   * Change selected menu item with position.
   *
   * @param position menu item position. Value should be in range of 0 to item size.
   *
   */
  fun setSelectedPosition(position: Int) {
    if (selectedItemPosition == position) {
      return
    }
    animateToSelectedPosition(position)
    selectedItemPosition = position
  }

  /**
   *
   * Listener that call when selected menu item clicked repeatedly.
   *
   */
  interface OnNavigationMenuItemReselectedListener {
    fun onNavigationItemReselected(@NonNull item: NavigationMenuItem)
  }

  /**
   *
   * Listener that call when menu item clicked.
   *
   */
  interface OnNavigationMenuItemSelectedListener {
    fun onNavigationItemSelected(@NonNull item: NavigationMenuItem)
  }
}