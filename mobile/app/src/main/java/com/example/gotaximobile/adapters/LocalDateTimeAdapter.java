package com.example.gotaximobile.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    //private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter LOCAL_FMT = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .optionalEnd()
            .toFormatter();


    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String dateStr = in.nextString();

        if (dateStr == null || dateStr.isBlank()) return null;

        try {
            return LocalDateTime.parse(dateStr, LOCAL_FMT);
        } catch (Exception e) {
            try {
                return OffsetDateTime.parse(dateStr).toLocalDateTime();
            } catch (Exception ex) {
                throw new IOException("Cannot parse LocalDateTime: " + dateStr, ex);
            }
        }
        //return LocalDateTime.parse(dateStr, formatter);
    }
}