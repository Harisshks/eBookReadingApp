package com.example.bookreaderapp

import android.graphics.Bitmap
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView


class PdfPagerAdapter(private val bitmaps: List<Bitmap>) : RecyclerView.Adapter<PdfPagerAdapter.PdfViewHolder>() {

    inner class PdfViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        return PdfViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        holder.imageView.setImageBitmap(bitmaps[position])
    }

    override fun getItemCount() = bitmaps.size
}
