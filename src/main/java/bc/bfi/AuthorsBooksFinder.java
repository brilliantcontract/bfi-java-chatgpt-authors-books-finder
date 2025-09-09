package bc.bfi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorsBooksFinder {
    private static final String OPEN_LIBRARY_URL = "https://openlibrary.org/search.json?author=";

    public static void main(String[] args) throws IOException {
        List<String> authors = Files.readAllLines(Paths.get("authors-to-check.txt"), StandardCharsets.UTF_8)
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (authors.isEmpty()) {
            System.out.println("No authors found in authors-to-check.txt");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        for (String author : authors) {
            System.out.println("Author: " + author);
            try {
                String query = OPEN_LIBRARY_URL + URLEncoder.encode(author, "UTF-8");
                HttpURLConnection connection = (HttpURLConnection) new URL(query).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int status = connection.getResponseCode();
                InputStream responseStream = (status >= 200 && status < 300)
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                String response = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                connection.disconnect();

                JsonNode root = mapper.readTree(response);
                JsonNode docs = root.path("docs");
                if (!docs.isArray() || docs.size() == 0) {
                    System.out.println("  No books found.");
                } else {
                    int count = Math.min(3, docs.size());
                    for (int i = 0; i < count; i++) {
                        JsonNode doc = docs.get(i);
                        String title = doc.path("title").asText("");
                        System.out.println("  - " + title);
                    }
                }
            } catch (Exception e) {
                System.out.println("  Error retrieving books: " + e.getMessage());
            }
        }
    }
}
