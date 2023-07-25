package com.tung.coffeeorder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@TypeConverters(Converters::class)
@Database(entities = [Order::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun orderDao(): OrderDao

    companion object{
        private var singleton: AppDatabase? = null

        fun getSingleton(context: Context): AppDatabase{
            if (singleton==null){
                singleton=Room.databaseBuilder(context, AppDatabase::class.java, "appdatabase")
                    .allowMainThreadQueries()
                    .build()
            }
            return singleton!!
        }
    }
}