package com.ronnie.presenatation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ronnie.data.api.PixaBayApi
import com.ronnie.presenatation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainImageActivity : AppCompatActivity() {
    private val adapter = ImagesAdapter {}
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var api: PixaBayApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemDecoration =
            ItemOffsetDecoration(this, R.dimen.item_margin)
        binding.list.addItemDecoration(itemDecoration)

        binding.list.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            val response = api.searchImages("fruits")

            withContext(Dispatchers.Main) {
                adapter.setImages(response.images)
            }

        }


    }

}