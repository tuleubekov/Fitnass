package com.akay.fitnass.data.db.converter;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import com.akay.fitnass.data.model.Lap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListConverter {
    private static final Gson gson;

    static {
        gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, DateTimeSerializer.ZDT_SERIALIZER)
                .registerTypeAdapter(ZonedDateTime.class, DateTimeSerializer.ZDT_DESERIALIZER)
                .create();
    }

    @TypeConverter
    public static List<Lap> fromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<Lap>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    @TypeConverter
    public static String toJson(List<Lap> list) {
        if (list == null) {
            return gson.toJson(new ArrayList<>());
        }
        return gson.toJson(list);
    }

    private static class DateTimeSerializer {
        static final JsonSerializer<ZonedDateTime> ZDT_SERIALIZER = (zdt, typeOfSrc, context) -> {
            DateTimeFormatter fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);
            return new JsonPrimitive(fmt.format(zdt.truncatedTo(ChronoUnit.MILLIS)));
        };

        static final JsonDeserializer<ZonedDateTime> ZDT_DESERIALIZER = (json, typeOfT, context) -> {
            DateTimeFormatter fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);
            return ZonedDateTime.from(fmt.parse(json.getAsString()));
        };
    }


}
