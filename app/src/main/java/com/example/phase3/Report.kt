package com.example.recycler

import android.location.Location

data class Report(
    var r_img_url: String = "",
    var r_id: String,
    var r_title: String = "",
    var r_uid: String,
    var r_username: String,
    var r_time_str: String,
    var r_location: Pair<Double, Double>,
    var r_tname: String,
    var r_description: String,
    var r_tag_list: Array<String>
)