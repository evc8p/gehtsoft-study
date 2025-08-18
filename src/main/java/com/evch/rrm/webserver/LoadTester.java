package com.evch.rrm.webserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoadTester { // chatGPT создал

    private static final int TOTAL_REQUESTS = 1000;
    private static final int THREADS = 50;

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] servers = {"http://localhost:8080/api/time", "http://localhost:8081/api/time"};

        for (String server : servers) {
            System.out.println("\n=== Load Test for: " + server + " ===");

            ExecutorService executor = Executors.newFixedThreadPool(THREADS);
            List<Future<Long>> futures = new ArrayList<>();

            Instant start = Instant.now();

            for (int i = 0; i < TOTAL_REQUESTS; i++) {
                futures.add(executor.submit(() -> sendRequest(server)));
            }

            List<Long> responseTimes = new ArrayList<>();
            for (Future<Long> future : futures) {
                responseTimes.add(future.get());
            }

            Instant end = Instant.now();
            executor.shutdown();

            double avgTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
            long minTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);
            long maxTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
            double throughput = TOTAL_REQUESTS / (double) Duration.between(start, end).toMillis() * 1000;

            System.out.println("Total requests: " + TOTAL_REQUESTS);
            System.out.println("Concurrent threads: " + THREADS);
            System.out.println("Total test duration: " + Duration.between(start, end).toMillis() + " ms");
            System.out.println("Avg response time: " + avgTime + " ms");
            System.out.println("Min response time: " + minTime + " ms");
            System.out.println("Max response time: " + maxTime + " ms");
            System.out.println("Throughput: " + String.format("%.2f", throughput) + " req/sec");
        }
    }

    private static long sendRequest(String urlString) {
        long start = System.currentTimeMillis();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                while (in.readLine() != null) ;
            }
            if (responseCode != 200) {
                System.out.println("Warning: received response code " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Request failed: " + e.getMessage());
        }
        return System.currentTimeMillis() - start;
    }
}
