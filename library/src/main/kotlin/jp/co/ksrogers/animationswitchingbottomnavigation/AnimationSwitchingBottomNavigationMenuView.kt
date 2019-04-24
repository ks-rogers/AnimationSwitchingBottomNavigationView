package jp.co.ksrogers.animationswitchingbottomnavigation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import jp.co.ksrogers.animationswitchingbottomnavigation.internal.MenuItem

class AnimationSwitchingBottomNavigationMenuView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  private val itemHeight: Int

  private val onClickListener = View.OnClickListener {
    val newItemView = it as AnimationSwitchingBottomNavigationItemView
    var newSelectedItemPosition: Int = 0

    // セレクトされたpositionを取得する
    run loop@{
      itemViews.map { it.itemData }.forEachIndexed { index, item ->
        if (item.itemId == newItemView.itemData.itemId) {
          newSelectedItemPosition = index
          return@loop
        }
      }
    }

    // セレクトされたItemViewをfadeOutさせる
    items[newSelectedItemPosition].takeIf { !it.ischecked }?.let {
      it.setChecked(true)
//      itemViews[newSelectedItemPosition].fadeOutIcon()
    }

    onMenuItemClickListener?.onClick(this, newSelectedItemPosition)

    // セレクトが外れたItemViewをfadeInさせる
    items[selectedItemPosition].takeIf { it.ischecked }?.let {
      it.setChecked(false)
//      itemViews[selectedItemPosition].fadeInIcon()
    }
    selectedItemPosition = newSelectedItemPosition
    selectedItemId = items[newSelectedItemPosition].itemId
  }

  var items = MenuItem.getSampleMenuItems(context)
  var onMenuItemClickListener: OnMenuItemClickListener? = null
  var itemViews = mutableListOf<AnimationSwitchingBottomNavigationItemView>()
  private var selectedItemId = 0
  private var selectedItemPosition = 0
  private var childWidth: Int = 0

  companion object {
    const val MAX_ITEM_COUNT = 5
  }

  // TODO
  init {
    itemHeight =
      resources.getDimensionPixelSize(R.dimen.animation_switching_bottom_navigation_default_height)

    buildMenuItems(items)
  }

  fun buildMenuItems(menus: List<MenuItem>) {
    this.removeAllViews()

    if (itemViews.isNotEmpty()) itemViews.clear()

    menus.forEach {
      val item = AnimationSwitchingBottomNavigationItemView(context).apply {
        initialize(it)
        setOnClickListener(onClickListener)
      }
      itemViews.add(item)
      this.addView(item)
    }

    itemViews[selectedItemPosition].setChecked(true)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    val width = MeasureSpec.getSize(widthMeasureSpec)
    val childCount = childCount
    val heightSpec = MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY)

    childWidth = width / if (childCount == 0) 1 else childCount

    (0 until childCount).forEach {
      val child = getChildAt(it)
      child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), heightSpec)
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    val count = this.childCount
    val width = right - left
    val height = bottom - top
    var used = 0

    for (i in 0 until count) {
      val child = this.getChildAt(i)
      if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
        child.layout(width - used - child.measuredWidth, 0, width - used, height)
      } else {
        child.layout(used, 0, child.measuredWidth + used, height)
      }

      used += child.measuredWidth
    }
  }

  private fun getNewSelectedItemPosition(newItem: MenuItem) {

  }

  interface OnMenuItemClickListener {
    fun onClick(
      menuView: AnimationSwitchingBottomNavigationMenuView,
      newPosition: Int
    )
  }
}