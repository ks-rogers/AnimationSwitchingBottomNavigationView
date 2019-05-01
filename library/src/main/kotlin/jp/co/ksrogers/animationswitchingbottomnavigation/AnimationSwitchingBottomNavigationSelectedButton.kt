package jp.co.ksrogers.animationswitchingbottomnavigation

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.IdRes

class AnimationSwitchingBottomNavigationSelectedButton @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : ImageButton(context, attrs) {

  private lateinit var navigationView: AnimationSwitchingBottomNavigationView
  private var ovalDrawable: Drawable? = null
  private var buttonColor: ColorStateList? = null
  @IdRes private var menuViewId: Int = 0

  private val onClickListener = OnClickListener {
    Toast.makeText(context, "クリック", Toast.LENGTH_SHORT).show()
  }

  init {
    attrs?.let {
      val a = context.obtainStyledAttributes(
        it,
        R.styleable.AnimationSwitchingBottomNavigationSelectedButton
      )
      buttonColor =
        a.getColorStateList(R.styleable.AnimationSwitchingBottomNavigationSelectedButton_buttonColor)
      menuViewId = a.getResourceId(R.styleable.AnimationSwitchingBottomNavigationSelectedButton_menuViewId, 0)
      a.recycle()
    }

    ovalDrawable = resources.getDrawable(
      R.drawable.animation_switching_bottom_navigation_selected_background,
      null
    ).apply {
      setTintList(buttonColor)
    }
    background = ovalDrawable
    setOnClickListener(onClickListener)
  }

//  /**
//   * 自身のサイズを変更する場合
//   */
//  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//
//    // FIXME 一旦の実装
//    // initの中ではまだViewTreeが構築されてないようで取得できない
//    resolveBottomNavigationView(menuViewId)
//  }
//
//  /**
//   * 自身の位置を設定する
//   */
//  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
//    super.onLayout(changed, left, top, right, bottom)
//
//    val itemWidth = navigationView.menuView.itemViews[0].measuredWidth
//    val itemHeight = navigationView.menuView.itemViews[0].measuredHeight
//  }
//
//  fun resolveBottomNavigationView(menuViewId: Int) {
//   navigationView = (parent as ViewGroup).findViewById(menuViewId)
//  }

  fun setBckgroundTintLisst(colorStateList: ColorStateList?) {
    ovalDrawable?.setTintList(colorStateList)
  }
}