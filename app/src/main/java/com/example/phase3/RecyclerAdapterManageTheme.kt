package com.example.phase3

import com.example.recycler.Theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.recycler.Report



//import com.squareup.picasso.Picasso

class RecyclerAdapterManageTheme(private val themes: ArrayList<Theme>) :
    RecyclerView.Adapter<RecyclerAdapterManageTheme.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val card_view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_manage_theme, parent, false)
        return RecyclerViewHolder(card_view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.theme_name.text = themes[position].t_name
        //var tags: String = ""
        //for (_tag in reports[position].r_tag_list) {
        //    tags += "#" + _tag + "#"
        //}
        holder.theme_description.text = themes[position].t_description
        // Ref: https://stackoverflow.com/a/51772213
        /*Picasso.get().load(reports[position].r_img_url).fit().placeholder(R.mipmap.ic_launcher).
            into(holder.report_img)*/
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        Glide.with(holder.itemView.context).applyDefaultRequestOptions(requestOptions)
            .load(themes[position].t_coverimg).into(holder.theme_cover_image)
    }

    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var theme_cover_image = itemView.findViewById<ImageView>(R.id.theme_img_manage)
        var theme_name = itemView.findViewById<TextView>(R.id.theme_title_manage)
        var theme_description = itemView.findViewById<TextView>(R.id.theme_description_manage)
    }

    override fun getItemCount(): Int = themes.size
}