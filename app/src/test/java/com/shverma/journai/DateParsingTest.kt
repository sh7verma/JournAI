package com.shverma.journai

import org.junit.Test
import org.junit.Assert.*
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

class DateParsingTest {

    @Test
    fun testParseIsoDateTimeString() {
        // The problematic date string from the error message
        val dateTimeString = "2025-07-29T08:25:39.261927Z"
        
        // Parse using our new approach
        val offsetDateTime = OffsetDateTime.parse(dateTimeString)
        val localDate = offsetDateTime.toLocalDate()
        
        // Verify the extracted date is correct
        assertEquals(2025, localDate.year)
        assertEquals(7, localDate.monthValue)
        assertEquals(29, localDate.dayOfMonth)
        
        // Print for debugging
        println("[DEBUG_LOG] Successfully parsed date: $localDate from $dateTimeString")
    }
    
    @Test
    fun testFallbackToDirectParsing() {
        // A simple date string without time
        val dateString = "2025-07-29"
        
        // Parse using direct LocalDate parsing
        val localDate = LocalDate.parse(dateString)
        
        // Verify the parsed date is correct
        assertEquals(2025, localDate.year)
        assertEquals(7, localDate.monthValue)
        assertEquals(29, localDate.dayOfMonth)
        
        println("[DEBUG_LOG] Successfully parsed simple date: $localDate from $dateString")
    }
    
    @Test
    fun testRobustDateParsing() {
        // Test our robust parsing approach with both formats
        val dateTimeString = "2025-07-29T08:25:39.261927Z"
        val simpleDateString = "2025-07-29"
        
        // Function that mimics our implementation in DetailsScreen.kt
        fun parseDate(dateStr: String): LocalDate {
            return try {
                // Try parsing as OffsetDateTime first
                val offsetDateTime = OffsetDateTime.parse(dateStr)
                offsetDateTime.toLocalDate()
            } catch (e: Exception) {
                try {
                    // Fall back to direct LocalDate parsing
                    LocalDate.parse(dateStr)
                } catch (e2: Exception) {
                    // Last resort: use current date
                    LocalDate.now()
                }
            }
        }
        
        // Test with date-time string
        val parsedDateTime = parseDate(dateTimeString)
        assertEquals(2025, parsedDateTime.year)
        assertEquals(7, parsedDateTime.monthValue)
        assertEquals(29, parsedDateTime.dayOfMonth)
        
        // Test with simple date string
        val parsedDate = parseDate(simpleDateString)
        assertEquals(2025, parsedDate.year)
        assertEquals(7, parsedDate.monthValue)
        assertEquals(29, parsedDate.dayOfMonth)
        
        println("[DEBUG_LOG] Robust parsing works for both formats")
    }
}