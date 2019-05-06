package jp.co.ksrogers.animationswitchingbottomnavigation.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.NavigationMenuItem
import kotlinx.android.synthetic.main.activity_sample.*

class MainActivity : AppCompatActivity() {

  private lateinit var navController: NavController

  val onNavigationItemSelectedListener =
    object : AnimationSwitchingBottomNavigationLayout.OnNavigationMenuItemSelectedListener {
      override fun onNavigationItemSelected(item: NavigationMenuItem) {
        when (item.id) {
          R.id.animation_switching_botttom_navigation_sample_id_1 -> {
            navController.navigate(R.id.fragment_sample1)
          }
          R.id.animation_switching_botttom_navigation_sample_id_2 -> {
            navController.navigate(R.id.fragment_sample2)
          }
          R.id.animation_switching_botttom_navigation_sample_id_3 -> {
            navController.navigate(R.id.fragment_sample3)
          }
          R.id.animation_switching_botttom_navigation_sample_id_4 -> {
            navController.navigate(R.id.fragment_sample4)
          }
          R.id.animation_switching_botttom_navigation_sample_id_5 -> {
            navController.navigate(R.id.fragment_sample5)
          }
        }
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sample)
    layout_animation_switching_bottom_navigation.apply {
      addNavigationMenuItem(
        listOf(
          NavigationMenuItem(
            id = R.id.animation_switching_botttom_navigation_sample_id_1,
            iconDrawableRes = R.drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@MainActivity, R.color.red)
          ),
          NavigationMenuItem(
            id = R.id.animation_switching_botttom_navigation_sample_id_2,
            iconDrawableRes = R.drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@MainActivity, R.color.blue)
          ),
          NavigationMenuItem(
            id = R.id.animation_switching_botttom_navigation_sample_id_3,
            iconDrawableRes = R.drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@MainActivity, R.color.green)
          ),
          NavigationMenuItem(
            id = R.id.animation_switching_botttom_navigation_sample_id_4,
            iconDrawableRes = R.drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@MainActivity, R.color.orange)
          ),
          NavigationMenuItem(
            id = R.id.animation_switching_botttom_navigation_sample_id_5,
            iconDrawableRes = R.drawable.ic_home_black_24dp,
            selectedBackgroundColor = ContextCompat.getColor(this@MainActivity, R.color.pink)
          )
        )
      )
      onNavigationMenuItemSelectedListener = this@MainActivity.onNavigationItemSelectedListener
    }
    navController = findNavController(R.id.nav_fragment_main)
  }
}