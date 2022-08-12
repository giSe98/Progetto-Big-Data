package com.example.controllers;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Controller("/dashboard")
public class DashboardController {
    private HashMap<String, HashMap<String, Integer>> numTweetMesi = new HashMap<>();

    public DashboardController() {
    }

    @Get(value="/numTweetMesePro",produces = MediaType.TEXT_PLAIN)
    public List<Integer> numTweetMesePro() {
        int mese = Calendar.getInstance().get(Calendar.MONTH);

        List<Integer> num = new ArrayList<>();
        for(int i = 0; i < mese; i++)
            num.add(0);

        numTweetMesi.get("pro").forEach((key, value) -> num.add(value));

        return num;
    }

    @Post(value="/numTweetMesePro",produces = MediaType.TEXT_PLAIN)
    public void numTweetMesePro(HashMap<String, Integer> tweet) {
        numTweetMesi.put("pro", tweet);
    }

    @Get(value="/numTweetMeseContro",produces = MediaType.TEXT_PLAIN)
    public List<Integer> numTweetMeseContro() {
        int mese = Calendar.getInstance().get(Calendar.MONTH);

        List<Integer> num = new ArrayList<>();
        for(int i = 0; i < mese; i++)
            num.add(0);

        numTweetMesi.get("contro").forEach((key, value) -> num.add(value));

        return num;
    }

    @Post(value="/numTweetMeseContro",produces = MediaType.TEXT_PLAIN)
    public void numTweetMeseContro(HashMap<String, Integer> tweet) {
        numTweetMesi.put("contro", tweet);
    }

}
