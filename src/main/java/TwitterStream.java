import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.storm.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class TwitterStream {
    private static final String bearer = System.getenv("BEARER_TOKEN");

    private static void connectStream() {
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
        try {
            URIBuilder uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/search/stream");
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Authorization", String.format("Bearer %s", bearer));

            CloseableHttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if(entity != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader((entity.getContent())));
                String line = reader.readLine();
                System.out.println(line);
            }
            response.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addRules(Map<String, String> rules) {
        HttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
        try {
            URIBuilder uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/search/stream/rules");

            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setHeader("Authorization", String.format("Bearer %s", bearer));
            httpPost.setHeader("content-type", "application/json");

            //System.out.println(getAddString(rules));
            StringEntity body = new StringEntity(getAddString(rules));
            httpPost.setEntity(body);

            HttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if(entity != null)
                System.out.println(EntityUtils.toString(entity, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getAddString(Map<String, String> rules) {
        StringBuilder sb = new StringBuilder();
        String str = "{ \"add\" : [%s]}";

        if(rules.size() == 1) {
            String key = rules.keySet().iterator().next();
            return String.format(str, "{ \"value\" : \"" + key +"\", \"tag\" : \"" + rules.get(key) + "\"}");
        }
        else {
            int i = 0;
            for(Map.Entry<String, String> entry : rules.entrySet()) {
                sb.append("{ \"value\" : \"" + entry.getKey() + "\", \"tag\" : \"" + entry.getValue() + "\"}");
                if(i < rules.size() - 1)
                    sb.append(", ");
                i++;
            }
        }
        return String.format(str, sb);
    }

    private static List<String> getRules() {
        List<String> rules = new ArrayList<>();

        HttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
        try {
            URIBuilder uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/search/stream/rules");
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Authorization", String.format("Bearer %s", bearer));
            httpGet.setHeader("content-type", "application/json");
            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if(entity != null) {
                JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity, "UTF-8"));
                if(jsonObject.length() > 1) {
                    JSONArray array = (JSONArray) jsonObject.get("data");
                    for(int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        rules.add(object.getString("id"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rules;
    }

    private static void deleteRules(List<String> rules) {
        HttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
        try {
            URIBuilder uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/search/stream/rules");
            HttpPost httpPost = new HttpPost(uriBuilder.build());
            httpPost.setHeader("Authorization", String.format("Bearer %s", bearer));
            httpPost.setHeader("content-type", "application/json");

            StringEntity body = new StringEntity(getDeleteString(rules));
            httpPost.setEntity(body);

            HttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if(entity != null)
                System.out.println(EntityUtils.toString(entity, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getDeleteString(List<String> rules) {
        StringBuilder sb = new StringBuilder();
        String str = "{ \"delete\" : { \"ids\" : [%s]}}";

        if(rules.size() == 1)
            return String.format(str, "\"" + rules.get(0) + "\"");
        else {
            for(int i = 0; i < rules.size(); i++) {
                sb.append("\"" + rules.get(i) + "\"");
                if(i < rules.size() - 1)
                    sb.append(", ");
            }
        }
        return String.format(str, sb);
    }

    public static void setupRules(Map<String, String> rules) {
        List<String> existingRules = getRules();
        if (existingRules.size() > 0) {
            deleteRules(existingRules);
        }
        addRules(rules);
    }


    public static void main(String[] args) {
//        Map<String, String> rules1 = new HashMap<>();
//        rules1.put("context:123.1220701888179359745", "covid");
//        setupRules(rules1);
//        while (true) {
//            connectStream();
//            Utils.sleep(10);
//        }
        //  value    id
//        Map<String, String> r = new HashMap<>();
//        r.put("context:123.1220701888179359745 lang:en -is:retweet", "covid");


        HttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
        try {
            URIBuilder uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/1479880081787006981");
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Authorization", String.format("Bearer %s", bearer));
            httpGet.setHeader("content-type", "application/json");
            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
//            System.out.println(reader.readLine());
            if(entity != null) {
                JSONObject jsonObject = new JSONObject(EntityUtils.toString(entity, "UTF-8"));
                System.out.println(jsonObject);

                String object = jsonObject.getJSONObject("data").getString("text");
                System.out.println(object.contains("novax"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
