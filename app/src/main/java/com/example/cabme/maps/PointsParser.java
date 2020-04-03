package com.example.cabme.maps;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    TaskLoadedCallback taskCallback;
    String directionMode;

    /**
     * This parses all the points for polylines in the directions API in driving mode and 
     * calls to draw poylines for the i-th row
     * References & Sources:
     * - https://github.com/divindvm/Android-DrawOnMap
     * - https://github.com/Vysh01/android-maps-directions
     * @param mContext
     * @param directionMode
     */
    public PointsParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    
    @Override
    /**
     * This parses the data in non-ui thread
     * @param jsonData
     * @return
     */
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("MAPSLOG", jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.d("MAPSLOG", parser.toString());

            // Starts parsing data
            routes = parser.parse(jObject);
            Log.d("MAPSLOG", "Executing routes");
            Log.d("MAPSLOG", routes.toString());

        } catch (Exception e) {
            Log.d("MAPSLOG", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    /**
     * This executes in UI thread, after the parsing process
     * @param result
     */
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            if (directionMode.equalsIgnoreCase("walking")) {
                lineOptions.width(10);
                lineOptions.color(Color.MAGENTA);
            } else {
                lineOptions.width(10);
                lineOptions.color(Color.GRAY);
            }
            Log.d("MAPSLOG", "onPostExecute lineoptions decoded");
        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            //mMap.addPolyline(lineOptions);
            taskCallback.onTaskDone(lineOptions);

        } else {
            Log.d("MAPSLOG", "without Polylines drawn");
        }
    }
}