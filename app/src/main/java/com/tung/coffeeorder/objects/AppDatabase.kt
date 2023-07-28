package com.tung.coffeeorder

import android.content.Context
import androidx.room.*
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.time.LocalDateTime
import java.util.*

class Converters {
    @TypeConverter
    fun fromListOrder(value: List<Order>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toListOrder(value: String): List<Order> {
        val listType = object : TypeToken<List<Order>>() {}.type
        return Gson().fromJson(value, listType)
    }



    @TypeConverter
    fun fromLinkedListCoffee(value: LinkedList<CoffeeInCart>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toLinkedListCoffee(value: String): LinkedList<CoffeeInCart> {
        val listType = object : TypeToken<LinkedList<CoffeeInCart>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime {
        val listType = object : TypeToken<LocalDateTime>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromOrder(value: Order): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toOrder(value: String): Order {
        val listType = object : TypeToken<Order>() {}.type
        return Gson().fromJson(value, listType)
    }
}

@Database(entities = [Order::class, Cart::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun cartDao(): CartDao

    companion object{
        private var singleton: AppDatabase? = null

        fun getSingleton(context: Context): AppDatabase{
            if (singleton==null){
                singleton=Room.databaseBuilder(context, AppDatabase::class.java, "coffeeOrder-db")
                    .allowMainThreadQueries()
                    .build()
            }
            return singleton!!
        }
    }
}