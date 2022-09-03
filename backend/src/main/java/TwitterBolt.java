import Test.Pipeline;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class TwitterBolt extends BaseRichBolt {
    private OutputCollector collector;
    private StanfordCoreNLP stanfordCoreNLP = null;
    private Map<String,Integer> valoriAnalisi= new HashMap<>();
    private Map<String, Integer> device = new HashMap<>();
    private Map<String, Integer> countries = new HashMap<>();
    private Map<String, Integer> lang = new HashMap<>();
    private Map<String, JSONObject> geo = new HashMap<>();
    private Map<String, JSONObject> realPositions = new HashMap<>();
    private Map<String, JSONObject> tweets = new HashMap<>();

    private int bestR = Integer.MIN_VALUE; // indice retweet
    private JSONObject retweetObj; // object
    private int best_like = 0; //dovrebbe contare il numero di like di ogni tweet e restituire quello migliore ma da null pointer exception
    private JSONObject likeObj; // object


    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        valoriAnalisi.put("Negative",0);
        valoriAnalisi.put("Positive",0);
        valoriAnalisi.put("Neutral",0);
        stanfordCoreNLP = Pipeline.getPipeline();
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        if(input.getString(0).equals("tweet finiti")){
            System.out.println("Sending");
            String json = "";

            json = createStringTweets(tweets);
            sendData(json, "/dashboard/tweets");

            //send best retweet
            json = createStringBestTweet(retweetObj, "retweet");
            sendData(json, "/interesse/bestRetweet");

            //send tweet with most like
            json = createStringBestTweet(likeObj, "like");
            sendData(json, "/interesse/bestLike");

            //send device
            json = createString(device);
            sendData(json, "/interesse/device");

            //send positions deduced
            json = createStringGeo(geo);
            sendData(json, "/maps/relativePositions");

            //send real positions
            if (!realPositions.isEmpty()) {
                json = createStringGeo(realPositions);
                sendData(json, "/maps/realPositions");
            }

            //send lang
            json = createString(lang);
            sendData(json, "/interesse/lang");
            System.out.println("Sending complete");
        }

        try {
            String line = input.getString(0);
            JSONObject o = new JSONObject(line);

            JSONObject tweet = extractTweetJSON(o);

            String language = o.getJSONObject("data").getString("lang");
            update(lang, language);

            String dato = o.getJSONObject("data").getString("text");
            dato = clear(dato);
            String sentiment = analysis(dato);
            valoriAnalisi.put(sentiment, valoriAnalisi.get(sentiment) + 1);
            tweet.put("sentiment", sentiment);
            tweets.put(tweet.getString("conversation_id"), tweet);

            bestRetweet(o);

            bestLike(o);

            String source = o.getJSONObject("data").getString("source");
            update(device, source);

            getCountry(o);
            countries.remove("null");
            countries.remove("None");
        } catch (Exception e) {}

        collector.emit("stream", new Values(input.getString(0)));
    }


    private JSONObject extractTweetJSON(JSONObject o) {
        JSONObject ret = new JSONObject();

        ret.put("author_id", o.getJSONObject("data").getString("author_id"));
        ret.put("conversation_id", o.getJSONObject("data").getString("conversation_id"));
        ret.put("created_at", o.getJSONObject("data").getString("created_at"));
        ret.put("description", o.getJSONObject("data").getString("text"));

        return ret;
    }

    private void sendData(String json, String endpoint) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://localhost:8080" + endpoint);
        StringEntity entity = null;
        try {
            entity = new StringEntity(json);
            httppost.setEntity(entity);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        httppost.setHeader("Content-type", "application/json");

        //Execute and get the response.
        CloseableHttpResponse body = null;
        try {
            body = httpclient.execute(httppost);
            body.close();
            System.out.println("BODY: " + body);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HttpEntity entity1 = body.getEntity();

        if (entity1 != null) {
            try (InputStream instream = entity.getContent()) {
                // do something useful
            } catch (UnsupportedOperationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private String createStringBestTweet(JSONObject o, String what) {
        JSONObject ret = new JSONObject();

        JSONObject jsonObject = o.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0);
        ret.put("author_id", jsonObject.getString("author_id"));
        ret.put("conversation_id", jsonObject.getString("conversation_id"));
        ret.put("created_at", jsonObject.getString("created_at"));
        ret.put("description", jsonObject.getString("text"));
        ret.put(what + "_count", jsonObject.getJSONObject("public_metrics").getInt(what + "_count"));
        ret.put("sentiment", analysis(clear(jsonObject.getString("text"))));

        return "{\"tweet\": \"" + Base64.getEncoder().encodeToString(ret.toString().getBytes()) + "\"}";
    }

    private String createStringTweets(Map<String, JSONObject> tweets) {
        String ret = "{";
        int count = 0;
        int size = tweets.size();

        for (Map.Entry<String, JSONObject> entry : tweets.entrySet()) {
            ret += "\"" + entry.getKey() + "\": \"" + Base64.getEncoder().encodeToString(entry.getValue().toString().getBytes()) + "\"";
            if (count < size - 1) ret += ", ";
            count++;
        }
        ret += "}";

        return ret;
    }

    private String createStringGeo(Map<String, JSONObject> geo) {
        String ret = "{";
        int count = 0;
        int size = geo.size();

        for (Map.Entry<String, JSONObject> entry : geo.entrySet()) {
            ret += "\"" + entry.getKey() + "\": \"" + Base64.getEncoder().encodeToString(entry.getValue().toString().getBytes()) + "\"";
            if (count < size - 1) ret += ", ";
            count++;
        }
        ret += "}";

        return ret;
    }

    private String createString(Map<String, Integer> countries) {
        String ret = "{";
        int count = 0;
        int size = countries.size();

        for (Map.Entry<String, Integer> entry : countries.entrySet()) {
            ret += "\"" + entry.getKey() + "\": " + entry.getValue();
            if (count < size - 1) ret += ", ";
            count++;
        }
        ret += "}";

        return ret;
    }

    private void bestRetweet(JSONObject o) {
        if (o.getJSONObject("data").has("referenced_tweets")) {
            int c = Integer.parseInt(o.getJSONObject("data").getJSONObject("public_metrics").getString("retweet_count"));
            if (c > bestR) {
                bestR = c;
                retweetObj = o;
            }
        }
    }

    private void bestLike(JSONObject o) {
        if(o.getJSONObject("includes").has("tweets")){
            int c = Integer.parseInt(o.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getJSONObject("public_metrics").getString("like_count"));
            if (c > best_like) {
                best_like = c;
                likeObj = o;
            }
        }
    }

    private void update(Map<String, Integer> map, String source) {
        if (map.containsKey(source))
            map.put(source, map.get(source) + 1);
        else
            map.put(source, 1);
    }

    private void getCountry(JSONObject o) {
        String country = "";
        JSONObject buff;
        
        getRealCountry(o);

        if(o.getJSONObject("includes").getJSONArray("users").getJSONObject(0).has("location")) {
            String city = o.getJSONObject("includes").getJSONArray("users").getJSONObject(0).getString("location");

            // pip install geopy -> to make the command work
            String cmd = String.format("python -c \"import sys;from geopy.geocoders import Nominatim;dd={};tmp=Nominatim(user_agent='geoapiExercises').geocode(sys.argv[1], language='en');country=tmp[0].split(', ');dd['country']=country[-1];dd['lat']=tmp[1][0];dd['lon']=tmp[1][1];print(dd);\" \"%s\"", city);
            Runtime run = Runtime.getRuntime();
            Process pr = null;

            try {
                pr = run.exec(cmd);
                pr.waitFor();
                BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String asd = buf.readLine();
                buff = new JSONObject(asd);
                buff.put("name", Base64.getEncoder().encodeToString(city.getBytes(StandardCharsets.ISO_8859_1)));

                country = buff.getString("country");

                String key = digest(buff.toString().getBytes());
                geo.computeIfPresent(key, (k,v) -> v.put("pop", geo.get(key).getDouble("pop") + 0.01));
                geo.putIfAbsent(key, buff.put("pop", 2.0));
            } catch (Exception e) {
                //e.printStackTrace();
            }

            update(countries, country);
        }
    }

    private void getRealCountry(JSONObject o) {
        if(o.getJSONObject("includes").has("places")) {
            JSONObject result = new JSONObject();
            JSONObject place = o.getJSONObject("includes").getJSONArray("places").getJSONObject(0);
            result.put("country", place.getString("country"));
            result.put("name", Base64.getEncoder().encodeToString(place.getString("name").getBytes(StandardCharsets.ISO_8859_1)));
            result.put("lat", place.getJSONObject("geo").getJSONArray("bbox").get(1));
            result.put("lon", place.getJSONObject("geo").getJSONArray("bbox").get(0));

            String key = digest(result.toString().getBytes());
            realPositions.computeIfPresent(key, (k,v) -> v.put("pop", realPositions.get(key).getDouble("pop") + 0.01));
            realPositions.putIfAbsent(key, result.put("pop", 2.0));
        }
    }

    private String digest(byte[] input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        byte[] result = md.digest(input);
        return bytesToHex(result);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("stream", new Fields("tweety"));
    }

    private static String clear(String object) {
        object=object.toLowerCase();
        //remove urls
        object=object.replaceAll("((www\\.[^\\s]+)|(https?://[^\\s]+))", "");
        //remove user names
        object = object.replaceAll("@[^\\s]+", "");
        //remove # from hash tag
        object = object.replaceAll("#", "");
        //remove punctuation
        object = object.replaceAll("\\p{Punct}+", "");

        return object;
    }

    public String analysis(String text){
        String sentiment="";
        CoreDocument coreDocument = new CoreDocument(text);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreSentence> sentences = coreDocument.sentences();
        for(CoreSentence sentence : sentences) {
            sentiment = sentence.sentiment();
        }
        return sentiment;
    }
}
