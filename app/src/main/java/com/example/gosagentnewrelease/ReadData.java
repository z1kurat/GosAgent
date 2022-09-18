package com.example.gosagentnewrelease;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadData {
    private HttpURLConnection Connection;
    private String AnswerDB;

    private OnMarkersReadListener mListener;
    private OnLotReadListener lListener;

    public Markers Markers = new Markers();
    public LotInformation Lot = new LotInformation();
    public FavoritesLotInformation FavoritesLotInformation = new FavoritesLotInformation();

    public interface OnMarkersReadListener {
        void onMarkersRead(Boolean isDone);
    }

    public interface OnLotReadListener {
        void onLotsRead(Boolean isDone);
    }

    class Markers {
        ExecutorService executor;
        Handler handler;

        public void execute () {
            executor = Executors.newSingleThreadExecutor();
            handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                Boolean resultWord = GetMarkersData();
                handler.post(() -> mListener.onMarkersRead(resultWord));
            });
        }
    }

    class LotInformation {
        ExecutorService executor;
        Handler handler;

        public void execute (String[] locations) {
            executor = Executors.newSingleThreadExecutor();
            handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                Boolean resultWord = GetLotData(locations[0], locations[1]);
                handler.post(() ->  lListener.onLotsRead(resultWord));
            });
        }
    }

    class FavoritesLotInformation {
        ExecutorService executor;
        Handler handler;

        public void execute() {
            executor = Executors.newSingleThreadExecutor();
            handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                Boolean resultWord = GetFavoritesLotData();
                handler.post(() ->  lListener.onLotsRead(resultWord));
            });
        }
    }

    public void setOnMarkersReadListener(OnMarkersReadListener listener) { mListener = listener; }

    public void setOnLotsReadListener(OnLotReadListener listener) {
        lListener = listener;
    }

    public boolean GetMarkersData() {
        String typeConnection = PluginData.DB_HOST + PluginData.ACTION_GET_MARKERS + PluginData.TABLE_NAME + PluginModel.Data.MarkerType;
        if (!CanGetData(typeConnection))
            return false;

        ParseMarkersData();
        return true;
    }

    public Boolean GetFavoritesLotData() {
        String typeConnection = PluginData.DB_HOST + PluginData.ACTION_GET_FAVORITES_LOT_INFORMATION + PluginModel.getTableName() +
                                    "&name=";
        for (FavoritesLot favoritesLot : PluginModel.Data.FavoritesLots) {
            if (!CanGetData(typeConnection + favoritesLot.getName()))
                return false;

            ParseLotInformation();
        }

        return true;
    }

    public Boolean GetLotData(String x, String y) {
        String typeConnection = PluginData.DB_HOST + PluginData.ACTION_GET_LOT_INFORMATION + PluginModel.getTableName() +
                                "&X=" + x + "&Y=" + y;
        if (!CanGetData(typeConnection))
            return false;

        ParseLotInformation();
        return true;
    }

    private boolean CanGetData(String typeConnection) {
        if (!SetConnection(typeConnection))
            return false;

        return GetServerAnswer();
    }

    private boolean SetConnection(String typeConnection) {
        try {
            Log.d("GosAgent", "start connection");

            Connection = (HttpURLConnection) new URL(typeConnection).openConnection();
            Connection.setReadTimeout(10000);
            Connection.setConnectTimeout(15000);
            Connection.setRequestMethod("POST");
            Connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            Connection.setDoInput(true);

            Connection.connect();

            Log.d("GosAgent", "successful connection");
        } catch (Exception e) {
            Log.d("GosAgent", "error connection: " + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean GetServerAnswer() {
        try {
            InputStream inputStream = Connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder answerDB = new StringBuilder();

            String getLotInformation = bufferedReader.readLine();

            while (getLotInformation != null) {
                answerDB.append(getLotInformation);
                getLotInformation = bufferedReader.readLine();
            }

            Log.d("GosAgent", "Successful get information");

            this.AnswerDB = answerDB.toString();
            this.AnswerDB = this.AnswerDB.substring(0, this.AnswerDB.indexOf("]") + 1);

            inputStream.close();
            bufferedReader.close();

        } catch (Exception e) {
            Log.d("GosAgent", "error get information " + e.getMessage());
            return false;
        } finally {
            if (Connection != null)
                Connection.disconnect();
            Log.d("GosAgent", "connection closed");
        }
        return true;
    }

    private void ParseMarkersData() {
        try {
            JSONArray JSON_All_Data = new JSONArray(AnswerDB);
            JSONObject JSON_data;

            int attributeNumber = 0;

            while (attributeNumber < JSON_All_Data.length()) {
                JSON_data = JSON_All_Data.getJSONObject(attributeNumber);
                PluginModel.Data.Markers.add(new MarkerData(JSON_data.getInt("id"), JSON_data.getDouble("coordinatesX"),
                        JSON_data.getDouble("coordinatesY")));
                attributeNumber++;
            }
        } catch (Exception e) {
            Log.d("GosAgent", "fatal error " + e.getMessage());
        }
    }

    private void ParseLotInformation() {
        try {
            JSONArray JSON_All_Data = new JSONArray(AnswerDB);
            JSONObject JSON_data;

            int attributeNumber = 0;

            while (attributeNumber < JSON_All_Data.length()) {
                JSON_data = JSON_All_Data.getJSONObject(attributeNumber);

                PluginModel.Data.LotData.add(ParseAttributeInformation(JSON_data));

                Log.d("GosAgent","success parse information lot");

                attributeNumber++;
            }
        } catch (Exception errorMessage) {
            Log.d("GosAgent", "fatal error " + errorMessage.getMessage());
        }
    }

    private LotData ParseAttributeInformation(JSONObject JSON_data) {
        LotData allAttributeInformation = new LotData();
        List<String> description = new ArrayList<>();

        for (String tag : PluginData.AllTegLot) {
            String attributeInformation;
            try {
                attributeInformation = JSON_data.getString(tag);

                if (attributeInformation.length() <= 1)
                    continue;

                switch (tag) {
                    case "number":          allAttributeInformation.number = attributeInformation; break;
                    case "link":            allAttributeInformation.link = attributeInformation; break;
                    case "location":        allAttributeInformation.location = attributeInformation; break;
                    case "monthly_payment": allAttributeInformation.monthlyPayment = attributeInformation; break;
                    case "initial_price":   allAttributeInformation.initialPrice = attributeInformation; break;
                    default:                description.add(attributeInformation); break;
                }

            } catch (JSONException errorMessage) {
                errorMessage.printStackTrace();
            }
        }

        allAttributeInformation.description.addAll(description);
        return allAttributeInformation;
    }

    public void clear() {
        Connection = null;
        AnswerDB = null;
        Markers = new Markers();
        Lot = new LotInformation();
    }
}
