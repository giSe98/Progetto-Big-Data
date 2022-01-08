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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class TwitterSpout extends BaseRichSpout {
    private SpoutOutputCollector spoutOutputCollector;
    private int i = 0; // SERVE ?
    private BufferedReader reader;
    private TopologyContext contex;
    private URIBuilder uriBuilder;
    private static String bearer = System.getenv("BEARER_TOKEN");

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.spoutOutputCollector = collector;
        this.contex = context;
        HttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
        try {
            uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/search/stream");
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

            if(line != null && i < 10) {
                this.spoutOutputCollector.emit("stream", new Values(line));
                i++;
            }
            else {
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
}
