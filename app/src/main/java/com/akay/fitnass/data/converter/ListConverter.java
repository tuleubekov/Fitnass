package com.akay.fitnass.data.converter;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.akay.fitnass.data.model.Lap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ListConverter {

    ListConverter() {
        Log.d("ololo", "ListConverter constructor");
    }

    @TypeConverter
    public static List<Lap> fromString(String json) {
        Log.d("ololo", "ListConverter fromJson json: " + json);
        Type listType = new TypeToken<List<Lap>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }

    @TypeConverter
    public static String fromList(List<Lap> list) {
        return new Gson().toJson(list);
    }
}
