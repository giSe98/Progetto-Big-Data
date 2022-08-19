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
        String json = "\"tweets\": {\"asd\",\"asdasdasd\"}";
        System.out.println(new JSONObject(json));
    }
}
