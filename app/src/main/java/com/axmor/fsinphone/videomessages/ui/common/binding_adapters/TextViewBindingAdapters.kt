package com.axmor.fsinphone.videomessages.ui.common.binding_adapters

import android.graphics.Typeface
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.extensions.isCurrentYear
import com.axmor.fsinphone.videomessages.common.extensions.isToday
import com.axmor.fsinphone.videomessages.common.extensions.isYesterday
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter(value = ["date", "dateFormat"], requireAll = true)
fun dateFromLong(view: TextView, date: Long, dateFormat: String) =
    dateFromDate(view, Date(date), dateFormat)

@BindingAdapter(value = ["date", "dateFormat"], requireAll = true)
fun dateFromDate(view: TextView, date: Date?, dateFormat: String) {
    if (date == null) {
        view.text = null
        return
    }

    val formattedDate = BindingAdapters.formatDate(dateFormat, date)

    view.text = formattedDate
}

@BindingAdapter("dateGroup")
fun dateGroup(view: TextView, date: Long) {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = date
    }
    val c = view.context
    view.text = when {
        calendar.isToday() -> c.getString(R.string.common_today)
        calendar.isYesterday() -> c.getString(R.string.common_yesterday)
        calendar.isCurrentYear() -> BindingAdapters.formatDate(
            Constants.CHAT_DATE_FORMAT,
            Date(date)
        )
        else -> BindingAdapters.formatDate(Constants.CHAT_DATE_FORMAT_WITH_YEAR, Date(date))
    }
}

@BindingAdapter("isBold")
fun setBold(view: TextView, isBold: Boolean) {
    if (isBold) {
        view.setTypeface(null, Typeface.BOLD)
    } else {
        view.setTypeface(null, Typeface.NORMAL)
    }
}

@BindingAdapter("stringRes")
fun stringRes(view: TextView, @StringRes stringRes: Int) {
    view.text = view.resources.getString(stringRes)
}

@BindingAdapter("textColorRes")
fun setTextColor(view: TextView, @ColorRes color: Int) {
    view.setTextColor(ContextCompat.getColor(view.context, color))
}

@BindingAdapter(value = ["defaultDate", "newDateFormat"])
fun setFormattedDate(view: TextView, defaultDate: String?, newDateFormat: String) {
    if (defaultDate != null) {
        val oldFormattedDate = "yyyy-MM-dd HH:mm:ss"

        if (oldFormattedDate != null) {
            val newFormattedDate =
                SimpleDateFormat(newDateFormat, Locale("RU")).format(oldFormattedDate)

            view.text =
                view.context.getString(R.string.contact_details_last_activity, newFormattedDate)
        }
    }
}