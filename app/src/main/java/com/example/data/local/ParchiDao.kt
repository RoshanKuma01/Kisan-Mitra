package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ParchiDao {
  @Query("SELECT * FROM parchi_records ORDER BY timestamp DESC")
  fun getAllParchis(): Flow<List<ParchiEntity>>

  @Query("SELECT * FROM parchi_records WHERE id = :id LIMIT 1")
  suspend fun getParchiById(id: Long): ParchiEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertParchi(parchi: ParchiEntity): Long

  @Update
  suspend fun updateParchi(parchi: ParchiEntity)

  @Delete
  suspend fun deleteParchi(parchi: ParchiEntity)

  @Query("DELETE FROM parchi_records WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("SELECT COUNT(*) FROM parchi_records")
  fun getCount(): Flow<Int>
}
