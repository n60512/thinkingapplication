package com.example.yafun.thinkingapplication;

import android.util.Log;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.json.*;

public class ConnServer {

    private String database = null;
    private String webRequest = null;      //  web request;type:json
    private String imageID = "-1";

    /**
     * 處理遊戲 drawing,drawingmult,oneimage 建構子
     * @param database
     * @param content
     * @param crtuser
     */
    public ConnServer(String database, String content, String crtuser) {
        this.database = database;

        switch (database) {
            case "drawing":
                draw(content, crtuser);
                break;
            case "drawingmult":
                draw(content, crtuser);
                break;
            case "oneimage":
                oneimage(content, crtuser);
                break;
            default:
                ;
        }
    }

    /**
     * 處理遊戲 association 建構子
     * @param database
     * @param description
     * @param chosenList
     * @param crtuser
     */
    public ConnServer(String database, String description, String[] chosenList, String crtuser) {
        this.database = database;

        int index = 0;
        String tmpText = "";
        for (String option : chosenList)
            tmpText = tmpText + "'chosen" + Integer.toString(index = index + 1) + "','" + option + "',";

        String chosenContent = "column_create(" + tmpText.substring(0, tmpText.length() - 1) + ")";     // column_create('chosen1','0','chosen2','0' ...)
        //showMessage("test", tmpText); for test

        if (database.equals("association")) {
            association(description, chosenContent, crtuser);
        } else {
        }
    }

    /**
     * @param Name    pictureName
     * @param crtuser userName
     */
    private void draw(String Name, String crtuser) {

        JSONObject obj;
        //Object jsonOb = null;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://140.122.91.218/thinkingapp/connDB/drawing.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("database", database));
        params.add(new BasicNameValuePair("Name", Name));
        params.add(new BasicNameValuePair("crtuser", crtuser));

        try {
            request.setEntity(new UrlEncodedFormEntity(params));    //  set post params
            HttpResponse response = client.execute(request);        //  get web response
            HttpEntity resEntity = response.getEntity();
            webRequest = EntityUtils.toString(resEntity);             //  取得網頁 REQUEST

            showMessage("webRequest", webRequest);

            obj = new JSONObject(this.webRequest);  // parse web request
            imageID = obj.getJSONObject(database).getString("ID");
            showMessage("ID", imageID);

        } catch (java.io.IOException e) {
            showMessage("IOException", e.getMessage());
        } catch (org.json.JSONException e) {
            showMessage("JSON Error", e.getMessage());
        }

    }

    /**
     *  取得圖片流水號
     * @return
     */
    public String getImageID(){
        return imageID;
    }

    /**
     * Game association connection
     * @param description
     * @param chosenContent
     * @param crtuser
     */
    private void association(String description, String chosenContent, String crtuser) {

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://140.122.91.218/thinkingapp/connDB/association.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("Description", description));
        params.add(new BasicNameValuePair("ChosenContent", chosenContent));
        params.add(new BasicNameValuePair("crtuser", crtuser));

        try {
            request.setEntity(new UrlEncodedFormEntity(params));    //  set post params
            HttpResponse response = client.execute(request);        //  get web response
            HttpEntity resEntity = response.getEntity();
            webRequest = EntityUtils.toString(resEntity);             //  取得網頁 REQUEST

            showMessage("association webRequest", webRequest);

        } catch (java.io.IOException e) {
            showMessage("IOException", e.getMessage());
        }
    }

    /**
     * Game oneimage connection
     * @param data
     * @param crtuser
     */
    private void oneimage(String data, String crtuser) {

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://140.122.91.218/thinkingapp/connDB/oneimage.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("Data", data));
        params.add(new BasicNameValuePair("crtuser", crtuser));

        try {
            request.setEntity(new UrlEncodedFormEntity(params));    //  set post params
            HttpResponse response = client.execute(request);        //  get web response
            HttpEntity resEntity = response.getEntity();
            webRequest = EntityUtils.toString(resEntity);             //  取得網頁 REQUEST

            showMessage("oneimage webRequest", webRequest);

        } catch (java.io.IOException e) {
            showMessage("IOException", e.getMessage());
        }
    }

    private void showMessage(String title, String content) {
        Log.d(title, content);
    }

}


