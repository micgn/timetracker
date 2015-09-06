/* 
 * Copyright 2015 Michael Gnatz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mg.ttjs.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RestClient {

    public static String post(String path, String data)
            throws MalformedURLException, IOException {

        URL url = new URL(ServiceCommon.getBaseUrl() + path);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/xml");
            conn.setUseCaches(false);

            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            os.close();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("HTTP error code : "
                        + conn.getResponseCode());
            }

            String out = read(conn.getInputStream());
            return out;

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String get(String path)
            throws MalformedURLException, IOException {

        URL url = new URL(ServiceCommon.getBaseUrl() + path);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/xml");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("HTTP error code : "
                        + conn.getResponseCode());
            }

            String out = read(conn.getInputStream());
            return out;

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String read(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String output = "";
        String line;
        while ((line = br.readLine()) != null) {
            output += line + "\n";
        }
        return output;
    }
}
