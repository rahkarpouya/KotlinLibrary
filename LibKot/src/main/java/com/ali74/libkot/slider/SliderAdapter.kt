package com.ali74.libkot.slider

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ali74.libkot.R
import com.ali74.libkot.utils.DP
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView

class SliderAdapter(
    private val models: MutableList<SlideShow>,
    private val margin: Int,
    private val radius: Float,
    private val elevation: Float,
    private val width: Int,
    private val height: Int,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var onclickListener:OnclickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ImageHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_slider_image, parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ImageHolder
        holder.image.imageLoader(models[position % models.size].ImageUrl)
        holder.image.setOnClickListener {
            onclickListener.onSliderClicked(models[position % models.size])
        }
    }

    override fun getItemCount(): Int = if (models.size > 1) Int.MAX_VALUE else models.size

    inner class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardSlider0: MaterialCardView = view.findViewById(R.id.cardSlider0)
        val image: AppCompatImageView = view.findViewById(R.id.ivSlider0)

        init {
            val layoutParams = cardSlider0.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.width = width
            layoutParams.height = height
            layoutParams.setMargins(margin.DP, 0, margin.DP, 0)
            cardSlider0.layoutParams = layoutParams

            cardSlider0.radius = radius.DP
            cardSlider0.cardElevation = elevation
            cardSlider0.maxCardElevation = elevation
        }
    }

    interface OnclickListener{
        fun onSliderClicked(Slider:SlideShow)
    }

    fun setOnclick(onclickListener:OnclickListener){
        this.onclickListener = onclickListener
    }


    private fun AppCompatImageView.imageLoader(Picture: String?) {
        if (!Picture.isNullOrEmpty()) {
            val options= RequestOptions().placeholder(R.drawable.ic_image).error(R.drawable.ic_image_crash)
            Glide.with(this.context).setDefaultRequestOptions(options).load(Picture)
                .centerCrop().into(this)
        }
    }


}