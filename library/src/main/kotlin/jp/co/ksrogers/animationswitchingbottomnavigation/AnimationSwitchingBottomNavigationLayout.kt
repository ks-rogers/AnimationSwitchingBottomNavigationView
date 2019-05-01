package jp.co.ksrogers.animationswitchingbottomnavigation

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.annotation.NonNull
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.SelectedButtonSize.NORMAL
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.SelectedButtonSize.SMALL
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.animatorAlpha
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.animatorX
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.playSequentiallyExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.playTogetherExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setDurationExt
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setListener
import jp.co.ksrogers.animationswitchingbottomnavigation.ext.setStartDelayExt
import jp.co.ksrogers.animationswitchingbottomnavigation.internal.MenuItem

/**
 * TODO 最終的にライブラリとして開発者に提供されるLayout。
 * 内部的に {@link AnimationSwitchingBottomNavigationView}, {@link AnimationSwitchingBottomNavigationSelectedButton}を生成し、メインコンテンツと両立させる。
 * AnimationSwitchingBottomNavigationSelectedViewとAnimationSwitchingBottomNavigationViewの選択時の処理を同時にdispatchするため、このレイアウトを作成する。
 *
 */
class AnimationSwitchingBottomNavigationLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  private enum class SelectedButtonSize {
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

  companion object {
    const val NAVIGATION_VIEW_INDEX = 1
    const val SELECTED_BUTTON_INDEX = 2
    const val MAX_CHILD_COUNT = 3

    // FIXME should delete
    const val TAG_NAME = "NavLayout"
  }

  // NavigationViewでイベントが発火されたときに呼び出されるリスナー
  private val onNavigationClickListener =
    object : AnimationSwitchingBottomNavigationView.OnNavigationClickListener {
      override fun onClick(menuItem: MenuItem, newPosition: Int) {
        if (selectedItemPosition == newPosition) return

        val menuView = navigationView.menuView
        val itemView = menuView.itemViews[selectedItemPosition]
        val newItemView = menuView.itemViews[newPosition]
        val underwartView = navigationView.underWartView

        if (animator?.isRunning != false) {
          animator?.cancel()
          animator = null
        }

        animator = AnimatorSet().playSequentiallyExt(
          createAnimatorFadeOutMenuItem(newItemView),
          AnimatorSet().playTogetherExt(
            createAnimatorMoveToSelectedPosition(selectedButton, menuView, newPosition),
            createAnimatorMoveToSelectedBackgroundPosition(underwartView, menuView, newPosition)
          ),
          createAnimatorFadeInMenuItem(itemView)
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
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_default_width)
  private var selectedHeight =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_default_height)
  private var selectedBottomMargin =
    resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_default_bottom_margin)

  private var buttonBackgroundColor: ColorStateList? = null

  private var selectedItemId: Int = 0
  private var selectedItemPosition: Int = 0

  private var animator: Animator? = null

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
            resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_default_width)
          selectedHeight =
            resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_selected_default_height)
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

    selectedButton.setBckgroundTintLisst(buttonBackgroundColor)
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
    val navigationWidth = layoutWidth
    val navigationWidthSpec = MeasureSpec.makeMeasureSpec(navigationWidth, MeasureSpec.AT_MOST)
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
    // SelectedButtonの位置を確定
    val selectedWidth = selectedButton.measuredWidth
    val selectedHeight = selectedButton.measuredHeight
    val itemViewWidth = navigationView.menuView.itemViews[0].measuredWidth
    val differenceBetweeensselectedAndItem = (itemViewWidth - selectedWidth) / 2
    selectedButton.layout(
      differenceBetweeensselectedAndItem,
      measuredHeight - selectedHeight - selectedBottomMargin,
      differenceBetweeensselectedAndItem + selectedWidth,
      measuredHeight - selectedBottomMargin
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
    selectedButton: AnimationSwitchingBottomNavigationSelectedButton,
    menuView: AnimationSwitchingBottomNavigationMenuView,
    newPosition: Int
  ): Animator {
    // animate underWartView
    val selectedButtonWidth = selectedButton.measuredWidth
    val itemViewWidth = menuView.itemViews[newPosition].measuredWidth
    val startX = selectedButton.x
    val targetX = menuView.itemViews[newPosition].x + (itemViewWidth - selectedButtonWidth) / 2

    return selectedButton.animatorX(startX, targetX)
      .setDurationExt(AnimationSwitchingBottomNavigationView.ANIMATION_DURATION)
      .setStartDelayExt(10L)
  }

  fun createAnimatorMoveToSelectedBackgroundPosition(
    underWartView: UnderWartView,
    menuView: AnimationSwitchingBottomNavigationMenuView,
    newPosition: Int
  ): Animator {
    // animate underWartView
    val startX = underWartView.x
    val targetX = menuView.itemViews[newPosition].x

    return underWartView.animatorX(startX, targetX)
      .setDurationExt(AnimationSwitchingBottomNavigationView.ANIMATION_DURATION)
  }

  interface OnNavigationItemReselectedListener {
    fun onNavigationItemReselected(@NonNull item: MenuItem): Boolean
  }

  interface OnNavigationItemSelectedListener {
    fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean
  }
}