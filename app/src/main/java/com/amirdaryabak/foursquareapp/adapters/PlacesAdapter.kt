package com.amirdaryabak.foursquareapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amirdaryabak.foursquareapp.R
import com.amirdaryabak.foursquareapp.models.Venue
import kotlinx.android.synthetic.main.places_item.view.*

class PlacesAdapter : RecyclerView.Adapter<PlacesAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Venue>() {
        override fun areItemsTheSame(oldItem: Venue, newItem: Venue): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Venue, newItem: Venue): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.places_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentVenue = differ.currentList[position]
        holder.itemView.apply {
            place_name.text = currentVenue.name
            setOnClickListener {
                onItemClickListener?.let { it(currentVenue) }
            }
        }
    }

    private var onItemClickListener: ((Venue) -> Unit)? = null

    fun setOnItemClickListener(listener: (Venue) -> Unit) {
        onItemClickListener = listener
    }
}