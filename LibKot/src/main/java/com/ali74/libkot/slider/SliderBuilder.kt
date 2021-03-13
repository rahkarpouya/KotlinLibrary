package com.ali74.libkot.slider

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.ali74.libkot.utils.DP
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class SliderBuilder( private  val context: Context) {

    private lateinit var sliderManager: LinearLayoutManager
    private val sliderSize = AtomicInteger(0)
    private var sliderTimer: Timer? = null
    private var sliderTask: TimerTask? = null

    private lateinit var onclickListener:OnclickListener

    private var recyclerView: RecyclerView? = null
    private var reverseLayout = true
    private var useCarouselLayoutManager = false
    private var timerDelay: Long = 5000
    private var models: MutableList<SlideShow> = mutableListOf()
    private var itemMargin = 0
    private var radius = 0f
    private var itemElevation = 0f
    private var itemWidth = ViewGroup.LayoutParams.MATCH_PARENT
    private var itemHeight = 200.DP
    private var autoScroll = true

    fun setRecyclerView(recyclerView: RecyclerView) = apply { this.recyclerView = recyclerView }

    fun setTimer(timer: Long) = apply { timerDelay = timer }

    fun setModels(models: MutableList<SlideShow>) = apply { this.models = models }

    fun setMarginItem(margin: Int) = apply { itemMargin = margin.DP }

    fun setRadius(radius: Float) = apply { this.radius = radius.DP }

    fun setItemElevation(elevation: Float) = apply { itemElevation = elevation.DP }

    fun setItemWidth(width: Int) = apply { itemWidth = width.DP }

    fun setItemHeight(height: Int) = apply { itemHeight = height.DP }

    fun setAutoScroll(autoScroll: Boolean) = apply { this.autoScroll = autoScroll }

    fun setReverseLayout(reverseLayout: Boolean) = apply { this.reverseLayout = reverseLayout }

    fun useCarouselLayoutManager(use: Boolean) = apply { useCarouselLayoutManager = use }

    fun setOnclick(onclickListener:OnclickListener) = apply {
        this.onclickListener = onclickListener
    }

    fun create() {

        sliderManager = if (useCarouselLayoutManager)
            CarouselLayoutManager(context, RecyclerView.HORIZONTAL, reverseLayout)
        else LinearLayoutManager(context, RecyclerView.HORIZONTAL, reverseLayout)

        val sliderAdapter = SliderAdapter(
            models,
            itemMargin,
            radius,
            itemElevation,
            itemWidth,
            itemHeight
        )
        sliderSize.set(models.size)
        recyclerView?.apply {
            layoutManager = sliderManager
            onFlingListener = null
            adapter = sliderAdapter
            val snapHelper: SnapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this)
            addOnScrollListener(InfiniteScroller(sliderManager, models.size))

            if (autoScroll && models.size > 1)
                schedule()
        }

        sliderAdapter.setOnclick(object :SliderAdapter.OnclickListener{
            override fun onSliderClicked(Slider: SlideShow) {
                onclickListener.onSliderClicked(Slider)
            }

        })

    }


    private fun schedule() {
        stopSlider()

        if (sliderSize.get() == 0) return

        sliderTimer = Timer()
        sliderTask = object : TimerTask() {
            override fun run() {
                recyclerView?.apply {
                    post {
                        scrollToNextSlide()
                    }
                }
            }
        }
        sliderTimer?.schedule(sliderTask, timerDelay)
    }

    private fun stopSlider() {
        sliderTask?.cancel()
        sliderTask = null
        sliderTimer?.cancel()
        sliderTimer = null
    }

    private fun scrollToNextSlide() {
        val position = sliderManager.findFirstCompletelyVisibleItemPosition()
        if (sliderSize.get() != 0 && position != RecyclerView.NO_POSITION) {
            recyclerView?.apply {
                smoothScrollToPosition(position + 1)
            }
        }
        schedule()
    }


    interface OnclickListener{
        fun onSliderClicked(slider:SlideShow)
    }




}