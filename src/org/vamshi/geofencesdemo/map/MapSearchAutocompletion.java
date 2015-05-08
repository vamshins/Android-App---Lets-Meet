
package org.vamshi.geofencesdemo.map;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vamshi.geofencesdemo.geofence.callbacks.GeofenceCallbacks;

import android.os.AsyncTask;
import android.util.Log;

public class MapSearchAutocompletion extends AsyncTask<Void, Void, ArrayList<String>> {

    private static final String LOG_TAG = MapSearchAutocompletion.class.getSimpleName();

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyAJ_yqB75qxDuxdjJxEWOqb3nk9AWMOvt0";

    private String mInput;
    private GeofenceCallbacks mListener;

    public MapSearchAutocompletion(final String input, final GeofenceCallbacks listener) {
        mInput = input;
        mListener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(final Void... params) {

        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?sensor=false&key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(mInput, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL");
            e.printStackTrace();
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API");
            e.printStackTrace();
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results");
            e.printStackTrace();
        }

        return resultList;
    }

    @Override
    protected void onPostExecute(final ArrayList<String> result) {
        mListener.showSuggestions(result);
    }

}
