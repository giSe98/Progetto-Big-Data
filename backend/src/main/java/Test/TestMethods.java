package Test;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import java.security.MessageDigest;

public class TestMethods {
    static Map<String, JSONObject> geo = new HashMap<>();

    static JSONObject extractJSON(JSONObject o, String what) {
        JSONObject ret = new JSONObject();

        JSONObject jsonObject = o.getJSONObject("includes").getJSONArray("tweets").getJSONObject(0);
        ret.put("author_id", jsonObject.getString("author_id"));
        ret.put("conversation_id", jsonObject.getString("conversation_id"));
        ret.put("created_at", jsonObject.getString("created_at"));
        ret.put("description", jsonObject.getString("text"));
        ret.put(what + "_count", jsonObject.getJSONObject("public_metrics").getInt(what + "_count"));

        return ret;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
//        String json = "{\"data\":{\"attachments\":{},\"author_id\":\"3306501383\",\"context_annotations\":[{\"domain\":{\"id\":\"123\",\"name\":\"Ongoing News Story\",\"description\":\"Ongoing News Stories like 'Brexit'\"},\"entity\":{\"id\":\"1220701888179359745\",\"name\":\"COVID-19\"}},{\"domain\":{\"id\":\"131\",\"name\":\"Unified Twitter Taxonomy\",\"description\":\"A taxonomy view into the Semantic Core knowledge graph\"},\"entity\":{\"id\":\"1220701888179359745\",\"name\":\"COVID-19\"}}],\"conversation_id\":\"1560269494387621888\",\"created_at\":\"2022-08-18T14:16:50.000Z\",\"entities\":{\"mentions\":[{\"start\":3,\"end\":16,\"username\":\"UzmEsraGunes\",\"id\":\"2512154114\"}]},\"geo\":{},\"id\":\"1560269494387621888\",\"lang\":\"tr\",\"possibly_sensitive\":false,\"public_metrics\":{\"retweet_count\":419,\"reply_count\":0,\"like_count\":0,\"quote_count\":0},\"referenced_tweets\":[{\"type\":\"retweeted\",\"id\":\"1560008737800290306\"}],\"reply_settings\":\"everyone\",\"source\":\"Twitter for Android\",\"text\":\"RT @UzmEsraGunes: Yeni Zelanda'da 26 yaşındaki bir gencin ölüm sebebinin BioNTech aşısına bağlı kalp zarı iltihaplanması olduğu açıklandı.…\"},\"includes\":{\"users\":[{\"created_at\":\"2015-06-02T10:51:32.000Z\",\"description\":\"Geleneksel ve Tamamlayıcı Tıp taraftarı\",\"id\":\"3306501383\",\"location\":\"İzmir, Türkiye\",\"name\":\"DrCengiz Demir\",\"profile_image_url\":\"https://pbs.twimg.com/profile_images/605764730976923648/WC084Ekq_normal.jpg\",\"protected\":false,\"public_metrics\":{\"followers_count\":56,\"following_count\":235,\"tweet_count\":2369,\"listed_count\":0},\"url\":\"\",\"username\":\"cengizhan5209\",\"verified\":false},{\"created_at\":\"2014-05-21T10:07:42.000Z\",\"description\":\"\uD83D\uDCCCKendin ol. Uzman Klinik Psikolog, Sporcu Psikologu (Arsenal)/ Istanbul Ünv. Psikoloji/ Beykent Klinik Psk/ Hacettepe Kimya/ Fen Lisesi\uD83D\uDD38Sağlık, Bilim, Siyaset\",\"id\":\"2512154114\",\"location\":\"İstanbul\",\"name\":\"Uzm.Esra Güneş Kaya\",\"pinned_tweet_id\":\"1551430696090083329\",\"profile_image_url\":\"https://pbs.twimg.com/profile_images/1521649558194995205/XrUT1xr7_normal.jpg\",\"protected\":false,\"public_metrics\":{\"followers_count\":44031,\"following_count\":775,\"tweet_count\":9979,\"listed_count\":93},\"url\":\"\",\"username\":\"UzmEsraGunes\",\"verified\":false}],\"tweets\":[{\"attachments\":{},\"author_id\":\"2512154114\",\"context_annotations\":[{\"domain\":{\"id\":\"123\",\"name\":\"Ongoing News Story\",\"description\":\"Ongoing News Stories like 'Brexit'\"},\"entity\":{\"id\":\"1220701888179359745\",\"name\":\"COVID-19\"}},{\"domain\":{\"id\":\"131\",\"name\":\"Unified Twitter Taxonomy\",\"description\":\"A taxonomy view into the Semantic Core knowledge graph\"},\"entity\":{\"id\":\"1220701888179359745\",\"name\":\"COVID-19\"}}],\"conversation_id\":\"1560008737800290306\",\"created_at\":\"2022-08-17T21:00:41.000Z\",\"entities\":{\"urls\":[{\"start\":180,\"end\":203,\"url\":\"https://t.co/6ACaCaTI7A\",\"expanded_url\":\"https://tr.euronews.com/2021/12/20/yeni-zelanda-26-yas-ndaki-gencin-olum-sebebi-biontech-as-s-na-bagl-kalp-zar-iltihaplanmas\",\"display_url\":\"tr.euronews.com/2021/12/20/yen…\",\"images\":[{\"url\":\"https://pbs.twimg.com/news_img/1559500262800236545/DCPL-GFf?format=jpg&name=orig\",\"width\":1000,\"height\":563},{\"url\":\"https://pbs.twimg.com/news_img/1559500262800236545/DCPL-GFf?format=jpg&name=150x150\",\"width\":150,\"height\":150}],\"status\":200,\"title\":\"Yeni Zelanda: Bir kişi Covid-19 aşısına bağlı miyokarditten öldü\",\"description\":\"Yeni Zelandalı yetkililer 26 yaşındaki bir kişinin Pfizer/BioNTech'in Covid-19 aşısına bağlı kalp zarı iltihaplanması (miyokardit) sonucu hayatını kaybettiğini duyurdu.\",\"unwound_url\":\"https://tr.euronews.com/2021/12/20/yeni-zelanda-26-yas-ndaki-gencin-olum-sebebi-biontech-as-s-na-bagl-kalp-zar-iltihaplanmas\"}]},\"geo\":{},\"id\":\"1560008737800290306\",\"lang\":\"tr\",\"possibly_sensitive\":false,\"public_metrics\":{\"retweet_count\":419,\"reply_count\":26,\"like_count\":1131,\"quote_count\":12},\"reply_settings\":\"everyone\",\"source\":\"Twitter for Android\",\"text\":\"Yeni Zelanda'da 26 yaşındaki bir gencin ölüm sebebinin BioNTech aşısına bağlı kalp zarı iltihaplanması olduğu açıklandı. Bizde de Mehmet Ceyhan covid aşıları kalbi koruyor diyor.\\n https://t.co/6ACaCaTI7A\"}]},\"matching_rules\":[{\"id\":\"1558106798866972673\",\"tag\":\"covid\"}]}";
//        System.out.println(new JSONObject(json));
//        System.out.println(extractJSON(new JSONObject(json), "retweet"));
//        System.out.println(extractJSON(new JSONObject(json), "like"));

        System.out.println(Base64.getEncoder().encodeToString("Tietê".getBytes()));
        System.out.println();
        System.out.println(new String(Base64.getDecoder().decode("VGlldMOq")));

        System.out.println("ASD".split(",")[0]);
    }
}
