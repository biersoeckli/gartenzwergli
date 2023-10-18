package ch.ost.gartenzwergli.services

import android.content.Context
import androidx.room.Room.databaseBuilder
import ch.ost.gartenzwergli.services.interfaces.AppDatabase

class DatabaseService {

    companion object {
        private var db: AppDatabase? = null

        fun getDb(): AppDatabase {
            if (db == null) {
                throw Exception("Database not set")
            }
            return db!!
        }

        fun setupDbWithContext(context: Context) {
            db = databaseBuilder(
                context,
                AppDatabase::class.java,
                "gartenzwergli"
            ).allowMainThreadQueries() // not recommended for production
                .build()
        }
    }
}