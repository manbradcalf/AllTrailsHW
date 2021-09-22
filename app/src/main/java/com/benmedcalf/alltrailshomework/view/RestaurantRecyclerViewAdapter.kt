package com.benmedcalf.alltrailshomework.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.benmedcalf.alltrailshomework.BuildConfig.apikey
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.databinding.FragmentItemBinding
import com.benmedcalf.alltrailshomework.model.Restaurant
import com.bumptech.glide.Glide

class RestaurantRecyclerViewAdapter(
    private val values: ArrayList<Restaurant>,
    private val nav: NavController,
) : RecyclerView.Adapter<RestaurantRecyclerViewAdapter.ViewHolder>() {
    // This is set by the ViewModel
    var updateFavoriteStatus: ((Restaurant) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = values[position]
        holder.rating.numStars = restaurant.rating.toInt()
        holder.name.text = restaurant.name
        holder.contentView.tag = restaurant
        holder.supportingText.text = restaurant.formatPrice(restaurant.priceLevel)
        holder.contentView.setOnClickListener {
            nav.navigate(
                ListResultsFragmentDirections.actionListResultsFragmentToDetailFragment(
                    restaurant
                )
            )
        }

        holder.heart.setOnClickListener {
            setHeartIcon(it, !restaurant.isFavorite)
            updateFavoriteStatus?.invoke(restaurant)
        }

        setHeartIcon(holder.heart, restaurant.isFavorite)
        restaurant.photoReference?.let {
            setImageThumbnail(holder.imageView, it)
        }
    }

    private fun setHeartIcon(heart: View, isFavorite: Boolean) {
        if (isFavorite) {
            heart.setBackgroundResource(R.drawable.ic_favorited)
        } else {
            heart.setBackgroundResource(R.drawable.ic_unfavorited)
        }
    }

    private fun setImageThumbnail(thumbnail: ImageView, imageReference: String) {
        val photoUrl =
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=${imageReference}&key=${apikey}"
        Glide.with(thumbnail.context).load(photoUrl).into(thumbnail)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var contentView = binding.root
        var imageView: ImageView = binding.itemImage
        var name: TextView = binding.itemName
        var rating: RatingBar = binding.itemRating
        var supportingText: TextView = binding.itemSupportingText
        var heart: ImageButton = binding.heart
    }
}