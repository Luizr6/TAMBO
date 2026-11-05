package com.tecsup.tambo.data

import androidx.lifecycle.LiveData

class SedeRepository(private val sedeDao: SedeDao) {

    val sedes: LiveData<List<SedeEntity>> = sedeDao.getAll()

    suspend fun insert(sede: SedeEntity): Long = sedeDao.insert(sede)

    suspend fun update(sede: SedeEntity) = sedeDao.update(sede)

    suspend fun delete(sede: SedeEntity) = sedeDao.delete(sede)

    suspend fun getById(id: Long): SedeEntity? = sedeDao.getById(id)
}
