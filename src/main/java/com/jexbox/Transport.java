package com.jexbox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;

public class Transport {
    public static void send(String host, JsonObject json, String appId) throws TransportException, UnsupportedEncodingException{
    	send(host, json.toString(), "application/json", appId);
    }

    public static void send(String host, InputStream is, String appId) throws TransportException, UnsupportedEncodingException{
    	send(host, is, "application/json", appId);
    }

    public static void send(String host, String data, String ct, String appId) throws TransportException, UnsupportedEncodingException{
    	send(host, new ByteArrayInputStream(data.getBytes("UTF-8")), ct, appId);
    }

    public static void send(String host, InputStream data, String ct, String appId) throws TransportException{
        HttpURLConnection conn = null;
        try {
            URL url = new URL(host);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true); 
            conn.setChunkedStreamingMode(0);
            if(ct != null) {
                conn.addRequestProperty("Content-Type", ct);
            }
            conn.addRequestProperty("Authorization", appId);

            OutputStream out = null;
            try {
                out = conn.getOutputStream();
            
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = data.read(buffer)) != -1)
                {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                if(out != null) {
                    out.close();
                }
            }

            int status = conn.getResponseCode();
            if(status / 100 != 2) {
                throw new TransportException(host);
            }
        } catch (IOException e) {
            throw new TransportException(String.format("Connection error while sending data to %s", host), e);
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
    }

}
