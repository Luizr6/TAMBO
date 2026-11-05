package com.tecsup.tambo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sedes")
data class SedeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreLocal: String,
    val gerente: String,
    val direccion: String,
    val horarioAtencion: String,
    val latitud: Double,
    val longitud: Double
)
