package Test;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TestMicronaut {
    public static void sendData(String json, String endpoint) {
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

    private static String createStringGeo(Map<String, JSONObject> geo) {
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

    private static String digest(byte[] input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        byte[] result = md.digest(input);
        return bytesToHex(result);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Map<String, JSONObject> asd = new HashMap<>();

        JSONObject a = new JSONObject("{\"country\":\"Japan\",\"name\":\"Pz8/\",\"lon\":139.7594549,\"lat\":35.6828387}");
        String key = digest(a.toString().getBytes());
        a.put("pop", 2.0);
        asd.put(key, a);
        String json = createStringGeo(asd);
        System.out.println(json);
        sendData(json,"/maps/realPositions");
    }
}
