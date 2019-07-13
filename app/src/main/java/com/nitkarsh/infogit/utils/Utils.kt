package com.nitkarsh.infogit.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import android.content.Context.INPUT_METHOD_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.content.Intent
import android.net.Uri


object Utils {

    @SuppressLint("SimpleDateFormat")
    fun convertDateViaFormatTZ(dateTime: String): String {
        var dateTime = dateTime
        val sdfFrom = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfTo = SimpleDateFormat("dd MMM, yyyy h:mm a")
        try {
            dateTime =
                dateTime.replace("T", " ").split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val myDate = sdfFrom.parse(dateTime)
            return sdfTo.format(myDate)
        } catch (e1: Exception) {
            e1.printStackTrace()
            return convertDate(dateTime)
        }
    }

    private fun convertDate(dateTime: String): String {
        try {
            val date = dateTime.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

            val year = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            var month = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            val day = date.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2]

            if ("01".equals(month, ignoreCase = true)) {
                month = "Jan"
            } else if ("02".equals(month, ignoreCase = true)) {
                month = "Feb"
            } else if ("03".equals(month, ignoreCase = true)) {
                month = "Mar"
            } else if ("04".equals(month, ignoreCase = true)) {
                month = "Apr"
            } else if ("05".equals(month, ignoreCase = true)) {
                month = "May"
            } else if ("06".equals(month, ignoreCase = true)) {
                month = "Jun"
            } else if ("07".equals(month, ignoreCase = true)) {
                month = "Jul"
            } else if ("08".equals(month, ignoreCase = true)) {
                month = "Aug"
            } else if ("09".equals(month, ignoreCase = true)) {
                month = "Sept"
            } else if ("10".equals(month, ignoreCase = true)) {
                month = "Oct"
            } else if ("11".equals(month, ignoreCase = true)) {
                month = "Nov"
            } else if ("12".equals(month, ignoreCase = true)) {
                month = "Dec"
            }

            val time = dateTime.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

            val hour = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val min = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

            val amOrpm = if (Integer.parseInt(hour) >= 12) "PM" else "AM"
            var newHour = ""

            if (Integer.parseInt(hour) == 0) {
                newHour = "12"
            } else if (Integer.parseInt(hour) <= 12) {
                newHour = hour
            } else {
                newHour = "" + (Integer.parseInt(hour) - 12)
            }

            val finalTime = "$newHour:$min $amOrpm"

            return "$day $month, $year $finalTime"
        } catch (e: Exception) {
            e.printStackTrace()
            return dateTime
        }
    }

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            try {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun openUrl(context: Context, url: String) {
        var url = url
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://$url"

        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        context.startActivity(i)
    }
}