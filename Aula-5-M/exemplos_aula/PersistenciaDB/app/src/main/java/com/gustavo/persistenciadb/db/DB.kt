package com.gustavo.persistenciadb.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gustavo.persistenciadb.dao.PessoaDao
import com.gustavo.persistenciadb.domain.Pessoa

@Database(
    entities = [Pessoa::class],
    version = 1
)
abstract class DB : RoomDatabase() {

    abstract fun pessoaDao(): PessoaDao

    object DatabaseProvider {

        @Volatile
        private var INSTANCE: DB? = null

        fun getDatabase(context: Context): DB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DB::class.java,
                    "app_db"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}

