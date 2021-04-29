package com.anandmali.commits.util

import com.anandmali.commits.api.model.RepoDetailsModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*

fun getMonth(item: RepoDetailsModel): String {

    val stringDate = item.commit.committer.date

    val format: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    format.timeZone = TimeZone.getTimeZone("UTC")
    val calendar = GregorianCalendar.getInstance()

    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val instant: Instant = Instant.parse(stringDate)
        instant.atZone(ZoneId.of("UTC")).month.toString()
    } else {
        val date = format.parse(stringDate)
        calendar.time = date!!
        calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())!!
    }

}