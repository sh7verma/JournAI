package com.shverma.journai.data.local.converter

import androidx.room.TypeConverter
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Room type converter for OffsetDateTime
 */
class DateTimeConverter {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun fromString(value: String?): OffsetDateTime? {
        return value?.let { OffsetDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun toString(date: OffsetDateTime?): String? {
        return date?.format(formatter)
    }
}