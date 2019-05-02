package com.akay.fitnass.data.storage.converter;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.akay.fitnass.data.storage.model.Lap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ListConverter {
    private static final Gson gson;

    static {
        gson = new Gson();
    }

    ListConverter() {
        Log.d("ololo", "ListConverter constructor");
    }

    @TypeConverter
    public static List<Lap> stringToList(String json) {
        return fromJson(json);
    }

    @TypeConverter
    public static String listToString(List<Lap> laps) {
        return toJson(laps);
    }

    private static <T> List<T> fromJson(final String json) {
        if (json == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<T>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    private static  <T> String toJson(final List<T> list) {
        if (list == null) {
            return gson.toJson(Collections.<T>emptyList());
        }
        return gson.toJson(list);
    }
}
