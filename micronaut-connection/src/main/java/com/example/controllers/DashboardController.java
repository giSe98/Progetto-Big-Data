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
        System.out.println(tweets);
        System.out.println(jsonObject);

        this.tweets = update(jsonObject, this.tweets);
    }

    private HashMap<String, JSONObject> update(JSONObject jsonObject, HashMap<String, JSONObject> hashMap) {
        Iterator<String> keys = jsonObject.keys();

        while(keys.hasNext()) {
            String key = keys.next();

            if (!tweets.containsKey(key)) {
                JSONObject value = new JSONObject(new String(Base64.getDecoder().decode((String) jsonObject.get(key))));
//                System.out.println(value);
                tweets.put(key, value);
            }
        }
//
        System.out.println(hashMap);
        return hashMap;
    }
}
