package com.example.demo.mycourse.service;

import com.example.demo.mycourse.config.CaptchaSettings;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class CaptchaValidator {

    private static final String GOOGLE_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Autowired
    private CaptchaSettings recaptchaConfig;

    public boolean verifyCaptcha(String responseToken) {
        try {
            // Creo la connessione all'endpoint di Google
            URL url = new URL(GOOGLE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // Necessario per inviare dati nel body

            // Parametri da inviare nel body
            String params = "secret=" + recaptchaConfig.getSecret()
                    + "&response=" + responseToken;

            // Scrivo i dati nel body della richiesta
            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes());
                os.flush();
            }

            // Leggo la risposta di Google
            InputStreamReader reader = new InputStreamReader(conn.getInputStream());
            StringBuilder responseBuilder = new StringBuilder();
            int character;
            while ((character = reader.read()) != -1) {
                responseBuilder.append((char) character);
            }

            // Analizzo la risposta JSON
            JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
            return jsonResponse.getBoolean("success");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public String getSecretKey() {
        return recaptchaConfig.getSecret();
    }
    public String getSiteKey() {
        return recaptchaConfig.getSite();
    }
}
