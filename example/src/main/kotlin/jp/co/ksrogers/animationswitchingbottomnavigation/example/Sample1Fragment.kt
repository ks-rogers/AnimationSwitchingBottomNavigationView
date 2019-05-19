package jp.co.ksrogers.animationswitchingbottomnavigation.example

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_sample.view.*

class Sample1Fragment : Fragment() {

  @SuppressLint("SetTextI18n")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_sample, container, false).apply {
      frame_sample.setBackgroundResource(R.color.red)
      text_sample.text = "Sample1"
      btn_open_main2.setOnClickListener {
        startActivity(Intent(activity, Main2Activity::class.java))
      }
    }
  }
}