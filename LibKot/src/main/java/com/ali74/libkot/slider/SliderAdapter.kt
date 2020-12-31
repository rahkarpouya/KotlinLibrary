package com.ali74.libkot.slider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.ali74.libkot.R
import com.ali74.libkot.utils.DP
import com.google.android.material.card.MaterialCardView

class SliderAdapter<T>(
    private val models: MutableList<T>,
    private val margin: Int,
    private val radius: Float,
    private val elevation: Float,
    private val width: Int,
    private val height: Int,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var slider: SliderHolder<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ImageHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_slider_image, parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        slider.sliderData(models[position % models.size], holder as SliderAdapter<*>.ImageHolder)
    }

    override fun getItemCount(): Int = if (models.size > 1) Int.MAX_VALUE else models.size

    inner class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardSlider0: MaterialCardView = view.findViewById(R.id.cardSlider0)
        val image: AppCompatImageView = view.findViewById(R.id.ivSlider0)
        val progressBar: ProgressBar = view.findViewById(R.id.prbSlider0)

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

    interface SliderHolder<T> {
        fun sliderData(data: T, view: SliderAdapter<*>.ImageHolder)
    }

    fun fetchData(slider: SliderHolder<T>) {
        this.slider = slider
    }


}