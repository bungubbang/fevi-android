package com.fevi.fadong.support;

import com.fevi.fadong.domain.Member;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 1000742 on 15. 1. 28..
 */
public class FadongHttpClient {

    public FadongHttpClient() {
    }

    public String sendLogin(String url, String parameter) {
        String data = null;
        String line = null;

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-unlencoded");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();

            os.write(parameter.getBytes("UTF-8"));
            os.flush();
            os.close();

            InputStream is = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
            StringBuilder buff = new StringBuilder();

            while ( ( line = in.readLine() ) != null )
            {
                buff.append(line).append("\n");
            }
            data = buff.toString().trim();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
