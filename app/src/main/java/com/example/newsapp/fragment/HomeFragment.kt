package com.example.newsapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.util.Common.Companion.QUERY_PAGE_SIZE
import com.example.newsapp.util.Common.Companion.isSaved
import com.example.newsapp.util.Response
import com.example.newsapp.viewmodel.NewsViewModel

class HomeFragment : Fragment() {

    private lateinit var newsAdapter : NewsAdapter
    private lateinit var newsModel: NewsViewModel

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsModel = (activity as MainActivity).newsViewModel

        //setup recyclerview
        setupRecyclerView()

        //setup webview
        goToWeb()

        //UI View Model
        newsModel.latestNews.observe(viewLifecycleOwner, {
            when(it){
                is Response.Success -> {
                    hideProgress()
                    it.data?.let {
                        newsAdapter.asyncDiffer.submitList(it.articles.toList())
                        val totalPages = it.totalResults / QUERY_PAGE_SIZE
                        isLastPage = newsModel.initLatestNewsPage == totalPages
                        if(isLastPage)
                            binding.rvHome.setPadding(0,0,0,0)
                    }
                }
                is Response.Error -> {
                    hideProgress()
                    it.message?.let {
                        Toast.makeText(activity, "Error occured: $it", Toast.LENGTH_SHORT).show()
                    }
                }
                is Response.Loading -> showProgress()
            }
        })
    }

    private fun showProgress(){
        binding.pgProgressBar.visibility = View.VISIBLE
        isLoading = true
    }
    private fun hideProgress(){
        binding.pgProgressBar.visibility = View.GONE
        isLoading = false
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.rvHome.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = newsAdapter

            addOnScrollListener(this@HomeFragment.scrollListener)
        }
    }

    private fun goToWeb(){
        newsAdapter.setOnItemCallback {
            val bundle = Bundle().apply {
                putSerializable(
                    "articles",
                    it
                )
            }
//            isSaved = false
            findNavController().navigate(R.id.action_homeFragment_to_webFragment, bundle)
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    //Handle Page
    val scrollListener = object: RecyclerView.OnScrollListener(){
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
            val isTotalMoreThanVisible = mTotalItem >= QUERY_PAGE_SIZE

            val paginating = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if(paginating){
                newsModel.getLatestNews("bbc-news")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) isScrolling = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}