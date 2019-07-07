package com.akay.fitnass.data.db.converter

import androidx.room.TypeConverter
import android.text.TextUtils
import com.akay.fitnass.data.model.Lap
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

class ListConverter {
    private val gson: Gson

    init {
        val zdtSerializer = JsonSerializer<ZonedDateTime> { src, _, _ ->
            val fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC)
            JsonPrimitive(fmt.format(src.truncatedTo(ChronoUnit.MILLIS)))
        }
        val zdtDeserializer = JsonDeserializer { json, _, _ ->
            val fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC)
            ZonedDateTime.from(fmt.parse(json.asString))
        }

        gson = GsonBuilder()
                .registerTypeAdapter(ZonedDateTime::class.java, zdtSerializer)
                .registerTypeAdapter(ZonedDateTime::class.java, zdtDeserializer)
                .create()
    }

    @TypeConverter
    fun fromJson(json: String): List<Lap> {
        if (TextUtils.isEmpty(json)) {
            return ArrayList()
        }
        val listType = object : TypeToken<List<Lap>>() {}.type
        return gson.fromJson(json, listType)
    }

    @TypeConverter
    fun toJson(list: List<Lap>?): String {
        return if (list == null) {
            gson.toJson(ArrayList<Any>())
        } else gson.toJson(list)
    }
}