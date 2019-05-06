package jp.co.ksrogers.animationswitchingbottomnavigation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.core.view.ViewCompat
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.NavigationMenuItem

@RestrictTo(LIBRARY_GROUP)
class AnimationSwitchingBottomNavigationMenuView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

  private val onClickListener = View.OnClickListener { v ->
    val newItemView = v as AnimationSwitchingBottomNavigationItemView
    var newSelectedItemPosition = 0

    // セレクトされたpositionを取得する
    run loop@{
      itemViews.map { it.item }.forEachIndexed { index, item ->
        if (item.id == newItemView.item.id) {
          newSelectedItemPosition = index
          return@loop
        }
      }
    }

    itemViews[newSelectedItemPosition].takeIf { !it.isSelected }?.isSelected = true

    onMenuItemClickListener?.onClick(this, newSelectedItemPosition)

    itemViews[selectedItemPosition].takeIf { it.isSelected }?.isSelected = false
    selectedItemPosition = newSelectedItemPosition
  }

  var onMenuItemClickListener: OnMenuItemClickListener? = null
  var itemViews = mutableListOf<AnimationSwitchingBottomNavigationItemView>()
  private var selectedItemPosition = 0
  private var childWidth: Int = 0

  private var items = mutableListOf<NavigationMenuItem>()

  fun addNavigationItems(items: List<NavigationMenuItem>) {
    this.items.addAll(items)
    buildMenuItems()
  }

  init {
    buildMenuItems()
  }

  private fun buildMenuItems() {
    this.removeAllViews()

    if (itemViews.isNotEmpty()) itemViews.clear()

    items.forEach {
      val item = AnimationSwitchingBottomNavigationItemView(context).apply {
        initialize(it)
        setOnClickListener(onClickListener)
      }
      itemViews.add(item)
      this.addView(item)
    }

    if (itemViews.size > selectedItemPosition) {
      itemViews[selectedItemPosition].isSelected = true
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    val width = MeasureSpec.getSize(widthMeasureSpec)
    val childCount = childCount

    childWidth = width / if (childCount == 0) 1 else childCount

    (0 until childCount).forEach {
      val child = getChildAt(it)
      child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), heightMeasureSpec)
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

  interface OnMenuItemClickListener {
    fun onClick(
      menuView: AnimationSwitchingBottomNavigationMenuView,
      newPosition: Int
    )
  }
}