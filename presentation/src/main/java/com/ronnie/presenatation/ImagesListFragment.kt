package com.ronnie.presenatation

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.ronnie.data.api.PixaBayApi
import com.ronnie.domain.Image
import com.ronnie.presenatation.databinding.FragmentImageListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class ImagesListFragment: Fragment(R.layout.fragment_image_list) {
    private lateinit var binding:FragmentImageListBinding
    private val adapter = ImagesAdapter { image, imageView ->  navigate(image,imageView)}
    private val viewModel:MainViewModel by activityViewModels()

    @Inject
    lateinit var api: PixaBayApi
    private var job:Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentImageListBinding.bind(view)

        val itemDecoration =
            ItemOffsetDecoration(requireContext(), R.dimen.item_margin)
        binding.list.addItemDecoration(itemDecoration)

        binding.list.adapter = adapter

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job?.cancel()
        job =  lifecycleScope.launch(Dispatchers.IO) {
            val response = api.searchImages("fruits")

            withContext(Dispatchers.Main) {
                adapter.setImages(response.images)
            }

        }
        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else {

        }
    }
    private fun navigate(image: Image, imageView: ImageView) {
        viewModel.selectedImage = image
        val extras = FragmentNavigatorExtras(
            imageView to image.largeImageURL
        )
        val action = ImagesListFragmentDirections.toImageDetailFragment()
        findNavController().navigate(action, extras)
        changeStatusBarColorToBlack()
    }


    private fun changeStatusBarColorToBlack(){
        requireActivity().apply {
            changeStatusBar(false)
        }
    }

}