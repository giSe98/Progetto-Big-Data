import com.sun.xml.bind.v2.TODO;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class TwitterBolt extends BaseRichBolt {
    private OutputCollector collector;

    private final int numTweet=100;
    private ArrayList<JSONObject> tweets = new ArrayList<>();
    private int i=0; //indice tweet

    private StanfordCoreNLP stanfordCoreNLP = null;

    private Map<String,Integer> valoriAnalisi= new HashMap<>();
    private Map<String, Integer> device = new HashMap<>();
    private Map<String, Integer> countries = new HashMap<>();

    private int bestR = Integer.MIN_VALUE; // indice retweet
    private JSONObject retweetObj; // object
    private int retweet = 0;
    private String best_country="";
    private int best_like = 0; //dovrebbe contare il numero di like di ogni tweet e restituire quello migliore ma da null pointer exception
    private JSONObject likeObj; // object

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
            //stampa più famoso retweet con valori di like e commenti
            System.out.println("tweet più retweettato: "+retweetObj.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getString("text"));
            System.out.println("num like= "+retweetObj.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getJSONObject("public_metrics").getString("like_count")+ " num reply= "+retweetObj.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getJSONObject("public_metrics").getString("reply_count"));
            System.out.println("--------------------------------------------------------------------------------");
            //stampa tweet con più like
            System.out.println("tweet con più like: "+likeObj.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getString("text")+" numLike: "+best_like);
            //stampa device
            device.entrySet().forEach(entry->{
                System.out.println(entry.getKey()+" "+entry.getValue());
            });
            System.out.println("--------------------------------------------------------------------------------");
            //stampa se sono retweet
            System.out.println("sono retweet= "+retweet+" non lo sono= "+(numTweet-retweet));
            System.out.println("--------------------------------------------------------------------------------");
            //stampa device
            countries.entrySet().forEach(entry->{
                System.out.println(entry.getKey()+" "+entry.getValue());
            });
            System.out.println("--------------------------------------------------------------------------------");
            //stampa best countries
            System.out.println("best_country -> " + best_country);

            System.exit(0);
        }

        String line = input.getString(0);
        JSONObject o = new JSONObject(line);
        if(o.getJSONObject("data").getString("lang").equals("en")) {
            String dato = o.getJSONObject("data").getString("text");
            dato = clear(dato);
            String sentiment = analysis(dato);
            valoriAnalisi.put(sentiment,valoriAnalisi.get(sentiment)+1);
        }

        //query per i retweet
        checkRetweet(o);

        //query per i like
        bestLike(o);

        //query sul tipo di device
        String source = o.getJSONObject("data").getString("source");
        update(device, source);

        //query su location dell'utente e sulla location più attiva
        getCountry(o);
        countries.remove("null");
        countries.remove("None");
        best_country = Collections.max(countries.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();

        //TODO Ampliare SentimentAnalysis e like count

        i++;
        collector.emit("stream", new Values(input.getString(0)));
    }

    private void checkRetweet(JSONObject o) {
        if (o.getJSONObject("data").has("referenced_tweets")) {
            //retweet con più like
            bestRetweet(o);
            retweet++;
        }
    }

    private void bestRetweet(JSONObject o) {
        int c = Integer.parseInt(o.getJSONObject("data").getJSONObject("public_metrics").getString("retweet_count"));
        if (c > bestR) {
            bestR = c;
            retweetObj = o;
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
        //System.out.println(o.getJSONObject("includes"));
        if(o.getJSONObject("includes").getJSONArray("users").getJSONObject(0).has("location")) {
            String city = o.getJSONObject("includes").getJSONArray("users").getJSONObject(0).getString("location");

            // pip install geopy -> to make the command work
            String cmd = String.format("python -c \"import sys;from geopy.geocoders import Nominatim;print(str(Nominatim(user_agent='geoapiExercises').geocode(sys.argv[1], language='en')).split(', ')[-1])\" \"%s\"", city);
            Runtime run = Runtime.getRuntime();
            Process pr = null;

            try {
                pr = run.exec(cmd);
                pr.waitFor();
                BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                country = buf.readLine();
                //if (country.equals("None")) System.out.println("VALORE NONE -> " + o.getJSONObject("includes"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            update(countries, country);
        }
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
