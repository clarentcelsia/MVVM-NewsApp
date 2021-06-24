package com.example.newsapp.fragment

import android.app.SearchManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import android.app.Activity
import android.content.Context.SEARCH_SERVICE
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.databinding.FragmentSearchBinding
import com.example.newsapp.util.Common
import com.example.newsapp.util.Common.Companion.SEARCH_DELAY
import com.example.newsapp.util.Response
import com.example.newsapp.viewmodel.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var newsModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsModel = (activity as MainActivity).newsViewModel

        //setup recyclerview
        setupRecyclerView()

        //setup webview
        gotoWeb()

        //UI Search Listener
        searchListener()

        //ViewModel
        searchViewModel()

    }

    private fun searchViewModel() {
        newsModel.searchNews.observe(viewLifecycleOwner, {
            when (it) {
                is Response.Success -> {
                    hideProgress()
                    it.data?.let {
                        newsAdapter.asyncDiffer.submitList(it.articles.toList()) // **
                        val totalPages = it.totalResults / Common.QUERY_PAGE_SIZE
                        isLastPage = newsModel.initSearchNewsPage == totalPages
                        if (isLastPage) {
                            //MAKE PROGRESS OVERLAP
                            binding.rvSearch.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Response.Error -> {
                    hideProgress()
                    it.message?.let {
                        Toast.makeText(activity, "Error occured: ${it}", Toast.LENGTH_SHORT).show()
                    }
                }
                is Response.Loading -> showProgress()

            }
        })
    }


    // to handle many requests created while typing -> delaying
    private fun searchListener() {
        var job: Job? = null
        binding.etSearch.addTextChangedListener {
            //while typing, cancel current job
            job?.cancel()
            job = MainScope().launch{
                delay(SEARCH_DELAY)
                it?.let {
                    if (it.toString().isNotEmpty())
                        newsModel.getSearchedNews(it.toString())
                }
            }
        }
    }

    private fun gotoWeb() {
        newsAdapter.setOnItemCallback {
            val bundle = Bundle().apply {
                putSerializable("articles", it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_webFragment, bundle)
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = newsAdapter

            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private fun hideProgress() {
        binding.pgProgressBar.visibility = View.GONE
        isLoading = false
    }

    private fun showProgress() {
        binding.pgProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    //Handle Page
    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            // to know whether the list has reached bottom or not
            val mLayout = recyclerView.layoutManager as LinearLayoutManager
            val firstItem = mLayout.findFirstVisibleItemPosition()

            val mVisible = mLayout.childCount
            val mTotalItem = mLayout.itemCount

            // to check if there is more news, scroll to load news until no more news in API
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstItem + mVisible >= mTotalItem
            val isNotAtBeginning = firstItem >= 0
            val isTotalMoreThanVisible = mTotalItem >= Common.QUERY_PAGE_SIZE

            val paginating =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (paginating) {
                newsModel.getSearchedNews(binding.etSearch.text.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) isScrolling =
                true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}