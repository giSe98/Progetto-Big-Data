import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitterBolt extends BaseRichBolt {
    private OutputCollector collector;
    private final int numTweet=20;
    private ArrayList<JSONObject> tweets = new ArrayList<>();
    private int i=0; //indice tweet

    private StanfordCoreNLP stanfordCoreNLP = null;
    private Map<String,Integer> valoriAnalisi= new HashMap<>();
    private Map<String, Integer> device = new HashMap<>();

    private int bestR = Integer.MIN_VALUE; // indice retweet
    private JSONObject retweetObj; // object
    private int retweet = 0;

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        valoriAnalisi.put("Negative",0);
        valoriAnalisi.put("Positive",0);
        valoriAnalisi.put("Neutral",0);
        stanfordCoreNLP = Pipeline.getPipeline();
        this.collector=collector;
    }

    @Override
    public void execute(Tuple input) {
        if(input.getString(0).equals("tweet finiti")){
            System.out.println("--------------------------------------------------------------------------------");
            //stampa sentimenti analysys
            System.out.println("P= "+valoriAnalisi.get("Positive")+" N= "+valoriAnalisi.get("Negative")+" neutral= "+valoriAnalisi.get("Neutral"));
            System.out.println("--------------------------------------------------------------------------------");
            //stampa più famoso retweet con valori di like ecommenti
            System.out.println("tweet più retweettato: "+retweetObj.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getString("text"));
            System.out.println("num like= "+retweetObj.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getJSONObject("public_metrics").getString("like_count")+ " num reply= "+retweetObj.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getJSONObject("public_metrics").getString("reply_count"));
            System.out.println("--------------------------------------------------------------------------------");
            //stampa device
            device.entrySet().forEach(entry->{
                System.out.println(entry.getKey()+" "+entry.getValue());
            });
            System.out.println("--------------------------------------------------------------------------------");
            //stampa se sono retweet
            System.out.println("sono retweet= "+retweet+" non lo sono= "+(numTweet-retweet));
            System.exit(0);
        }

        String line = input.getString(0);
        //System.out.println(line);
        JSONObject o = new JSONObject(line);
        if(o.getJSONObject("data").getString("lang").equals("en")) {
            String dato = o.getJSONObject("data").getString("text");
            dato = clean(dato);
            String sentiment = analysis(dato);
            valoriAnalisi.put(sentiment,valoriAnalisi.get(sentiment)+1);
        }

        //query sui tweet
        checkRetweet(o);

        //query sul tipo di device
        String source = o.getJSONObject("data").getString("source");
        updateSource(source);

        i++;
        collector.emit("stream", new Values(input.getString(0)));
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
