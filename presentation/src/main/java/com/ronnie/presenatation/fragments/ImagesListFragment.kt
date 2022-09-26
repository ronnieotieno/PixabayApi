package com.ronnie.presenatation.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import com.ronnie.presenatation.R
import com.ronnie.presenatation.adapters.ImagesAdapter
import com.ronnie.presenatation.adapters.LoadingStateAdapter
import com.ronnie.presenatation.databinding.FragmentImageListBinding
import com.ronnie.presenatation.model.ImagePresentation
import com.ronnie.presenatation.utils.IMAGE_VIEW_TYPE
import com.ronnie.presenatation.utils.ItemOffsetDecoration
import com.ronnie.presenatation.utils.changeStatusBar
import com.ronnie.presenatation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImagesListFragment : Fragment(R.layout.fragment_image_list) {
    private lateinit var binding: FragmentImageListBinding
    private val adapter = ImagesAdapter { image, imageView -> navigate(image, imageView) }
    private val viewModel: MainViewModel by activityViewModels()
    private var gridLayoutSpan = 2
    private var isInitiated = false
    private var job: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentImageListBinding.bind(view)
        setSearchViewListener()
        setUpAdapter()

        if (!isInitiated) init()

        binding.retryBtn.setOnClickListener {
            adapter.refresh()
        }

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

            binding.emptySection.isVisible =
                state.refresh is LoadState.NotLoading && adapter.itemCount == 0
            binding.errorSection.isVisible = state.refresh is LoadState.Error && adapter.snapshot().isEmpty()

        }
    }

    private fun init() {
        isInitiated = true
        searchImages(viewModel.currentSearch)
        binding.searchView.apply {
            setText(viewModel.currentSearch)
            text?.length?.let { setSelection(it) }
        }
    }

    private fun searchImages(searchString: String, isUserInitiated: Boolean = false) {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            if (isUserInitiated) adapter.submitData(PagingData.empty())
            viewModel.searchImages(searchString).collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }


    }

    private fun setSearchViewListener() {
        binding.searchView.apply {
            addTextChangedListener { text: Editable? ->
                binding.cancelSearch.isVisible = text.toString().isNotEmpty()
            }
            setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = text.toString().trim()
                    if (query.isNotEmpty()) {
                        searchImages(query, true)
                    } else {
                        binding.searchView.text = null
                    }
                    hideSoftKeyboard()
                    return@OnEditorActionListener true
                }
                false
            })
        }
        binding.cancelSearch.setOnClickListener {
            binding.searchView.apply {
                text = null
                requestFocus()
            }
            showSoftKeyboard()
        }
    }

    private fun navigate(image: ImagePresentation, imageView: ImageView) {
        viewModel.selectedImage = image
        val extras = FragmentNavigatorExtras(
            imageView to image.largeImageURL
        )
        val action = ImagesListFragmentDirections.toImageDetailFragment()
        findNavController().navigate(action, extras)
        changeStatusBarColorToBlack()
    }

    private fun changeStatusBarColorToBlack() {
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

    override fun onResume() {
        super.onResume()
        requireActivity().changeStatusBar(true)
        hideSoftKeyboard()
    }
}