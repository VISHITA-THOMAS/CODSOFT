package com.currency;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class CurrencyServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/convert", new ConvertHandler());
        server.createContext("/api/rates", new RatesHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Currency Converter running at http://localhost:" + port);
    }

    // Serves static HTML/CSS/JS files
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";

            InputStream is = getClass().getResourceAsStream("/web" + path);
            if (is == null) {
                String msg = "404 Not Found";
                exchange.sendResponseHeaders(404, msg.length());
                exchange.getResponseBody().write(msg.getBytes());
                exchange.getResponseBody().close();
                return;
            }

            byte[] bytes = is.readAllBytes();
            String contentType = path.endsWith(".css") ? "text/css" :
                                 path.endsWith(".js")  ? "application/javascript" : "text/html";

            exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        }
    }

    // /api/rates?base=USD  — fetches from open.er-api.com (free, no key needed)
    static class RatesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String base = "USD";
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] kv = param.split("=");
                    if (kv.length == 2 && kv[0].equals("base")) base = kv[1].toUpperCase();
                }
            }

            String json;
            try {
                URL url = new URL("https://open.er-api.com/v6/latest/" + base);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                json = new String(conn.getInputStream().readAllBytes());
            } catch (Exception e) {
                json = "{\"error\":\"Failed to fetch rates: " + e.getMessage() + "\"}";
            }

            byte[] bytes = json.getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        }
    }

    // /api/convert?from=USD&to=INR&amount=100
    static class ConvertHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
            String from   = params.getOrDefault("from", "USD").toUpperCase();
            String to     = params.getOrDefault("to", "INR").toUpperCase();
            String amtStr = params.getOrDefault("amount", "1");

            String json;
            try {
                double amount = Double.parseDouble(amtStr);
                URL url = new URL("https://open.er-api.com/v6/latest/" + from);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                String raw = new String(conn.getInputStream().readAllBytes());

                // Simple extraction without external JSON lib
                double rate = extractRate(raw, to);
                double result = amount * rate;

                json = String.format(
                    "{\"from\":\"%s\",\"to\":\"%s\",\"amount\":%.2f,\"rate\":%.6f,\"result\":%.2f}",
                    from, to, amount, rate, result
                );
            } catch (Exception e) {
                json = "{\"error\":\"" + e.getMessage() + "\"}";
            }

            byte[] bytes = json.getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        }

        private double extractRate(String json, String currency) {
            // Looks for "USD":1.234 pattern inside "rates"
            String marker = "\"" + currency + "\":";
            int idx = json.indexOf(marker);
            if (idx < 0) throw new RuntimeException("Currency " + currency + " not found");
            int start = idx + marker.length();
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.')) end++;
            return Double.parseDouble(json.substring(start, end));
        }

        private Map<String, String> parseQuery(String query) {
            Map<String, String> map = new HashMap<>();
            if (query == null) return map;
            for (String p : query.split("&")) {
                String[] kv = p.split("=");
                if (kv.length == 2) map.put(kv[0], kv[1]);
            }
            return map;
        }
    }
}
