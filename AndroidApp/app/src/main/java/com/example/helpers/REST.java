package com.example.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class REST {
    private static BufferedWriter bufferedWriter;

    public static String sendPost(String postUrl, String info) throws IOException {
        URL url = new URL(postUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        setConnectionParameters(httpURLConnection, "POST");
        httpURLConnection.setRequestProperty("Accept", "application/json");

        OutputStream outputStream = httpURLConnection.getOutputStream();
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        bufferedWriter.write(info);
        bufferedWriter.close();
        outputStream.close();

        int code = httpURLConnection.getResponseCode();
        System.out.println("Response code to POST method is " + code);

        return handleResponse(httpURLConnection, code);
    }

    public static String sendGet(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        setConnectionParameters(httpURLConnection, "GET");
        httpURLConnection.setRequestProperty("Accept", "application/json");

        int code = httpURLConnection.getResponseCode();
        System.out.println("Response code to GET method is " + code);

        return handleResponse(httpURLConnection, code);
    }

    public static String sendPut(String urlStr, String info) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        setConnectionParameters(httpURLConnection, "PUT");
        httpURLConnection.setRequestProperty("Accept", "application/json");

        OutputStream outputStream = httpURLConnection.getOutputStream();
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
        bufferedWriter.write(info);
        bufferedWriter.close();
        outputStream.close();

        int code = httpURLConnection.getResponseCode();
        System.out.println("Response code to PUT method is " + code);

        return handleResponse(httpURLConnection, code);
    }

    public static String sendDelete(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        setConnectionParameters(httpURLConnection, "DELETE");
        httpURLConnection.setRequestProperty("Accept", "application/json");

        int code = httpURLConnection.getResponseCode();
        System.out.println("Response code to DELETE method is " + code);

        return handleResponse(httpURLConnection, code);
    }

    private static void setConnectionParameters(HttpURLConnection httpURLConnection, String method) throws ProtocolException {
        httpURLConnection.setRequestMethod(method);
        httpURLConnection.setConnectTimeout(20000);
        httpURLConnection.setReadTimeout(20000);
        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        httpURLConnection.setRequestProperty("Accept", "*/*");
        httpURLConnection.setDoInput(true);
        if (method.equals("POST") || method.equals("PUT")) {
            httpURLConnection.setDoOutput(true);
        }
    }

    private static String handleResponse(HttpURLConnection httpURLConnection, int code) throws IOException {
        if (code == HttpURLConnection.HTTP_OK) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
            bufferedReader.close();
            return response.toString();
        } else {
            return "Error";
        }
    }
}
