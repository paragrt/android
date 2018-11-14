package com.lfg.mobileang;

import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchDataAsync extends AsyncTask<OkHttpClient, Void, String> {
    JSONObject jObject;
    ArrayAdapter<String> listAdapter;
    Context mycontext ;
    TextToSpeech t1;
    String cmd;
    Button rr;
    String baseURL;

    public FetchDataAsync(String aBaseURL, Button readresult, ArrayAdapter<String> adapter, String aCmd, Context ctx) {
        this.listAdapter = adapter;
        this.cmd = aCmd;
        this.mycontext = ctx;
        this.rr = readresult;
        this.baseURL = aBaseURL;
    }
    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     */
    @Override
    protected String doInBackground(OkHttpClient...httpClients) {
        //assume that httpClients has 1 httpClient

        Request request = new Request.Builder()
                .url(this.baseURL+"/services/apexrest/RESTParag?cmd="+this.cmd)
                .build();
        Response response = null;
        try {
            response = httpClients[0].newCall(request).execute();
            String jsonStr = readStream(response.body().byteStream());
            jObject = new JSONObject(jsonStr);

            return jsonStr;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR:" + e.getMessage();
        }

    }
    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }
    static String readStream(InputStream in) throws IOException {
        byte[] arr = new byte[4096];
        StringBuffer sb = new StringBuffer();
        int count = 0;
        while ((count = in.read(arr)) != -1) {
            String s = new String(arr, 0, count);
            sb.append(s);
        }
        return sb.toString();
    }
    protected void onPostExecute(String result) {
System.out.println("===========>CMD = " + cmd);
        try {
            if ("bob".equalsIgnoreCase(cmd)) {
                handleBobJSON(result);
            } else if ("bobdetail".equalsIgnoreCase(cmd)) {
                handleError(result);
            } else if ("pending".equalsIgnoreCase(cmd)) {
                handlePendingJSON(result);
            } else if ("delegate".equalsIgnoreCase(cmd)) {
                handleBobDashBoardJSON("your Delegate", result);
            } else if ("dashboard".equalsIgnoreCase(cmd)) {
                handleBobDashBoardJSON("your", result);
            } else {
                // just dump it on the screen
                listAdapter.add(result);
            }
        } catch ( Exception e) {
            listAdapter.add("There was an error fetching Data. " + e.getMessage());
        }
        rr.performClick();
    }

    public void handleBobDashBoardJSON(String whose, String result)  throws Exception {

            jObject = new JSONObject(result);
            JSONObject root = jObject.getJSONObject("chartJsonRoot");
            JSONArray jsonArray = root.getJSONArray("valueByProducts");
            listAdapter.clear();
            listAdapter.add("Here is a list of "+whose+" holdings.");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject row = jsonArray.getJSONObject(i);
                double percnt = row.getDouble("percentage")/100.00D;
                double currencyval = row.getDouble("value");
                NumberFormat percentInstance = NumberFormat.getPercentInstance();
                percentInstance.setMinimumFractionDigits(2);
                percentInstance.setMaximumFractionDigits(2);

                String rowStr = row.getString("name") + " has a value of " + NumberFormat.getCurrencyInstance().format(currencyval)
                        + " and is " + percentInstance.format(percnt);
                listAdapter.add(rowStr);
            }
    }
    public void handleError(String result) {
        try {
            jObject = new JSONObject(result);

            listAdapter.add(jObject.getString("errorMsg"));
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
    public void handleBobJSON(String result)  throws Exception {

            jObject = new JSONObject(result);
            JSONObject root = jObject.getJSONObject("contracts");
            JSONArray jsonArray = root.getJSONArray("contracts");
            listAdapter.clear();
            listAdapter.add("Here is a list of your CONTRACTS.");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject row = jsonArray.getJSONObject(i);
                JSONArray clients = row.getJSONArray("clients");
                String clientStr = "Unknown";
                if ( clients != null && clients.length() >0) {
                    clientStr = clients.getJSONObject(0).getString("firstName") + " " + clients.getJSONObject(0).getString("lastName");
                }
                String rowStr = "Policy Number "+ row.getString("contract")
                        + " for " + clientStr
                        + " is " + row.getString("lob").replace("<br>","") ;
                listAdapter.add(rowStr);
            }

    }
    public void handlePendingJSON(String result) throws Exception {

            jObject = new JSONObject(result);
            JSONObject root = jObject.getJSONObject("pendingBusiness");
            JSONArray jsonArray = root.getJSONArray("pendingCountsByStatus");
            listAdapter.clear();
            listAdapter.add("Here is a list of your PENDING Business.");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject row = jsonArray.getJSONObject(i);

                String rowStr = "You have " + row.getString("count") + " " + row.getString("statusName") + " contracts.";
                listAdapter.add(rowStr);
            }

    }
}