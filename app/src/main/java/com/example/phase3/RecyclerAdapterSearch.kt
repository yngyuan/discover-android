package com.example.phase3

import android.util.Log
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
import kotlinx.android.synthetic.main.card_manage.view.*


//import com.squareup.picasso.Picasso

class RecyclerAdapterSearch(private val reports: ArrayList<Report>) :
    RecyclerView.Adapter<RecyclerAdapterSearch.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val card_view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_search, parent, false)
        return RecyclerViewHolder(card_view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

//        holder.itemView.delete_button.setOnClickListener { view->
//            Log.d("onBindViewHolder", reports[position].r_title)
//        }

        holder.report_title.text = reports[position].r_title
        holder.report_username.text = reports[position].r_username
        holder.report_tname.text = reports[position].r_tname
        var tags: String = ""
        for (_tag in reports[position].r_tag_list) {
            tags += "#" + _tag + "#"
        }
        holder.report_tag_list.text = tags
        holder.report_location.text = reports[position].r_location.toString()
        holder.report_description.text = reports[position].r_description
        holder.report_time.text = reports[position].r_time_str
        // Ref: https://stackoverflow.com/a/51772213
        /*Picasso.get().load(reports[position].r_img_url).fit().placeholder(R.mipmap.ic_launcher).
            into(holder.report_img)*/
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        Glide.with(holder.itemView.context).applyDefaultRequestOptions(requestOptions)
            .load(reports[position].r_img_url).into(holder.report_img)
    }

    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var report_img = itemView.findViewById<ImageView>(R.id.report_img)
        var report_title = itemView.findViewById<TextView>(R.id.report_title)
        var report_description = itemView.findViewById<TextView>(R.id.report_description)
        var report_location = itemView.findViewById<TextView>(R.id.report_location)
        var report_tag_list = itemView.findViewById<TextView>(R.id.report_tag)
        var report_time = itemView.findViewById<TextView>(R.id.report_time)
        var report_tname = itemView.findViewById<TextView>(R.id.report_tname)
        var report_username = itemView.findViewById<TextView>(R.id.report_user)
    }

    override fun getItemCount(): Int = reports.size
}