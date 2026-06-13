package com.currencyconverter.services;

import com.currencyconverter.models.ConversionResult;
import com.currencyconverter.models.Currency;
import com.currencyconverter.utils.FormatUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class ExchangeRateService {

    private static final String BASE = "https://api.frankfurter.app";
    private static final int    TIMEOUT = 10_000;

    public ConversionResult convert(double amount, Currency from, Currency to) throws Exception {
        String url  = String.format("%s/latest?amount=%.6f&from=%s&to=%s",
                BASE, amount, from.getCode(), to.getCode());
        String json = get(url);
        double converted = parse(json, to.getCode());
        double rate      = converted / amount;
        return new ConversionResult(amount, converted, from, to, rate, FormatUtils.nowTimestamp());
    }

    private String get(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        try {
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT);
            conn.setReadTimeout(TIMEOUT);
            conn.setRequestProperty("Accept", "application/json");
            int status = conn.getResponseCode();
            if (status == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    return br.lines().collect(Collectors.joining());
                }
            }
            throw new Exception("Server returned HTTP " + status + ". Check your internet connection.");
        } finally { conn.disconnect(); }
    }

    private double parse(String json, String code) throws Exception {
        String key = "\"" + code + "\":";
        int idx = json.indexOf(key);
        if (idx == -1) throw new Exception("Currency '" + code + "' not supported by Frankfurter API.");
        int s = idx + key.length();
        while (s < json.length() && Character.isWhitespace(json.charAt(s))) s++;
        int e = s;
        while (e < json.length() && json.charAt(e) != ',' && json.charAt(e) != '}'
               && !Character.isWhitespace(json.charAt(e))) e++;
        try { return Double.parseDouble(json.substring(s, e).trim()); }
        catch (NumberFormatException ex) { throw new Exception("Failed to parse exchange rate."); }
    }
}
