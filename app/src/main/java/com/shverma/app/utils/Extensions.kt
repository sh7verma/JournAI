package com.shverma.app.utils

import android.annotation.SuppressLint
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

/**
 * Extension functions for date formatting
 */

// Common date formatters
private val isoDateFormatter = DateTimeFormatter.ISO_DATE
private val isoOffsetDateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
val displayDateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")

/**
 * Convert an OffsetDateTime to an ISO-8601 formatted string
 * Used for API communication and passing data between screens
 */
fun OffsetDateTime.toIsoString(): String {
    return try {
        this.format(isoOffsetDateTimeFormatter)
    } catch (e: Exception) {
        // Return empty string in case of exception
        ""
    }
}

/**
 * Parse an ISO-8601 formatted string to an OffsetDateTime
 * Used for API communication and passing data between screens
 */
fun String.toOffsetDateTime(): OffsetDateTime {
    return try {
        OffsetDateTime.parse(this, isoOffsetDateTimeFormatter)
    } catch (e: Exception) {
        e.printStackTrace()
        OffsetDateTime.now(ZoneOffset.UTC)
    }
}


fun formatWrittenAt(createdAt: OffsetDateTime?): String {
    if (createdAt == null) return ""
    val localDateTime = createdAt.atZoneSameInstant(ZoneId.systemDefault())
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val dateStr = localDateTime.toLocalDate().format(displayDateFormatter)
    val timeStr = localDateTime.format(timeFormatter)
    return "Written on $dateStr at $timeStr"
}


/**
 * Format a LocalDate to ISO date format (yyyy-MM-dd)
 * Used for API communication and internal storage
 */
fun LocalDate.toIsoDateString(): String {
    return this.format(isoDateFormatter)
}

/**
 * Format a LocalDate to display format (MMMM dd, yyyy)
 * Used for displaying dates to users
 */
fun LocalDate.toDisplayDateString(): String {
    return this.format(displayDateFormatter)
}

/**
 * Parse an ISO date string (yyyy-MM-dd) to LocalDate
 */
fun String.toLocalDateFromIso(): LocalDate? {
    return try {
        LocalDate.parse(this, isoDateFormatter)
    } catch (e: Exception) {
        null
    }
}


@SuppressLint("NewApi")
fun getCurrentDateFormatted(): String {
    return LocalDate.now().format(displayDateFormatter)
}

/**
 * Convert a LocalDate to OffsetDateTime at the start of the day (00:00:00) in UTC
 * This preserves the original timezone and should be used for API communication and internal storage
 */
fun LocalDate.toOffsetDateTime(): OffsetDateTime {
    return OffsetDateTime.of(this, LocalTime.MIN, ZoneOffset.UTC)
}
