package com.ronnie.presenatation

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import androidx.core.view.WindowInsetsCompat.Type
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ronnie.data.api.PixaBayApi
import com.ronnie.domain.Image
import com.ronnie.presenatation.databinding.FragmentImageListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ImagesListFragment: Fragment(R.layout.fragment_image_list) {
    private lateinit var binding:FragmentImageListBinding
    private val adapter = ImagesAdapter { image, imageView ->  startNavigation(image,imageView)}
    private val viewModel:MainViewModel by activityViewModels()
    private var gridLayoutSpan = 2
    private var isInitiated = false

    @Inject
    lateinit var api: PixaBayApi
    private var job:Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentImageListBinding.bind(view)
        setSearchViewListener()
        setUpAdapter()
      //  if(!isInitiated)
            init()

    }

    private fun setUpAdapter() {
        val itemDecoration =
            ItemOffsetDecoration(requireContext(), R.dimen.item_margin)
        binding.list.addItemDecoration(itemDecoration)


        val currentOrientation = resources.configuration.orientation
        gridLayoutSpan = if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            3
        } else {
            2
        }

        val gridLayoutManager = GridLayoutManager(requireContext(), gridLayoutSpan)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                return if (viewType == IMAGE_VIEW_TYPE) 1
                else gridLayoutSpan
            }
        }
        binding.list.layoutManager = gridLayoutManager
        binding.list.adapter = adapter
        binding.list.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { retry() }
        )

        adapter.addLoadStateListener { state ->
            binding.progress.isVisible = state.refresh is LoadState.Loading

            if (state.refresh is LoadState.Error) {

            }
        }
    }

    private fun init(){
        isInitiated = true
        searchImages(viewModel.currentSearch)
        binding.searchView.setText(viewModel.currentSearch)
    }

    private fun searchImages(searchString: String, isUserInitiated:Boolean = false) {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch{
            viewModel.currentSearch = searchString
            if(isUserInitiated) adapter.submitData(PagingData.empty())
            viewModel.searchImages(searchString).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun setSearchViewListener(){
        binding.searchView.apply {
            addTextChangedListener { text: Editable? ->
                binding.cancelSearch.isVisible = text.toString().isNotEmpty()
            }
            setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = text.toString().trim()
                    if(query.isNotEmpty()) {
                        searchImages(query,true)
                    }else{
                        binding.searchView.text = null
                    }
                    hideSoftKeyboard()
                    return@OnEditorActionListener true
                }
                false
            })
        }
        binding.cancelSearch.setOnClickListener {
            binding.searchView.text = null
            showSoftKeyboard()
        }
    }

    private fun startNavigation(image: Image, imageView: ImageView) {
        viewModel.selectedImage = image
        val extras = FragmentNavigatorExtras(
            imageView to image.largeImageURL
        )
        showConfirmDialogAlertDialog({navigate(extras)}, image.user)
    }

    private fun navigate(extras:FragmentNavigator.Extras){
        val action = ImagesListFragmentDirections.toImageDetailFragment()
        findNavController().navigate(action, extras)
        changeStatusBarColorToBlack()
    }


    private fun changeStatusBarColorToBlack(){
        requireActivity().apply {
            changeStatusBar(false)
        }
    }

    private fun retry() {
        adapter.retry()
    }

    private fun hideSoftKeyboard() {
        requireActivity().apply {
            WindowInsetsControllerCompat(window, window.decorView).hide(Type.ime())
        }
    }

    private fun showSoftKeyboard() {
        requireActivity().apply {
            WindowInsetsControllerCompat(window, window.decorView).show(Type.ime())
        }
    }

    private fun showConfirmDialogAlertDialog(navigate: () -> Unit, user: String) {
        MaterialAlertDialogBuilder(requireContext(),
            R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
            .setMessage("Do you want to see more of this image by ${user}?")
            .setPositiveButton("Confirm") { _, _ -> navigate.invoke()}
            .setNegativeButton("Cancel", null)
            .show()
    }

}