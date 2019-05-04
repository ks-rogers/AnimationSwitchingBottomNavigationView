package jp.co.ksrogers.animationswitchingbottomnavigation

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.NavigationMenu

class AnimationSwitchingBottomNavigationView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  val menuView: AnimationSwitchingBottomNavigationMenuView
  val selectedBackgroundView: AnimationSwitchingBottomNavigationSelectedBackgroundView

  private var selectedItemId: Int = 0
  private var selectedItemPosition: Int = 0

  /**
   * クリックされたときにアニメーションさせる
   */
  private val onMenuItemClickListener =
    object : AnimationSwitchingBottomNavigationMenuView.OnMenuItemClickListener {
      override fun onClick(
        menuView: AnimationSwitchingBottomNavigationMenuView,
        newPosition: Int
      ) {

        val nextItem = menuView.itemViews[newPosition].navigationItem
        onNavigationClickListener?.onClick(nextItem, newPosition)
        selectedItemPosition = newPosition
        selectedItemId = nextItem.id
      }
    }

  var onNavigationClickListener: OnNavigationClickListener? = null

  init {
    menuView = AnimationSwitchingBottomNavigationMenuView(context).apply {
      this.onMenuItemClickListener =
        this@AnimationSwitchingBottomNavigationView.onMenuItemClickListener
    }
    selectedBackgroundView = AnimationSwitchingBottomNavigationSelectedBackgroundView(context)

    val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM)
    menuView.layoutParams = params

    addView(menuView, params)
    addView(
      selectedBackgroundView,
      LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP)
    )
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = MeasureSpec.getSize(heightMeasureSpec)

    // MenuViewのサイズを決定する
    menuView.measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    )

    // SelectedBackgroundViewのサイズを決定する
    val selectedBackgroundHeight = menuView.measuredHeight
    selectedBackgroundView.measure(
      MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
      MeasureSpec.makeMeasureSpec(selectedBackgroundHeight, MeasureSpec.EXACTLY)
    )

    setMeasuredDimension(width, height)
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    // SelectedBackgroundViewの位置を確定
    if (menuView.childCount == 0) return

    val selectedBackgroundWidth = selectedBackgroundView.measuredWidth
    val selectedBackgroundHeight = selectedBackgroundView.measuredHeight
    val itemViewWidth = menuView.itemViews[0].measuredWidth
    val differenceBetweenSelectedBackgroundAndItem = (itemViewWidth - selectedBackgroundWidth) / 2
    selectedBackgroundView.layout(
      differenceBetweenSelectedBackgroundAndItem,
      measuredHeight - selectedBackgroundHeight,
      differenceBetweenSelectedBackgroundAndItem + selectedBackgroundWidth,
      measuredHeight
    )
  }

  fun addNavigationMenu(menuList: List<NavigationMenu>) {
    menuView.addNavigationItems(menuList)
  }

  interface OnNavigationClickListener {
    fun onClick(navigationItem: NavigationMenu, newPosition: Int)
  }
}