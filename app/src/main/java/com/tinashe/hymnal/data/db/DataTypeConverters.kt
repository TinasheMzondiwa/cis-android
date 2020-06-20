package com.tinashe.hymnal.data.db

import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class DataTypeConverters {

    private val moshi by lazy { Moshi.Builder().build() }

    /**
     * Convert a List of [Int] to json
     */
    @TypeConverter
    fun integersToJson(ints: List<Int>?): String {
        val adapter: JsonAdapter<List<Int>> = moshi.adapter<List<Int>>(
            Types.newParameterizedType(List::class.java, Integer::class.java)
        ).nonNull()
        return adapter.toJson(ints)
    }

    /**
     * Convert json string to a List of [Int]
     */
    @TypeConverter
    fun jsonToIntegers(json: String?): List<Int> {
        return json?.let {
            val adapter: JsonAdapter<List<Int>> = moshi.adapter<List<Int>>(
                Types.newParameterizedType(List::class.java, Integer::class.java)
            ).nonNull()
            adapter.fromJson(json)
        } ?: emptyList()
    }
}
