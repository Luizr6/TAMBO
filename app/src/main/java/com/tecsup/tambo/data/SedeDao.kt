package com.tecsup.tambo.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SedeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sede: SedeEntity): Long

    @Update
    suspend fun update(sede: SedeEntity)

    @Delete
    suspend fun delete(sede: SedeEntity)

    @Query("SELECT * FROM sedes ORDER BY id DESC")
    fun getAll(): LiveData<List<SedeEntity>>

    @Query("SELECT * FROM sedes WHERE id = :id")
    suspend fun getById(id: Long): SedeEntity?
}
