package com.ronnie.presenatation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ronnie.presenatation.databinding.ActivityMainBinding
import com.ronnie.presenatation.utils.changeStatusBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainImageActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        //useful in Android 11 going down, removes the logo which act as splash screen.
        window?.setBackgroundDrawableResource(R.color.white)
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        changeStatusBar(true)
    }

}