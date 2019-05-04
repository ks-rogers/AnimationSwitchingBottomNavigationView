package jp.co.ksrogers.animationswitchingbottomnavigation.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.NavigationMenuItem
import kotlinx.android.synthetic.main.activity_sample.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sample)
    layout_animation_switching_bottom_navigation.addNavigationMenuItem(
      listOf(
        NavigationMenuItem(
          id = R.id.animation_switching_botttom_navigation_sample_id_1,
          iconDrawableRes = R.drawable.ic_home_black_24dp,
          selectedBackgroundColor = ContextCompat.getColor(this, R.color.red)
        ),
        NavigationMenuItem(
          id = R.id.animation_switching_botttom_navigation_sample_id_2,
          iconDrawableRes = R.drawable.ic_home_black_24dp,
          selectedBackgroundColor = ContextCompat.getColor(this, R.color.blue)
        ),
        NavigationMenuItem(
          id = R.id.animation_switching_botttom_navigation_sample_id_3,
          iconDrawableRes = R.drawable.ic_home_black_24dp,
          selectedBackgroundColor = ContextCompat.getColor(this, R.color.green)
        ),
        NavigationMenuItem(
          id = R.id.animation_switching_botttom_navigation_sample_id_4,
          iconDrawableRes = R.drawable.ic_home_black_24dp,
          selectedBackgroundColor = ContextCompat.getColor(this, R.color.orange)
        ),
        NavigationMenuItem(
          id = R.id.animation_switching_botttom_navigation_sample_id_5,
          iconDrawableRes = R.drawable.ic_home_black_24dp,
          selectedBackgroundColor = ContextCompat.getColor(this, R.color.pink)
        )
      )
    )
  }
}