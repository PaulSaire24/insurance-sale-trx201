package com.bbva.rbvd.lib.r211.impl.util;

import com.google.gson.*;
import org.joda.time.LocalDate;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JsonHelper {

    private static final String DATE = "yyyy-MM-dd";
    private static final JsonHelper INSTANCE = new JsonHelper();


    private final Gson gson;

    private JsonHelper() {
        gson = new GsonBuilder()
                .setDateFormat(DATE)
                .create();
    }

    public static JsonHelper getInstance() { return INSTANCE; }

    public String toJsonString(Object o) { return this.gson.toJson(o); }

}