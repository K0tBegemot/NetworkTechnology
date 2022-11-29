package Core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HTTPclient {
    private int connectTimeout;
    private int readTimeout;

    HTTPclient(int readTimeout, int connectTimeout) {
        this.readTimeout = readTimeout;
        this.connectTimeout = connectTimeout;
    }
    String request(String urlString) throws IOException {
        final URL url = new URL(urlString);
        final HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(connectTimeout);
        con.setReadTimeout(readTimeout);

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuilder contentBuilder = new StringBuilder();

        while ((inputLine = reader.readLine()) != null) {
            contentBuilder.append(inputLine);
        }
        reader.close();
        // return JSON data
        return contentBuilder.toString();
    }
}
