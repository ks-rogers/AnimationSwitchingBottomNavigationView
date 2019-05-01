package jp.co.ksrogers.animationswitchingbottomnavigation

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
  }

  val menuView: AnimationSwitchingBottomNavigationMenuView
  val underWartView: UnderWartView

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
  // TODO 再度デザイン確認しにいったところセレクトの背景は各タブよりも横幅が大きいっぽいので、UnderWartView側で確定するようにしたほうが良いかも

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = MeasureSpec.getSize(heightMeasureSpec)

    // UnderWartViewのサイズを決定する
    val menuView = getChildAt(MENU_VIEW_INDEX) as AnimationSwitchingBottomNavigationMenuView
    // TODO 2019/05/01 追加 これを追加しないとMenuViewのサイズがなくなってしまう
    menuView.measure(
      MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
    )

//    val underWartWidth = menuView.getChildAt(0).measuredWidth
    val underWartHeight = menuView.measuredHeight
    getChildAt(SELECTED_BACKGROUND_VIEW_INDEX).measure(
      MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
      MeasureSpec.makeMeasureSpec(underWartHeight, MeasureSpec.EXACTLY)
    )

    setMeasuredDimension(width, height)
  }

  // TODO UnderWartViewがタブとサイズが違ってくるので、UnderWartViewの中心がItemViewの中心になるようにlayoutする
  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    // SelectedButtonの位置を確定
    val underWartWidth = underWartView.measuredWidth
    val underWartHeight = underWartView.measuredHeight
    val itemViewWidth = menuView.itemViews[0].measuredWidth
    val differenceBetweeenUnderWartAndItem = (itemViewWidth - underWartWidth) / 2
    underWartView.layout(
      differenceBetweeenUnderWartAndItem,
      measuredHeight - underWartHeight,
      differenceBetweeenUnderWartAndItem + underWartWidth,
      measuredHeight
    )
  }

  interface OnNavigationClickListener {
    fun onClick(menuItem: MenuItem, newPosition: Int)
  }
}