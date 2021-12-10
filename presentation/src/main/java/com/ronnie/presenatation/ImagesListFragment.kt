package com.ronnie.presenatation

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.ronnie.data.api.PixaBayApi
import com.ronnie.domain.Image
import com.ronnie.presenatation.databinding.FragmentImageListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ImagesListFragment: Fragment(R.layout.fragment_image_list) {
    private lateinit var binding:FragmentImageListBinding
    private val adapter = ImagesAdapter { image, imageView ->  navigate(image,imageView)}
    private val viewModel:MainViewModel by activityViewModels()
    private var isInitiated = false

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
        binding.list. setHasFixedSize(true)
        if(!isInitiated) init()

        val currentOrientation = resources.configuration.orientation
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else {

        }

    }

    private fun init(){
        isInitiated = true
        searchImages("dogs")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun searchImages(searchString: String) {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch{
            viewModel.searchImages(searchString).collectLatest { data ->
                adapter.submitData(data)
            }
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