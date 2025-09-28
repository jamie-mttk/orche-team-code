package com.mttk.orche.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApiExample {
    public static void main(String[] args) {
        try {
            String apiKey = "6d9a9cd9461eda4a8245d87ac2dfec28";
            String city = "London";
            String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey
                    + "&units=metric";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(response.toString());
            } else {
                System.out.println("GET请求未成功，错误码：" + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}