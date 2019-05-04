package jp.co.ksrogers.animationswitchingbottomnavigation.ext

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.View

fun View.animatorAlpha(vararg values: Float) = ObjectAnimator.ofFloat(this, "alpha", *values)

fun View.animatorTranslationY(vararg values: Float) =
  ObjectAnimator.ofFloat(this, "translationY", *values)

fun View.animatorTranslationX(vararg values: Float) =
  ObjectAnimator.ofFloat(this, "translationX", *values)

fun View.animatorY(vararg values: Float) = ObjectAnimator.ofFloat(this, "y", *values)

fun View.animatorX(vararg values: Float) = ObjectAnimator.ofFloat(this, "x", *values)

fun AnimatorSet.playSequentiallyExt(vararg items: Animator) = apply { playSequentially(*items) }

fun AnimatorSet.playTogetherExt(vararg items: Animator) = apply { playTogether(*items) }

fun Animator.setInterpolatorExt(value: TimeInterpolator) = apply { interpolator = value }

fun Animator.setDurationExt(millSec: Long) = apply { duration = millSec }

fun Animator.setStartDelayExt(startDelay: Long) = apply { setStartDelay(startDelay) }

fun Animator.setListener(
  onStart: (Animator) -> Unit = {},
  onEnd: (Animator) -> Unit = {},
  onCancel: (Animator) -> Unit = {},
  onRepeat: (Animator) -> Unit = {}
) = apply {
  addListener(object : Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator) {
      onStart(animation)
    }

    override fun onAnimationEnd(animation: Animator) {
      onEnd(animation)
    }

    override fun onAnimationCancel(animation: Animator) {
      onCancel(animation)
    }

    override fun onAnimationRepeat(animation: Animator) {
      onRepeat(animation)
    }
  })
}

fun ValueAnimator.setIntValuesExt(vararg colors: Int) = apply {
  setIntValues(*colors)
}

fun ValueAnimator.setEvaluatorExt(evaluator: TypeEvaluator<*>) = apply {
  setEvaluator(evaluator)
}

fun ValueAnimator.addUpdateLister(
  onUpdate: (ValueAnimator?) -> Unit = {}
) = apply {
  addUpdateListener(object : AnimatorUpdateListener {
    override fun onAnimationUpdate(animation: ValueAnimator?) {
      onUpdate(animation)
    }
  })
}