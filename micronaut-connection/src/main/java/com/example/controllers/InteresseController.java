package com.example.controllers;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller("/interesse")
public class InteresseController {
    private HashMap<String, Integer> device = new HashMap<>();
    private HashMap<String, Integer> lang = new HashMap<>();
    private JSONObject bestRetweet = new JSONObject();
    private JSONObject bestLike = new JSONObject();

    private JSONObject jsonObject = new JSONObject();

    @Get(value="/device",produces = MediaType.TEXT_PLAIN)
    public String device() { return new JSONObject(this.device.toString().replace("=", ":")).toString(); }

    @Post(value="/device",produces = MediaType.TEXT_PLAIN)
    public void device(HashMap<String, Integer> device) {
        jsonObject = new JSONObject(device);
        this.device = formatting(jsonObject);
    }

    @Get(value="/lang",produces = MediaType.TEXT_PLAIN)
    public String lang() { return new JSONObject(this.lang.toString().replace("=", ":")).toString(); }

    @Post(value="/lang",produces = MediaType.TEXT_PLAIN)
    public void lang(HashMap<String, Integer> lang) {
        jsonObject = new JSONObject(lang);
        this.lang = formatting(jsonObject);
    }

    @Get(value="/bestRetweet",produces = MediaType.TEXT_PLAIN)
    public String bestRetweet() { return this.bestRetweet.toString(); }

    @Post(value="/bestRetweet",produces = MediaType.TEXT_PLAIN)
    public void bestRetweet(String tweet) {
        jsonObject = new JSONObject(new String(Base64.getDecoder().decode(tweet)));
        this.bestRetweet = jsonObject;
    }

    @Get(value="/bestLike",produces = MediaType.TEXT_PLAIN)
    public String bestLike() { return this.bestLike.toString(); }

    @Post(value="/bestLike",produces = MediaType.TEXT_PLAIN)
    public void bestLike(String tweet) {
        jsonObject = new JSONObject(new String(Base64.getDecoder().decode(tweet)));
        this.bestLike = jsonObject;
    }

    private HashMap<String, Integer> formatting(JSONObject jsonObject) {
        HashMap<String, Integer> hashMap = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            hashMap.put(key, jsonObject.getInt(key));
        }

        return hashMap;
    }
}
