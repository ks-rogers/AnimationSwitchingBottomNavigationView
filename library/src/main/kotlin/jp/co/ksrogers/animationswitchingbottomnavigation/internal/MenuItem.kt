package jp.co.ksrogers.animationswitchingbottomnavigation.internal

import android.content.Context
import android.graphics.drawable.Drawable
import jp.co.ksrogers.animationswitchingbottomnavigation.R

class MenuItem(
  val itemId: Int,
  val ordering: Int,
  val iconResId: Int = NO_ICON,
  val iconDrawable: Drawable?,
  var ischecked: Boolean = false
) {

  companion object {
    const val NO_ICON = 0

    fun getSampleMenuItems(context: Context): List<MenuItem> = listOf(
      MenuItem(
        itemId = R.id.animation_switching_botttom_navigation_sample_id_1,
        ordering = 0,
        iconResId = R.drawable.ic_home_black_24dp_sample,
        iconDrawable = context.resources.getDrawable(R.drawable.ic_home_black_24dp_sample, null)
      ),
      MenuItem(
        itemId = R.id.animation_switching_botttom_navigation_sample_id_2,
        ordering = 1,
        iconResId = R.drawable.ic_home_black_24dp_sample,
        iconDrawable = context.resources.getDrawable(R.drawable.ic_home_black_24dp_sample, null)
      ),
      MenuItem(
        itemId = R.id.animation_switching_botttom_navigation_sample_id_3,
        ordering = 2,
        iconResId = R.drawable.ic_home_black_24dp_sample,
        iconDrawable = context.resources.getDrawable(R.drawable.ic_home_black_24dp_sample, null)
      ),
      MenuItem(
        itemId = R.id.animation_switching_botttom_navigation_sample_id_4,
        ordering = 3,
        iconResId = R.drawable.ic_home_black_24dp_sample,
        iconDrawable = context.resources.getDrawable(R.drawable.ic_home_black_24dp_sample, null)
      ),
      MenuItem(
        itemId = R.id.animation_switching_botttom_navigation_sample_id_5,
        ordering = 4,
        iconResId = R.drawable.ic_home_black_24dp_sample,
        iconDrawable = context.resources.getDrawable(R.drawable.ic_home_black_24dp_sample, null)
      )
    )
  }

  fun setChecked(ischecked: Boolean) {
    this.ischecked = ischecked
  }
}