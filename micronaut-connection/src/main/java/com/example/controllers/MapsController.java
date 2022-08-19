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
//        System.out.println(geo);
//        System.out.println(jsonObject);
        this.geo = update(jsonObject, this.geo);
    }

    @Get(value="/realPositions",produces = MediaType.TEXT_PLAIN)
    public String realPosition() {
        System.out.println(this.real);
        return this.real.values().toString();
    }

    @Post(value="/realPositions",produces = MediaType.TEXT_PLAIN)
    public void realPosition(HashMap<String, String> real) {
        jsonObject = new JSONObject(real);
        System.out.println(real);
        System.out.println(jsonObject);
        this.real = update(jsonObject, this.real);
    }

    private HashMap<String, JSONObject> update(JSONObject jsonObject, HashMap<String, JSONObject> hashMap) {
        Iterator<String> keys = jsonObject.keys();

        while(keys.hasNext()) {
            String key = keys.next();

            if (hashMap.containsKey(key)) {
                JSONObject object = hashMap.get(key);
//                System.out.println(object);
                hashMap.put(key, object.put("pop", object.getDouble("pop") + 0.01));
            }
            else {
                String encode = jsonObject.getString(key);
                JSONObject value = new JSONObject(new String(Base64.getDecoder().decode(encode)));
                System.out.println(value);
                hashMap.put(key, value);
            }
        }
//
        System.out.println(hashMap);
        return hashMap;
    }
}
