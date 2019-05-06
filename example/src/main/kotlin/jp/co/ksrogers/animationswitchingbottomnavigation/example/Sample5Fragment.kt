package jp.co.ksrogers.animationswitchingbottomnavigation.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_sample.view.*

class Sample5Fragment : Fragment() {

  @SuppressLint("SetTextI18n")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_sample, container, false).apply {
      frame_sample.setBackgroundResource(R.color.pink)
      text_sample.text = "Sample5"
    }
  }
}