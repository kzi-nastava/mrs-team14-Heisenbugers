package com.example.gotaximobile.network;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PartUtil {
    public static RequestBody text(String value){
        return RequestBody.create(value == null ? "" : value,
                MediaType.parse("text/plain"));
    }
}
