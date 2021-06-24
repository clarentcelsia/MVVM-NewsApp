package com.example.newsapp.fragment

import android.content.ClipData
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.MainActivity
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.databinding.FragmentBookmarkBinding
import com.example.newsapp.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var newsModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsModel = (activity as MainActivity).newsViewModel

        //setup recyclerview
        setupRecyclerView()

        //setup webview
        gotoWeb()

        //ui viewmodel
        //observe if there's a data change
        newsModel.getBookmarkedNews().observe(viewLifecycleOwner, {
            newsAdapter.asyncDiffer.submitList(it)
        })

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvBookmark)
        }
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //delete news
            val selectedNews = newsAdapter.asyncDiffer.currentList[viewHolder.adapterPosition]
            newsModel.deleteNews(selectedNews)

            Snackbar.make(binding.root, "Your article was successfully deleted!", Snackbar.LENGTH_SHORT)
                .apply {
                    setAction("undo") {
                        newsModel.saveBookmarkedNews(selectedNews)
                    }
                    show()
                }
        }

    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        binding.rvBookmark.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = newsAdapter
        }
    }

    private fun gotoWeb(){
        newsAdapter.setOnItemCallback {
            val bundle = Bundle().apply {
                putSerializable("articles", it)
            }
            findNavController().navigate(R.id.action_bookmarkFragment_to_webFragment, bundle)
        }
    }
}