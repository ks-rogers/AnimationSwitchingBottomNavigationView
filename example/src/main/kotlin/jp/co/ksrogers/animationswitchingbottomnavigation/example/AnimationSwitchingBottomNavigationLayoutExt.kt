package jp.co.ksrogers.animationswitchingbottomnavigation.example

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.ui.R
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.NavigationMenuItem
import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.OnNavigationMenuItemSelectedListener
import java.lang.ref.WeakReference

fun AnimationSwitchingBottomNavigationLayout.setupWithNavController(navController: NavController) {
  this.onNavigationMenuItemSelectedListener = object : OnNavigationMenuItemSelectedListener {
    override fun onNavigationItemSelected(item: NavigationMenuItem) {
      onNavDestinationSelected(item, navController, true)
    }
  }

  val weakNavLayout = WeakReference<AnimationSwitchingBottomNavigationLayout>(this)
  navController.addOnDestinationChangedListener(object : OnDestinationChangedListener {
    override fun onDestinationChanged(
      controller: NavController,
      destination: NavDestination,
      arguments: Bundle?
    ) {
      val navLayout = weakNavLayout.get() ?: run {
        navController.removeOnDestinationChangedListener(this)
        return
      }

      val items = navLayout.getItems()
      items.forEachIndexed { index, item ->
        if (matchDestination(destination, item.id)) {
          navLayout.setSelectedPosition(index)
        }
      }
    }
  })
}

private fun onNavDestinationSelected(
  item: NavigationMenuItem,
  navController: NavController,
  popUp: Boolean
) {
  val builder = NavOptions.Builder()
    .setLaunchSingleTop(true)
    .setEnterAnim(R.anim.nav_default_enter_anim)
    .setExitAnim(R.anim.nav_default_exit_anim)
    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
  if (popUp) {
    builder.setPopUpTo(findStartDestination(navController.graph)!!.getId(), false)
  }
  val options = builder.build()
  try {
    // TODO provide proper API instead of using Exceptions as Control-Flow.
    navController.navigate(item.id, null, options)
  } catch (e: IllegalArgumentException) {
    // TODO
  }
}

private fun findStartDestination(@NonNull graph: NavGraph): NavDestination? {
  var startDestination: NavDestination? = graph
  while (startDestination is NavGraph) {
    val parent = startDestination as NavGraph?
    startDestination = parent!!.findNode(parent.startDestination)
  }
  return startDestination
}

private fun matchDestination(@NonNull destination: NavDestination, @IdRes destId: Int): Boolean {
  var currentDestination: NavDestination? = destination
  while (currentDestination!!.id != destId && currentDestination.parent != null) {
    currentDestination = currentDestination.parent
  }
  return currentDestination.id == destId
}