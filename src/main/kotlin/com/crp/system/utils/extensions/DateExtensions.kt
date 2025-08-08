package com.crp.system.utils.extensions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

enum class DateFormatType(val format: String) {
    responseLong("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
    longDateAndLongTime("yyyy-MM-dd HH:mm:ss.SSSSSS"),
    horseEventDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"),
    longDateAndShortTime("yyyy-MM-dd HH:mm:ss"),
    headerTime("EEE, d MMM yyyy HH:mm:ss Z"),
    responseLongWithoutMs("yyyy-MM-dd'T'HH:mm:ss'Z'"),
    responseLongWithoutMsFallback("yyyy-MM-dd'T'HH:mm:ssXXX"),
    longDateAndShortTimeSlash("dd/MM/yyyy HH:mm:ss"),
    longDateAndShortTimeSlashAmPm("dd/MM/yyyy hh:mm:ss a"),
    transactionHistory("dd/MM//yyyy - HH:mm:ss"),
    inboxMessageDate("yyyy-MM-dd'T'HH:mm:ssZ"),
    longDateWithHour("MM/dd/yyyy HH:mm"),
    longDateWithHourWithDayFirst("dd/MM/yyyy HH:mm"),
    delay("HH:mm:ss.SSSSSSS"),
    timeDashSeperated("HH-mm"),
    transctionFormat("MMMM yyyy"),
    transactionCellDate("dd EEEE yyyy"),
    novileagueDate("dd MMMM yyyy"),
    longDate("yyyy-MM-dd"),
    longDateReverse("dd-MM-yyyy"),
    shortTime("HH:mm"),
    minutesSecondsTime("mm:ss"),
    longTime("HH:mm:ss"),
    longDateSlash("dd/MM/yyyy"),
    longMonthFirst("MMM dd, yyyy HH:mm"),
    longDateDot("dd.MM.yyyy"),
    bannerTime("HH:mm"),
    preLiveEventDateTime("EEEE dd/MM"),//here
    preLiveDate("dd/MM"),//here
    timeformat("HH:mm:ss"),
    transactionMonthsCellDate("MMMM"),
    month("MM"),
    year("yyyy"),
    day("dd"),
    preLiveCouponEventDateTime("EEEE dd MMMM"),
    tournamentFormat("EEEE dd/MM/yyyy"),
    preLiveCouponFilterDateTime("EEEE d/M")
}

fun String.toDate(type: DateFormatType, withTimeZoneConvert: Boolean = false): Date? {
    val format = SimpleDateFormat(type.format, Locale.US)
    if (withTimeZoneConvert) {
        format.timeZone = TimeZone.getTimeZone("UTC")
    }
    return try {
        format.parse(this)
    } catch (e: ParseException) {
        null
    }
}

fun String.toDateWithLocale(type: DateFormatType, locale: Locale): Date? {
    val format = SimpleDateFormat(type.format, locale)
    return try {
        format.parse(this)
    } catch (e: ParseException) {
        null
    }

}


fun Date.toStringFormat(type: DateFormatType, locale: Locale = Locale.US, withTimeZoneConvert: Boolean = false): String? {
    val format = SimpleDateFormat(type.format, locale)
    if (withTimeZoneConvert) {
        format.timeZone = TimeZone.getDefault()
    }
    return try {
        format.format(this)
    } catch (e: ParseException) {
        null
    }
}

//2021-03-26T11:00:46+00:00
fun String.toDateString(inputType: DateFormatType, outputType: DateFormatType, locale: Locale = Locale.US, withTimeZoneConvert: Boolean = false): String? {
    val date = this.toDate(inputType, withTimeZoneConvert)
    return date?.toStringFormat(outputType, locale, withTimeZoneConvert)
}

fun String.toStringWithDay(): String {
    val inputDate = this.toDate(DateFormatType.responseLongWithoutMs)
            ?: this.toDate(DateFormatType.responseLongWithoutMsFallback)
    val formatter = SimpleDateFormat(DateFormatType.preLiveDate.format, Locale.US)
    val date = formatter.format(inputDate ?: return "")
    return inputDate?.actualDay().plus(" ").plus(date)
}

fun String.toFreeBetFallback(dateFormatTypeOld: DateFormatType, dateFormatTypeNew: DateFormatType, formatter: SimpleDateFormat): String {
    val inputDate = this.toDate(dateFormatTypeOld) ?: this.toDate(dateFormatTypeNew)
    val date = formatter.format(inputDate ?: return "")
    return date.toString()
}

fun Date.actualDay(): String {
    val sdf = SimpleDateFormat("EEEE")
    return sdf.format(this).take(3)
}


fun Calendar.getDaysBefore(days: Int): Date {
    val calendar = this
    calendar.add(Calendar.DAY_OF_MONTH, -days)
    return calendar.time
}

fun Long.millisToDateFormatString(): (Pair<String, Locale>) -> String = { pair ->
    val (dateFormat, locale) = pair
    try {
        SimpleDateFormat(dateFormat, locale).format(this)
    } catch (e: Exception) {
        ""
    }
}

fun String.smartCastDate(vararg dateFormatTypes: DateFormatType, withTimeZoneConvert: Boolean = false): Date? {
    var inputDate = this.toDate(dateFormatTypes[0])
    var i = 1
    while (inputDate == null && i <= dateFormatTypes.lastIndex) {
        inputDate = this.toDate(dateFormatTypes[i])
        i++
    }
    if (inputDate != null) return inputDate
    i = 0
    val allTypes = DateFormatType.values()
    while (inputDate == null && i <= allTypes.lastIndex) {
        inputDate = this.toDate(allTypes[i], withTimeZoneConvert)
        i++
    }
    return inputDate
}

fun String.smartCastTime(vararg dateFormatTypes: DateFormatType, outputType: DateFormatType, locale: Locale = Locale.US, withTimeZoneConvert: Boolean = false): String {
    var inputDate = this.toDate(dateFormatTypes[0], withTimeZoneConvert)
    var i = 1
    while (inputDate == null && i <= dateFormatTypes.lastIndex) {
        inputDate = this.toDate(dateFormatTypes[i], withTimeZoneConvert)
        i++
    }
    if (inputDate != null) return (inputDate.toStringFormat(outputType, locale, withTimeZoneConvert)).toString()
    i = 0
    val allTypes = DateFormatType.values()
    while (inputDate == null && i <= allTypes.lastIndex) {
        inputDate = this.toDate(allTypes[i], withTimeZoneConvert)
        i++
    }
    val dateString = inputDate?.toStringFormat(outputType, locale, withTimeZoneConvert)
    return dateString.toString()
}

fun String.fallbackTime(dateFormatTypeOld: DateFormatType, dateFormatTypeNew: DateFormatType, outputType: DateFormatType, locale: Locale = Locale.US, withTimeZoneConvert: Boolean = false): String {
    val inputDate = this.toDate(dateFormatTypeOld) ?: this.toDate(dateFormatTypeNew)
    val dateString = inputDate?.toStringFormat(outputType, locale, withTimeZoneConvert)
    return dateString.toString()
}