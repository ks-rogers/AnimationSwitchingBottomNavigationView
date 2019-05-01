package jp.co.ksrogers.animationswitchingbottomnavigation

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
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

  val menuView: AnimationSwitchingBottomNavigationMenuView
  val underWartView: UnderWartView

  private var selectedItemId: Int = 0
  private var selectedItemPosition: Int = 0

  private var animator: Animator? = null

  /**
   * クリックされたときにアニメーションさせる
   */
  private val onMenuItemClickListener =
    object : AnimationSwitchingBottomNavigationMenuView.OnMenuItemClickListener {
      override fun onClick(
        menuView: AnimationSwitchingBottomNavigationMenuView,
        newPosition: Int
      ) {

        val newItemData = menuView.itemViews[newPosition].itemData
        onNavigationClickListener?.onClick(newItemData, newPosition)
        selectedItemPosition = newPosition
        selectedItemId = newItemData.itemId
      }
    }

  var onNavigationClickListener: OnNavigationClickListener? = null

  init {
    menuView = AnimationSwitchingBottomNavigationMenuView(context).apply {
      this.onMenuItemClickListener =
        this@AnimationSwitchingBottomNavigationView.onMenuItemClickListener
    }
    underWartView = UnderWartView(context)

    val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM)
    menuView.layoutParams = params

    addView(menuView, params)
    addView(
      underWartView,
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP)
    )
  }

  // TODO もしかするとUnderWartViewのサイズによっては位置調整のためにonLayoutをoverrideする必要があるかもしれない

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    // UnderWartViewのサイズを決定する
    val menuView = getChildAt(MENU_VIEW_INDEX) as AnimationSwitchingBottomNavigationMenuView
    // TODO 2019/05/01 追加 これを追加しないとMenuViewのサイズがなくなってしまう
    menuView.measure(
      MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
    )

    val underWartWidth = menuView.getChildAt(0).measuredWidth
    val underWartHeight = menuView.measuredHeight
    getChildAt(SELECTED_BACKGROUND_VIEW_INDEX).measure(
      MeasureSpec.makeMeasureSpec(underWartWidth, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(underWartHeight, MeasureSpec.EXACTLY)
    )
  }

  interface OnNavigationClickListener {
    fun onClick(menuItem: MenuItem, newPosition: Int)
  }
}