package com.ali74.samplelibrary

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ali74.libkot.slider.SliderAdapter
import com.ali74.libkot.slider.SliderBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val models = mutableListOf<String>()
        models.add("https://wl-brightside.cf.tsp.li/resize/728x/jpg/f2c/662/7fb5c25bc493f5189bf3c7b0df.jpg")
        models.add("https://s31242.pcdn.co/wp-content/uploads/2019/05/Personality-picture-test-1.jpg")
        models.add("https://www.test-english.com/img/test-english-home-Level-test.jpg")

        SliderBuilder<String>(this)
            .setModels(models)
            .setRecyclerView(findViewById(R.id.txtTest))
            .setItemWidth(300)
            .setAutoScroll(true)
            .setRadius(3f)
            .setTimer(3000)
            .useCarouselLayoutManager(true)
            .create(object : SliderAdapter.SliderHolder<String> {
                override fun sliderData(data: String, view: SliderAdapter<*>.ImageHolder) {
                    Glide.with(this@MainActivity)
                        .load(data)
                        .centerCrop()
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                view.progressBar.visibility = View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                view.progressBar.visibility = View.GONE
                                return false
                            }
                        })
                        .into(view.image)
                }
            })


    }
}