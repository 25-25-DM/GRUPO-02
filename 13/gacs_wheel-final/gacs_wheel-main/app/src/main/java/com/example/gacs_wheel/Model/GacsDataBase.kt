package com.example.gacs_wheel.Model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Usuario::class, Vehiculo::class],
    version = 2, // Incrementado de 1 a 2
    exportSchema = false
)
abstract class GascDataBase : RoomDatabase() {

    abstract fun vehiculoDao(): VehiculoDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: GascDataBase? = null

        // Migración para agregar el campo dynamoId
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Agregar la nueva columna dynamoId (puede ser NULL inicialmente)
                database.execSQL("ALTER TABLE usuario ADD COLUMN dynamoId INTEGER")
            }
        }

        fun getDatabase(context: Context): GascDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GascDataBase::class.java,
                    "gacs_wheel_database"
                )
                    .addMigrations(MIGRATION_1_2) // Agregar la migración
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}