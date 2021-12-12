package com.ronnie.presenatation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.ronnie.presenatation.R
import com.ronnie.presenatation.databinding.FragmentImageDetailsBinding
import com.ronnie.presenatation.utils.changeStatusBar
import com.ronnie.presenatation.utils.setToolbar
import com.ronnie.presenatation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageDetailFragment: Fragment(R.layout.fragment_image_details) {
    private lateinit var binding:FragmentImageDetailsBinding
    private val viewModel: MainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentImageDetailsBinding.bind(view)
        binding.image = viewModel.selectedImage
        loadTransition()

        binding.toolbar.apply {
            setToolbar(this)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    private fun loadTransition(){
        val imageUri = viewModel.selectedImage?.largeImageURL
        binding.imageView.apply {
           transitionName = imageUri
            Glide.with(requireContext())
                .load(imageUri)
                .into(this)
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().changeStatusBar(false)
    }

}