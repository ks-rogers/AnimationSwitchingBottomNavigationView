package jp.co.ksrogers.animationswitchingbottomnavigation.internal

import jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout.NavigationMenu

internal data class NavigationMenuState(
  val selectedNavigationMenu: NavigationMenu,
  val selectedMenuPosition: Int
)