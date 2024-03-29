package com.example.controllers;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.json.JSONObject;

import java.util.*;

@Controller("/maps")
public class MapsController {
    private HashMap<String, JSONObject> geo = new HashMap<>();
    private HashMap<String, JSONObject> real = new HashMap<>();
    private JSONObject jsonObject = new JSONObject();
    @Get(value="/relativePositions",produces = MediaType.TEXT_PLAIN)
    public String relativePosition() { return this.geo.values().toString(); }

    @Post(value="/relativePositions",produces = MediaType.TEXT_PLAIN)
    public void relativePosition(HashMap<String, String> geo) {
        jsonObject = new JSONObject(geo);
        this.geo = formatting(jsonObject);
    }

    @Get(value="/realPositions",produces = MediaType.TEXT_PLAIN)
    public String realPosition() {
        return this.real.values().toString();
    }

    @Post(value="/realPositions",produces = MediaType.TEXT_PLAIN)
    public void realPosition(HashMap<String, String> real) {
        jsonObject = new JSONObject(real);
        this.real = formatting(jsonObject);
    }

    private HashMap<String, JSONObject> formatting(JSONObject jsonObject) {
        HashMap<String, JSONObject> hashMap = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();

        while(keys.hasNext()) {
            String key = keys.next();

            String encode = jsonObject.getString(key);
            JSONObject value = new JSONObject(new String(Base64.getDecoder().decode(encode)));
            hashMap.put(key, value);
        }

        return hashMap;
    }
}
