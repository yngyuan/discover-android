
import android.app.PendingIntent.getActivity
import android.content.Context
import android.provider.Settings.Global.getString
import com.example.recycler.Theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.phase3.CustomJsonObjectRequest
import com.example.phase3.R
import com.example.recycler.Report
import org.json.JSONObject
import java.lang.Exception


//import com.squareup.picasso.Picasso

class ThemeRecyclerAdapter(private val themes: ArrayList<Theme>, private val uid: String,
                           private val url:String) :
    RecyclerView.Adapter<ThemeRecyclerAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val card_view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_discover, parent, false)
        return RecyclerViewHolder(card_view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.theme_name.text = themes[position].t_name
        holder.theme_description.text = themes[position].t_description
        holder.button.setOnClickListener(View.OnClickListener {
            var t_name = themes[position].t_name
//            var u_id = "5daf8d2f72eb94efe98bd904"
            var subscribe_clicked = "1"
            val jsonObj = JSONObject()
            // jsonObj.put("uid", u_id)
            jsonObj.put("t_name", t_name);
            jsonObj.put("subscribe_clicked", subscribe_clicked);
            jsonObj.put("uid", uid)

            //var jsonSubscribe = JSONObject("""{"t_name": "Pink", "subscribe_clicked": "1"}""")
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(holder.button.context)
            // ifconfig en0
//            url = "http://${homepage}/viewOneMobile"

            // Request a response from the provided URL.
            var postRequest: JsonObjectRequest = CustomJsonObjectRequest(
                Request.Method.POST, url, jsonObj,
                Response.Listener<JSONObject> { response ->
                    try {
                        // Parse the json object here
                        try{
                            subscribe_clicked = response.getString("subscribe_clicked")
                        }catch(e :Exception){

                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                        var message = e.printStackTrace()
                    }
                },
                Response.ErrorListener{
                    var message = "Volly error: $it}"}, holder.button.context)

            // Add the request to the RequestQueue.
            queue.add(postRequest)


            // make a toast to user
            var toast = Toast.makeText(holder.button.context, "subscribed", Toast.LENGTH_SHORT)
            toast.show()
        })
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
        var theme_cover_image = itemView.findViewById<ImageView>(R.id.theme_img)
        var theme_name = itemView.findViewById<TextView>(R.id.theme_title)
        var theme_description = itemView.findViewById<TextView>(R.id.theme_description)
        var button = itemView.findViewById<Button>(R.id.subscribe_button)
    }

    override fun getItemCount(): Int = themes.size
}