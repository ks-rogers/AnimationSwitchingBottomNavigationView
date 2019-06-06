# AnimationSwitchingBottomNavigationView

AnimationSwitchinBottomNavigationView is provide layout like a Bottom Navigation View. This project is inspired by [the dribble project](https://dribbble.com/shots/6044647-Tab-Bar-Animation-nr-3).

![alt text](https://github.com/ks-rogers/Assets/blob/master/AnimationSwitchingTabBar/demo.gif)

This layout provides menu item animation with no difficult settings.

iOS: https://github.com/ks-rogers/AnimationSwitchingTabBar

### Animation-able

Difference with Bottom Navitaion View is adopted animation-able menu item and selected menu item indicator.

When you click menu item, animiation will be calculate by AnimationSwitchingNavigatonBar and play it immidiately.

### Customizable

If you're not satisfied default layout of indicator, you can customize it.

Providing a layout for customize selected menu item indicator.

### Simple

Easily introduced into your project. Try it out!

## Gradle

```groovy
dependencies {
	implementation 'jp.co.ksrogers.animationswitchingbottomnavigation:core:1.0.2'
}
```

AnimationSwitchingNavigationView includes a module for Android Jetpack Navigation Component.

```groovy
implementation 'jp.co.ksrogers.animationswitchingbottomnavigation:navigation-support:1.0.0'
```

## Usage

### Add to layout xml

Use AnimationSwitchingBottomNavigationLayout as root layout.

```xml
<?xml version="1.0" encoding="utf-8"?>
<jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout
  xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:app="http://schemas.android.com/apk/res-auto"
  android:id="@+id/layout_animation_switching_bottom_navigation"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  app:navigationViewHeight="?attr/actionBarSize"
  app:selectedButtonBackgroundColor="@color/color_selected_button"
  app:selectedButtonSize="small"
  >
  
  ...
  
</jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout>
```

If you want to coordinate with Android Jetpack Navigation, add NavHostFragment as a child element of AnimationSwitchingBottomNavigationLayout.

```xml
<?xml version="1.0" encoding="utf-8"?>
<jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout
  xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:app="http://schemas.android.com/apk/res-auto"
  android:id="@+id/layout_animation_switching_bottom_navigation"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  app:navigationViewHeight="?attr/actionBarSize"
  app:selectedButtonBackgroundColor="@color/color_selected_button"
  app:selectedButtonSize="small"
  >

  <LinearLayout
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/red"
    >

    <fragment
      android:id="@+id/nav_fragment_main"
      android:name="androidx.navigation.fragment.NavHostFragment"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      app:defaultNavHost="true"
      app:navGraph="@navigation/nav_sample"
      />

  </LinearLayout>
</jp.co.ksrogers.animationswitchingbottomnavigation.AnimationSwitchingBottomNavigationLayout>
```

You can customize the layout from XML.

Please check sample code.

### Add menu item

Setup AnimationSwitchingBottomNavigationLayout in the activity.

You need add menu items.

```kotlin
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
        ...
      )
    )
  }
...
}
```

If you want to coordinate with X,   you need setup NavController and set the NavController to Layout.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContentView(R.layout.activity_main)
  ...
  navController = findNavController(R.id.nav_fragment_main)
  layout_animation_switching_bottom_navigation.setupWithNavController(navController)
}
```

## Example

To run the example of use this, clone this repository and open on Android Studio.

Example codes placed in `example` module.

## Contributing

Thanks for contributing! Please check below.

### Bug Report

Thank you for finding the bug.

If possible you can, please attach sample project or sample code for reproduce the bug.

### Send a Pull Request

If you fix a bug, please check below.

- Attach sample project or sample code for reproduce the issue.

## Change Log

### Version 1.0.0

first release :tada:

## Lisence

AnimationSwitchingBottomNavigationView is available under the MIT license. See the [LICENSE](./LICENSE) file for more info.