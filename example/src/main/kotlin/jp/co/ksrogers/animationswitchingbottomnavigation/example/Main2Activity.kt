package jp.co.ksrogers.animationswitchingbottomnavigation.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.NavigationMenuItem
import jp.co.ksrogers.animationswitchingbottomnavigation.example.R.color
import jp.co.ksrogers.animationswitchingbottomnavigation.example.R.drawable
import jp.co.ksrogers.animationswitchingbottomnavigation.setupWithNavController
import kotlinx.android.synthetic.main.activity_main2.*

/**
 * Sample code for implement navigation with library.
 *
 * Use [jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationSelectedItemLayout] in this class for customize navigation item layout when selected.
 */
class Main2Activity : AppCompatActivity() {

  private lateinit var navController: NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main2)

    layout_animation_switching_bottom_navigation.apply {
      addNavigationMenuItem(
        listOf(
          NavigationMenuItem(
            id = R.id.fragment_sample1,
            iconDrawableRes = drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@Main2Activity, color.red)
          ),
          NavigationMenuItem(
            id = R.id.fragment_sample2,
            iconDrawableRes = drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@Main2Activity, color.blue)
          ),
          NavigationMenuItem(
            id = R.id.fragment_sample3,
            iconDrawableRes = drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@Main2Activity, color.green)
          ),
          NavigationMenuItem(
            id = R.id.fragment_sample4,
            iconDrawableRes = drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@Main2Activity, color.orange)
          ),
          NavigationMenuItem(
            id = R.id.fragment_sample5,
            iconDrawableRes = drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@Main2Activity, color.pink)
          )
        )
      )
    }
    navController = findNavController(R.id.nav_fragment_main)
    layout_animation_switching_bottom_navigation.setupWithNavController(navController)

    // レイアウトファイルに定義したViewに対してlistener設定などを行う
    with(layout_animation_switching_bottom_navigation_selected_layout) {
      setOnNavigationItemSelectedListener {
        selected_item_animation_view.playAnimation()
      }
      setOnNavigationItemReselectedListener {
        selected_item_animation_view.playAnimation()
      }
    }
  }
}