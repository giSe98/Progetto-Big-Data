import org.apache.avro.data.Json;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TwitterSpout extends BaseRichSpout {
    private SpoutOutputCollector spoutOutputCollector;
    private int i = 0; // SERVE ?
    private BufferedReader reader;
    private TopologyContext contex;
    private URIBuilder uriBuilder;
    private static String bearer = "AAAAAAAAAAAAAAAAAAAAAFS6XgEAAAAAJEjYe3xU0MVazyixZSfa4%2B0c59E%3DgwwLmvJFo42oRCNko0gYfwoy5IeVqQ6WZqdktyn6qAfdoOHaD1";
            //System.getenv("BEARER_TOKEN");
    String URL = "tweet.fields=attachments,author_id,context_annotations,conversation_id,created_at,entities,geo,id,in_reply_to_user_id,lang,possibly_sensitive,public_metrics,referenced_tweets,reply_settings,source,text,withheld&expansions=attachments.poll_ids,attachments.media_keys,author_id,geo.place_id,in_reply_to_user_id,referenced_tweets.id,entities.mentions.username,referenced_tweets.id.author_id&place.fields=contained_within,country,country_code,full_name,geo,id,name,place_type&poll.fields=duration_minutes,end_datetime,id,options,voting_status&user.fields=created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld";
    ArrayList<JSONObject> tweets;//

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.spoutOutputCollector = collector;
        this.contex = context;
        HttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
        try {
            uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/search/stream?" + URL);
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Authorization", String.format("Bearer %s", bearer));

            Map<String, String> rules1 = new HashMap<>();
            rules1.put("context:123.1220701888179359745", "covid");
            TwitterStream.setupRules(rules1);

            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(entity.getContent()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void nextTuple() {
        try {
            String line = reader.readLine();

            if(line != null && i < 20) {
                //System.out.println(i+""+line);
                JSONObject object = new JSONObject(line);
                //String dato = object.getJSONObject("data").getString("text");
                //dato=clean(dato);
                //System.out.println(i+" C "+dato);
                //SentimentAnalysis analizer =new SentimentAnalysis(dato);
                //String sentiment=analizer.analysis();
                //System.out.println("SENTIMENT "+i+" --> "+sentiment);
                tweets.add(object);
                this.spoutOutputCollector.emit("stream", new Values(line));
                i++;
            }
            else {
                for( JSONObject o : tweets){
                    if(o.getJSONObject("data").getString("lang").equals("en")){
                        String dato = o.getJSONObject("data").getString("text");
                        dato=clean(dato);
                        SentimentAnalysis analizer =new SentimentAnalysis(dato);
                        String sentiment=analizer.analysis();
                        //decidere cosa fare con il risultatao della sentiment
                    }
                    else{
                        //decidere cosa fare con quelli che non sono in inglese
                    }
                }
                System.exit(0);
                //this.spoutOutputCollector.emit("stream", new Values(new String("CIAO LINEA NULL")));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("stream", new Fields("tweety"));
    }

    private static String clean(String object) {
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


}
