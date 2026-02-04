package com.shverma.journai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.shverma.journai.data.local.entity.JournalEntity
import org.threeten.bp.OffsetDateTime

@Dao
interface JournalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(journalEntity: JournalEntity): Long

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getJournalEntryById(id: String): JournalEntity?

    @Query("SELECT * FROM journal_entries ORDER BY created_at DESC")
    suspend fun getAllJournalEntries(): List<JournalEntity>

    @Update
    suspend fun updateJournalEntry(journalEntity: JournalEntity)

    @Delete
    suspend fun deleteJournalEntry(journalEntity: JournalEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournalEntryById(id: String)

    @Query("SELECT * FROM journal_entries WHERE date(created_at) = date(:date) ORDER BY created_at DESC")
    suspend fun getJournalEntriesByDate(date: OffsetDateTime): List<JournalEntity>

    @Query("SELECT * FROM journal_entries ORDER BY created_at DESC LIMIT 5")
    suspend fun getRecentJournalEntries(): List<JournalEntity>

    @Query("SELECT mood FROM journal_entries ORDER BY created_at DESC LIMIT 7")
    suspend fun getRecentMoods(): List<String>

    @Query("SELECT COUNT(*) FROM journal_entries WHERE date(created_at) >= date('now', '-7 days')")
    suspend fun getJournalEntriesCountLastWeek(): Int
}