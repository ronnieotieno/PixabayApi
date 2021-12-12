package com.ronnie.presenatation.utils

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.ronnie.presenatation.MainImageActivity
import com.ronnie.presenatation.R

fun Fragment.setToolbar(toolbar: Toolbar) {
    (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
    (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    (requireActivity() as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
}

fun Activity.changeStatusBar(shouldBeLight:Boolean){
    WindowInsetsControllerCompat(window,  window.decorView.rootView).isAppearanceLightStatusBars = shouldBeLight
    window.statusBarColor = if(shouldBeLight) getColor(R.color.white) else getColor(R.color.black)
}

/**
 * Removes splash image for android 10 and below
 * Android 12 implements its own splash theme.
 */
fun Fragment.setBackgroundWhite() {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) return
    try {
        (requireActivity() as MainImageActivity).binding.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
    } catch (e: Exception) {
        Log.d(tag, "The Fragment isn't MainImageActivity child")
    }

}