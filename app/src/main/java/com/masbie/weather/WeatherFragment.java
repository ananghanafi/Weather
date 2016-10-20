package com.masbie.weather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AnangHanafi on 19/10/2016.
 */
public class WeatherFragment extends Fragment {


    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;

    Handler handler;

    public WeatherFragment() {
        handler = new Handler();
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                updateWeatherData(new CityPreference(getActivity()).getCity());


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        cityField = (TextView) rootView.findViewById(R.id.kota);
        updatedField = (TextView) rootView.findViewById(R.id.updateKota);
        detailsField = (TextView) rootView.findViewById(R.id.details);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.temperatur);
        weatherIcon = (TextView) rootView.findViewById(R.id.cuaca);


        return rootView;
    }
    public class CityPreference {

        SharedPreferences prefs;

        public CityPreference(Activity activity){
            prefs = activity.getPreferences(Activity.MODE_PRIVATE);
        }

        // If the user has not chosen a city yet, return
        // Sydney as the default city
        String getCity(){
            return prefs.getString("city", "Sydney, AU");
        }

        void setCity(String city){
            prefs.edit().putString("city", city).commit();
        }

    }
    public void changeCity(String city){
        updateWeatherData(city);
    }

    private void updateWeatherData(final String city) {
        new Thread() {
            public void run() {
                final JSONObject json = AmbilData.getJSON(getActivity(), city);
                if (json == null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.tidak_ada),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        public void run() {
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp")) + " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt") * 1000));
            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (Exception e) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = getActivity().getString(R.string.cerah);
            } else {
                icon = getActivity().getString(R.string.malam_cerah);
            }
        } else {
            switch (id) {
                case 2:
                    icon = getActivity().getString(R.string.badai);
                    break;
                case 3:
                    icon = getActivity().getString(R.string.gerimis);
                    break;
                case 7:
                    icon = getActivity().getString(R.string.mendung);
                    break;
                case 8:
                    icon = getActivity().getString(R.string.berawan);
                    break;
                case 6:
                    icon = getActivity().getString(R.string.salju);
                    break;
                case 5:
                    icon = getActivity().getString(R.string.hujan);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }
}
