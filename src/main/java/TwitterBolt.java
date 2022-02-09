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
    private ArrayList<JSONObject> tweets = new ArrayList<>();
    private int i=0; //indice tweet

    private int pL=0; // indice like
    private String tpl=""; //testo più like

    private int pR=0; // indice retweet
    private String tpr=""; //testo più retweet

    private int pC=0; // indice commenti
    private String tpc=""; //testo più commenti

    private StanfordCoreNLP stanfordCoreNLP = null;
    private HashMap<String,Integer> valoriAnalisi= new HashMap<>();
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
            //System.out.println("P= "+valoriAnalisi.get("Positive")+" N= "+valoriAnalisi.get("Negative")+" neutral= "+valoriAnalisi.get("Neutral"));
            //System.out.println("testo con più like: "+tpl+" num like: "+pL);
            //System.out.println("testo con più retweet: "+tpr+" num retweet: "+pR);
            //System.out.println("testo con più commenti: "+tpc+" num commenti: "+pC);
            System.exit(0);
        }

        String line = input.getString(0);
        System.out.println(line);
        //JSONObject o = new JSONObject(line);
        //if(o.getJSONObject("data").getString("lang").equals("en")) {
        //    String dato = o.getJSONObject("data").getString("text");
        //    dato = clean(dato);
       //     String sentiment = analysis(dato);
        //    valoriAnalisi.put(sentiment,valoriAnalisi.get(sentiment)+1);
        //}

        //tweet con più like
        //if(Integer.parseInt(o.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getJSONObject("public_metrics").getString("like_count"))>pL){
        //    pL=Integer.parseInt(o.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getJSONObject("public_metrics").getString("like_count"));
        //    tpl=o.getJSONObject("data").getString("text");
        //}

        // tweet con più retweet
        //if(Integer.parseInt(o.getJSONObject("data").getJSONObject("public_metrics").getString("retweet_count"))>pR){
        //    pR=Integer.parseInt(o.getJSONObject("data").getJSONObject("public_metrics").getString("retweet_count"));
        //    tpr=o.getJSONObject("data").getString("text");//
        //}

        //tweet con più commenti
        //if(Integer.parseInt(o.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getJSONObject("public_metrics").getString("reply_count"))>pL){
        //    pL=Integer.parseInt(o.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0).getJSONObject("public_metrics").getString("reply_count"));
        //    tpl=o.getJSONObject("data").getString("text");
        //}

        i++;

        //todo
        //quello con più like, restituire numero e testo    NON FUNZIONA
        // quello con più retweet, restituire numero e testo    FATTO
        //quello con più commenti, restituire numero e testo    NON FUNZIONA, STESSO PROBLEMA DEL PRIMO
        //prendere la localizzazione ( con file batch ) DIFFERENZA TRA GEO E USER LOCATION?
        //device


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
