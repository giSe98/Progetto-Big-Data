package com.example.controllers;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

@Controller("/interesse")
public class InteresseController {
    private HashMap<String, Integer> device = new HashMap<>();
    private HashMap<String, Integer> lang = new HashMap<>();

    private JSONObject jsonObject = new JSONObject();

    @Get(value="/device",produces = MediaType.TEXT_PLAIN)
    public String device() { return new JSONObject(this.device.toString().replace("=", ":")).toString(); }

    @Post(value="/device",produces = MediaType.TEXT_PLAIN)
    public void device(HashMap<String, Integer> device) {
        jsonObject = new JSONObject(device);
        //System.out.println(jsonObject);
        this.device = update(jsonObject, this.device);

        //System.out.println("ASD " + this.device);
    }

    @Get(value="/lang",produces = MediaType.TEXT_PLAIN)
    public String lang() { return new JSONObject(this.lang.toString().replace("=", ":")).toString(); }

    @Post(value="/lang",produces = MediaType.TEXT_PLAIN)
    public void lang(HashMap<String, Integer> lang) {
        jsonObject = new JSONObject(lang);
        //System.out.println(jsonObject);
        this.lang = update(jsonObject, this.lang);
    }

    private HashMap<String, Integer> update(JSONObject jsonObject, HashMap<String, Integer> hashMap) {
        Iterator<String> keys = jsonObject.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            hashMap.computeIfPresent(key, (k, v) -> v + jsonObject.getInt(key));
            hashMap.computeIfAbsent(key, v -> jsonObject.getInt(key));
        }

        System.out.println(hashMap);
        return hashMap;
    }
}
