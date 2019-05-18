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

  /**
   * 外部からセットする
   */
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

  // 描画順序を考慮して、XMLのパースが終わった後にNavigationViewとSelectedButtonを追加する
  override fun onFinishInflate() {
    super.onFinishInflate()

    if (childCount < MAX_CHILD_COUNT) {
      // 内部に[AnimationSwitchingBottomNavigationSelectedItemLayout]があるかチェックする
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

      // デフォルトの[AnimationSwitchingBottomNavigationSelectedButton]はaddViewされてないのでここで追加
      if (!children.contains(selectedItemLayout)) {
        addView(selectedItemLayout)
      }
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

    // SelectedItemLayoutのサイズを確定
    val selectedWidthSpec = MeasureSpec.makeMeasureSpec(selectedWidth, MeasureSpec.EXACTLY)
    val selectedHeightSpec = MeasureSpec.makeMeasureSpec(selectedHeight, MeasureSpec.EXACTLY)
    selectedItemLayout.measure(selectedWidthSpec, selectedHeightSpec)

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

    // SelectedItemLayoutの位置を確定
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
      //TODO: animにズレが発生しているのでここはいらない
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

  fun getItems() = items

  @Suppress("unused")
  fun addNavigationMenuItem(item: NavigationMenuItem) {
    items.add(item)
    dispatchItem()
  }

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

  fun setSelectedPosition(position: Int) {
    if (selectedItemPosition == position) {
      return
    }
    animateToSelectedPosition(position)
    selectedItemPosition = position
  }

  interface OnNavigationMenuItemReselectedListener {
    fun onNavigationItemReselected(@NonNull item: NavigationMenuItem)
  }

  interface OnNavigationMenuItemSelectedListener {
    fun onNavigationItemSelected(@NonNull item: NavigationMenuItem)
  }
}