package com.ali74.libkot.city

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ali74.libkot.R
import com.ali74.libkot.recyclerview.RVDividerItemDecoration
import com.google.android.material.textview.MaterialTextView

class CityDialog(
    context: Context,
    private val models: MutableList<City>,
    private val itemSelected: Int ,
    private var title: String = "لطفا شهر خود را انتخاب کنید"
) : Dialog(context) {

    private val rclCityDialog by lazy { findViewById<RecyclerView>(R.id.rclCityDialog) }
    private val txtTitle by lazy { findViewById<TextView>(R.id.textViewTitle) }

    private lateinit var setOnItemClick: SetOnItemClick

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_recycler_view)
        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
        setCancelable(false)
        rclCityDialog.layoutManager = LinearLayoutManager(context)
        rclCityDialog.addItemDecoration(RVDividerItemDecoration(RecyclerView.VERTICAL))
        txtTitle.text = title

        val adapter = CityAdapter()
        rclCityDialog.adapter = adapter
    }

    private inner class CityAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return CityHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
            )
        }

        override fun getItemCount(): Int = models.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is CityHolder) {
                holder.fetchData()
            }
        }

        inner class CityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val txtCity: MaterialTextView = itemView.findViewById(R.id.txtCity)
            private val ivCheckCity: AppCompatImageView = itemView.findViewById(R.id.ivCheckCity)
            fun fetchData() {
                txtCity.text = models[adapterPosition].title

                txtCity.gravity = Gravity.START
                txtCity.textSize = 14f

                if (itemSelected != 0) {
                    if (itemSelected == models[adapterPosition].id) ivCheckCity.visibility = View.VISIBLE
                    else ivCheckCity.visibility = View.GONE
                } else ivCheckCity.visibility = View.GONE




                itemView.setOnClickListener {
                    setOnItemClick.setOnclick(
                        txtCity.text.toString().trim(),
                        models[adapterPosition].id
                    )
                }
            }
        }

    }

    interface SetOnItemClick {
        fun setOnclick(title: String, itemSelected: Int)
    }

    fun setOnClickListener(setOnItemClick: SetOnItemClick) {
        this.setOnItemClick = setOnItemClick
    }


    override fun onBackPressed() {
        dismiss()
    }
}