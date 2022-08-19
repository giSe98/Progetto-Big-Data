package com.example.controllers;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.json.JSONObject;

import java.util.*;

@Controller("/dashboard")
public class DashboardController {
    private HashMap<String, JSONObject> tweets = new HashMap<>();
    private JSONObject jsonObject = new JSONObject();

    public DashboardController() {
    }

    @Get(value="/tweets",produces = MediaType.TEXT_PLAIN)
    public String getTweets() {
        return this.tweets.values().toString();
    }

    @Post(value="/tweets",produces = MediaType.TEXT_PLAIN)
    public void getTweets(HashMap<String, String> tweets) {
        jsonObject = new JSONObject(tweets);
        this.tweets = formatting(jsonObject);
    }

    private HashMap<String, JSONObject> formatting(JSONObject jsonObject) {
        HashMap<String, JSONObject> hashMap = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            JSONObject value = new JSONObject(new String(Base64.getDecoder().decode(jsonObject.getString(key))));
            hashMap.put(key, value);
        }

        return hashMap;
    }
}
