package com.ming.pagcoiladapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import com.ming.pag.load
import com.ming.pag.view.PAGImageViewLayout
import org.libpag.PAGScaleMode

class MyViewActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_view_layout)
        rv = findViewById(R.id.recycler_view)
        rv.run {
            adapter = Adapter()
            layoutManager = GridLayoutManager(this@MyViewActivity, 2)
        }
    }

    private class Adapter : RecyclerView.Adapter<VH>() {

        private val pagData = buildPAGData()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(
                LayoutInflater.from(parent.context).inflate(R.layout.rv_item_pag_image_view, null)
            )
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.v.mImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            holder.v.mPAGView.run {
                setScaleMode(PAGScaleMode.Zoom)
                setRepeatCount(0)
            }
            holder.v.load(
                pagData[position],
                holder.v.context.imageLoader
            ) {
                placeholder(R.drawable.pexels_ian_turnell)
                error(R.drawable.pexels_ian_turnell)
            }
        }

        override fun getItemCount(): Int {
            return pagData.size
        }
    }

    private class VH(view: View) : RecyclerView.ViewHolder(view) {
        val v: PAGImageViewLayout = view.findViewById(R.id.pag_image_view_layout)
    }

}