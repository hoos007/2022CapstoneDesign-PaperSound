package org.techtown.paper_sound_original;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class BookDBRequest
{
    ObjectMapper objectMapper;
    HashMap<String, String> map;

    String json;
    HttpUtil util;
    JSONArray jsonArray;

    public BookDBRequest() {
        try
        {
            objectMapper = new ObjectMapper();
            map = new HashMap<>();
            json = objectMapper.writeValueAsString(map);
            util = new HttpUtil();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public JSONArray getArray()
    {
        try {
            util.execute(json);
            jsonArray = util.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  jsonArray;
    }
}

class HttpUtil extends AsyncTask<String, Void, JSONArray> {

    @Override
    protected JSONArray doInBackground(String... strings) {
        JSONArray memberJsonArr = null;
        try{
            String url = "<서버주소>";
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();

            ByteArrayOutputStream baos = null;
            InputStream is = null;
            String responseStr = null;

            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int nLength = 0;
                while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1)
                {
                    baos.write(byteBuffer, 0, nLength);
                }
                byteData = baos.toByteArray();

                responseStr = new String(byteData);

                JSONObject responseJSON = new JSONObject(responseStr);
                String result = (String) responseJSON.get("result");
                memberJsonArr = responseJSON.getJSONArray("bookData");
            }
            else
            {
                is = conn.getErrorStream();
                baos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int nLength = 0;
                while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1)
                {
                    baos.write(byteBuffer, 0, nLength);
                }
                byteData = baos.toByteArray();
                responseStr = new String(byteData);
                Log.i("info", "Data response error msg = " + responseStr);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
            Log.i("errorInfo", "error occured!" + e.getMessage());
        }

        return memberJsonArr;
    }
}
