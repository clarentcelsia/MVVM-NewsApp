package com.example.newsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.news.ArticleX

class NewsAdapter():RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<ArticleX>(){
        override fun areItemsTheSame(oldItem: ArticleX, newItem: ArticleX): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: ArticleX, newItem: ArticleX): Boolean {
            return oldItem == newItem
        }
    }

    val asyncDiffer = AsyncListDiffer(this, differCallback)


    class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        companion object{
            fun create(parent: ViewGroup): NewsViewHolder{
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
                return NewsViewHolder(view)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.NewsViewHolder {
        return NewsViewHolder.create(parent)

    }

    override fun onBindViewHolder(holder: NewsAdapter.NewsViewHolder, position: Int) {
        val ivArticleImage = holder.itemView.findViewById<ImageView>(R.id.ivArticleImage)
        val tvTitle = holder.itemView.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = holder.itemView.findViewById<TextView>(R.id.tvDescription)
        val tvSource = holder.itemView.findViewById<TextView>(R.id.tvSource)
        val tvPublishedAt = holder.itemView.findViewById<TextView>(R.id.tvPublishedAt)

        val articles = asyncDiffer.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(articles.urlToImage).into(ivArticleImage)
            tvTitle.text = articles.title
            tvDescription.text = articles.description
            tvSource.text = articles.source?.name
            tvPublishedAt.text = articles.publishedAt

            //setOnClickListener
            setOnClickListener {
                onItemCallback?.let {articleX ->
                    articleX(articles)
                }
            }
        }
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size

    private var onItemCallback: ((ArticleX)->Unit)? = null
    //listener
    fun setOnItemCallback(listener:(ArticleX)->Unit){
        onItemCallback = listener
    }

}