package com.tecsup.tambo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SedeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sedeDao(): SedeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Datos de ejemplo que se insertan solo la primera vez que se crea la base de datos.
        private val seedCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val cols = "(nombreLocal, gerente, direccion, horarioAtencion, latitud, longitud)"
                db.execSQL(
                    "INSERT INTO sedes $cols VALUES " +
                        "('TAMBO YUPANQUI - JESÚS MARÍA', 'José Lopez García', " +
                        "'Jirón Lloque Yupanqui 1912, Jesús María, Perú', " +
                        "'Lunes a domingo 09:00 - 22:00', -12.0720, -77.0500)"
                )
                db.execSQL(
                    "INSERT INTO sedes $cols VALUES " +
                        "('TAMBO ARENALES - LINCE', 'María Torres Ruiz', " +
                        "'Av. Arenales 1620, Lince, Perú', " +
                        "'Lunes a domingo 07:00 - 23:00', -12.0830, -77.0350)"
                )
                db.execSQL(
                    "INSERT INTO sedes $cols VALUES " +
                        "('TAMBO LARCOMAR - MIRAFLORES', 'Carlos Ramos Díaz', " +
                        "'Malecón de la Reserva 610, Miraflores, Perú', " +
                        "'Lunes a domingo 08:00 - 22:00', -12.1318, -77.0300)"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tambo.db"
                )
                    .addCallback(seedCallback)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
