package com.example.bookreaderapp

import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PdfPagerAdapter(private val bitmaps: List<Bitmap>) : RecyclerView.Adapter<PdfPagerAdapter.PdfViewHolder>() {

    inner class PdfViewHolder(val zoomableView: ZoomableImageView) : RecyclerView.ViewHolder(zoomableView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val zoomableView = ZoomableImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
          //  scaleType = ImageView.ScaleType.FIT_CENTER  // Scale to fit the display

        }
        return PdfViewHolder(zoomableView)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        holder.zoomableView.setImageBitmap(bitmaps[position])
    }

    override fun getItemCount() = bitmaps.size
}
