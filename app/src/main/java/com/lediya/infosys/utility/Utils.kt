package com.lediya.infosys.utility

import android.content.Context
import android.net.ConnectivityManager

object Utils{
    /*
    Check the internet activity in the application **/
    fun  isConnectedToNetwork(context:Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting() ?: false
    }

}