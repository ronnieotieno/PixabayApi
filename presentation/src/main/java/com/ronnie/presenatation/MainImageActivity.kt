package com.ronnie.presenatation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ronnie.presenatation.databinding.ActivityMainBinding
import com.ronnie.presenatation.utils.changeStatusBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onBackPressed() {
        super.onBackPressed()
       changeStatusBar(true)
    }

}