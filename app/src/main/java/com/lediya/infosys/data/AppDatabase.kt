package com.lediya.infosys.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lediya.infosys.R
import com.lediya.infosys.data.dao.CountryDao
import com.lediya.infosys.model.CountryListResponse

@Database(entities = [CountryListResponse::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val countryDao: CountryDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        /**
         * create instance to get the database*/
        fun getDatabase(context: Context): AppDatabase? {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(context)
                }
                return INSTANCE
            }
        }
        /**
         * create builder using room database */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                context.getString(R.string.database)
            ).build()
        }
    }

}