package jp.co.ksrogers.animationswitchingbottomnavigation

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.NavigationMenuItem

@RestrictTo(LIBRARY_GROUP)
class AnimationSwitchingBottomNavigationView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  val menuView: AnimationSwitchingBottomNavigationMenuView
  val selectedBackgroundView: AnimationSwitchingBottomNavigationSelectedBackgroundView

  private var selectedItemId: Int = 0
  private var selectedItemPosition: Int = 0

  private val onMenuItemClickListener =
    object : AnimationSwitchingBottomNavigationMenuView.OnMenuItemClickListener {
      override fun onClick(
        menuView: AnimationSwitchingBottomNavigationMenuView,
        newPosition: Int
      ) {

        val nextItem = menuView.itemViews[newPosition].item
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
    // itemViewsがない場合itemViews[0]で落ちるのでチェック
    if (menuView.childCount == 0) return

    // SelectedBackgroundViewの位置を確定
    val selectedBackgroundWidth = selectedBackgroundView.measuredWidth
    val selectedBackgroundHeight = selectedBackgroundView.measuredHeight
    val itemViewWidth = menuView.getChildAt(0).measuredWidth
    val differenceBetweenSelectedBackgroundAndItem = (itemViewWidth - selectedBackgroundWidth) / 2
    selectedBackgroundView.layout(
      differenceBetweenSelectedBackgroundAndItem,
      measuredHeight - selectedBackgroundHeight,
      differenceBetweenSelectedBackgroundAndItem + selectedBackgroundWidth,
      measuredHeight
    )
  }

  fun addNavigationMenuItem(menuItemList: List<NavigationMenuItem>) {
    menuView.addNavigationItems(menuItemList)

    if (menuItemList.size > selectedItemPosition) {
      selectedBackgroundView.color = menuItemList[selectedItemPosition].selectedBackgroundColor
    }
  }

  interface OnNavigationClickListener {
    fun onClick(navigationItemItem: NavigationMenuItem, newPosition: Int)
  }
}