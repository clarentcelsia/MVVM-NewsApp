package com.example.newsapp.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.annotation.DrawableRes
import androidx.navigation.fragment.navArgs
import com.example.newsapp.MainActivity
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentWebBinding
import com.example.newsapp.news.ArticleX
import com.example.newsapp.util.Common.Companion.isSaved
import com.example.newsapp.viewmodel.NewsViewModel

class WebFragment : Fragment() {

    private var _binding: FragmentWebBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var newsModel: NewsViewModel
    private val args: WebFragmentArgs by navArgs()  //pass data

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsModel = (activity as MainActivity).newsViewModel

        val articles = args.articles
        binding.webView.apply {
            webViewClient = WebViewClient()
            articles.url?.let { loadUrl(it) }
        }
        //button listener
        onClickListener(articles)

    }

    private fun onClickListener(articles:ArticleX){
        binding.fab.setOnClickListener {
//            isSaved = true
            binding.fab.setImageResource(R.drawable.ic_baseline_bookmark_24)
            newsModel.saveBookmarkedNews(articles)
        }
    }
}