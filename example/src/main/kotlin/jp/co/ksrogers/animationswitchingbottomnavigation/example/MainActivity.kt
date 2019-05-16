package jp.co.ksrogers.animationswitchingbottomnavigation.example

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.NavigationMenuItem
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.OnNavigationMenuItemReselectedListener
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.OnNavigationMenuItemSelectedListener
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationSelectedButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private lateinit var navController: NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    layout_animation_switching_bottom_navigation.apply {
      addNavigationMenuItem(
        listOf(
          NavigationMenuItem(
            id = R.id.fragment_sample1,
            iconDrawableRes = R.drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@MainActivity, R.color.red)
          ),
          NavigationMenuItem(
            id = R.id.fragment_sample2,
            iconDrawableRes = R.drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@MainActivity, R.color.blue)
          ),
          NavigationMenuItem(
            id = R.id.fragment_sample3,
            iconDrawableRes = R.drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@MainActivity, R.color.green)
          ),
          NavigationMenuItem(
            id = R.id.fragment_sample4,
            iconDrawableRes = R.drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@MainActivity, R.color.orange)
          ),
          NavigationMenuItem(
            id = R.id.fragment_sample5,
            iconDrawableRes = R.drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@MainActivity, R.color.pink)
          )
        )
      )
    }
    navController = findNavController(R.id.nav_fragment_main)
    layout_animation_switching_bottom_navigation.setupWithNavController(navController)

    // レイアウトファイルに定義したViewに対してlistener設定などを行う
//    with(layout_animation_switching_bottom_navigation_selected_layout) {
//      setOnNavigationItemSelectedListener {
//        selected_item_animation_view.playAnimation()
//      }
//      setOnNavigationItemReselectedListener {
//        selected_item_animation_view.playAnimation()
//      }
//    }

    // デフォルトで設定されるボタンに対してlistener設定などを行う
    layout_animation_switching_bottom_navigation.getSelectedItemLayout().setOnClickListener {
      Toast.makeText(this, "button clicked", Toast.LENGTH_SHORT).show()
    }
  }
}