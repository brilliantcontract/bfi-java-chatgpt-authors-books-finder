package bc.bfi.chatgpt_authors_books_finder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class Main {

    private static final Map<String, String> COUNTRY_MAP = new HashMap<String, String>();

    static {
        COUNTRY_MAP.put("us", "United States");
        COUNTRY_MAP.put("uk", "United Kingdom");
        COUNTRY_MAP.put("gb", "United Kingdom");
        COUNTRY_MAP.put("ca", "Canada");
        COUNTRY_MAP.put("au", "Australia");
    }

    public static void main(final String[] args) {
        final List<String> authors = readAuthorsFromFile("authors-to-check.txt");
        if (authors.isEmpty()) {
            System.out.println("No authors to process.");
            return;
        }

        final String baseUrlEnv = System.getenv("SERVICE_BASE_URL");
        final String baseUrl;
        if (baseUrlEnv != null && baseUrlEnv.length() > 0) {
            baseUrl = baseUrlEnv;
        } else {
            baseUrl = "http://localhost:3000";
        }

        final Base base = new Base();
        try {
            for (int i = 0; i < authors.size(); i++) {
                final String author = authors.get(i);
                System.out.println("Processing author: " + author);
                try {
                    final JsonObject data = searchAuthor(baseUrl, author);
                    handleAuthorResult(author, data, base);
                } catch (IOException ex) {
                    System.out.println("Failed to search: " + ex.getMessage());
                }
                System.out.println();
            }
        } finally {
            base.close();
        }
    }

    private static List<String> readAuthorsFromFile(final String fileName) {
        final List<String> list = new ArrayList<String>();
        final File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File not found: " + fileName);
            return list;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                final String trimmed = line.trim();
                if (trimmed.length() > 0) {
                    list.add(trimmed);
                }
            }
        } catch (IOException ex) {
            System.out.println("Read failed: " + ex.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                    // ignore
                }
            }
        }
        return list;
    }

    private static JsonObject searchAuthor(final String baseUrl, final String author) throws IOException {
        Objects.requireNonNull(baseUrl, "baseUrl");
        Objects.requireNonNull(author, "author");

        final CloseableHttpClient client = HttpClients.createDefault();
        try {
            final HttpPost post = new HttpPost(baseUrl + "/search");
            final String json = "{\"author\":\"" + author.replace("\"", "\\\"") + "\"}";
            final StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            final CloseableHttpResponse response = client.execute(post);
            try {
                final int status = response.getCode();
                assert status == 200 : "Unexpected HTTP status: " + status;

                final String body;
                try {
                    body = EntityUtils.toString(response.getEntity(), "UTF-8");
                } catch (org.apache.hc.core5.http.ParseException ex) {
                    throw new IOException("Failed to parse response entity", ex);
                }

                final JsonReader reader = Json.createReader(new StringReader(body));
                try {
                    return reader.readObject();
                } finally {
                    reader.close();
                }
            } finally {
                response.close();
            }
        } finally {
            client.close();
        }
    }

    private static void handleAuthorResult(final String author, final JsonObject data, final Base base) {
        Objects.requireNonNull(author, "author");
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(base, "base");

        final JsonArray results;
        if (data.containsKey("results")) {
            results = data.getJsonArray("results");
        } else {
            results = Json.createArrayBuilder().build();
        }

        if (results.size() == 0) {
            System.out.println("No results for " + author);
            return;
        }

        final boolean success = data.containsKey("success") && data.getBoolean("success");
        final int totalResults = data.containsKey("total_results") ? data.getInt("total_results") : 0;
        final long processingTime;
        if (data.containsKey("processing_time_ms")) {
            processingTime = (long) data.getJsonNumber("processing_time_ms").longValue();
        } else {
            processingTime = 0L;
        }
        final boolean aiUsed = data.containsKey("ai_analysis_used") && data.getBoolean("ai_analysis_used");
        final JsonObject metadata = data.containsKey("metadata") ? data.getJsonObject("metadata") : null;
        final String searchEngine;
        final String filtersApplied;
        final String timestamp;
        if (metadata != null) {
            if (metadata.containsKey("search_engine")) {
                searchEngine = metadata.getString("search_engine");
            } else {
                searchEngine = "";
            }
            if (metadata.containsKey("filters_applied")) {
                final JsonArray arr = metadata.getJsonArray("filters_applied");
                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i < arr.size(); i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(arr.getString(i));
                }
                filtersApplied = sb.toString();
            } else {
                filtersApplied = "";
            }
            if (metadata.containsKey("timestamp")) {
                timestamp = metadata.getString("timestamp");
            } else {
                timestamp = "";
            }
        } else {
            searchEngine = "";
            filtersApplied = "";
            timestamp = "";
        }

        for (int i = 0; i < results.size(); i++) {
            final JsonObject item = results.getJsonObject(i);
            final String title = item.containsKey("title") ? item.getString("title") : "";
            final String url;
            if (item.containsKey("url")) {
                url = item.getString("url");
            } else if (item.containsKey("link")) {
                url = item.getString("link");
            } else {
                url = "";
            }
            final String domain = getDomain(url);
            final String domainCountry = getDomainCountry(url);
            final String snippet = item.containsKey("snippet") ? item.getString("snippet") : "";
            final String isExact;
            if (item.containsKey("is_exact_match")) {
                isExact = String.valueOf(item.getBoolean("is_exact_match"));
            } else {
                isExact = "";
            }
            final String aiVerified;
            if (item.containsKey("ai_verified")) {
                aiVerified = String.valueOf(item.getBoolean("ai_verified"));
            } else {
                aiVerified = "";
            }

            final int position = i + 1;

            System.out.println("Position: " + position);
            System.out.println("Author: " + author);
            System.out.println("Title: " + title);
            System.out.println("URL: " + url);
            System.out.println("Domain: " + domain);
            System.out.println("Domain Country: " + domainCountry);
            System.out.println("Snippet: " + snippet);
            System.out.println("Is Exact Match: " + isExact);
            System.out.println("AI Verified: " + aiVerified);
            System.out.println("Success: " + success);
            System.out.println("Total Results: " + totalResults);
            System.out.println("Processing Time (ms): " + processingTime);
            System.out.println("AI Used: " + aiUsed);
            System.out.println("Search Engine: " + searchEngine);
            System.out.println("Filters Applied: " + filtersApplied);
            System.out.println("Timestamp: " + timestamp);
            System.out.println("---");

            final AuthorRecord record = new AuthorRecord(
                    String.valueOf(position),
                    author,
                    title,
                    url,
                    domain,
                    snippet,
                    isExact,
                    aiVerified,
                    String.valueOf(success),
                    String.valueOf(totalResults),
                    String.valueOf(processingTime),
                    String.valueOf(aiUsed),
                    searchEngine,
                    filtersApplied,
                    timestamp,
                    domainCountry);
            base.add(record);
        }
    }

    static String getDomain(final String url) {
        if (url == null || url.length() == 0) {
            return "";
        }
        final String lower = url.toLowerCase();
        int start = 0;
        final int schemeIdx = lower.indexOf("://");
        if (schemeIdx >= 0) {
            start = schemeIdx + 3;
        }
        int end = lower.indexOf('/', start);
        if (end < 0) {
            end = lower.length();
        }
        String host = lower.substring(start, end);
        final int colonIdx = host.indexOf(':');
        if (colonIdx >= 0) {
            host = host.substring(0, colonIdx);
        }
        assert host.length() > 0 : "Host part is empty for URL: " + url;
        return host;
    }

    static String getDomainCountry(final String url) {
        final String host = getDomain(url);
        if (host.length() == 0) {
            return "";
        }
        final int dotIdx = host.lastIndexOf('.');
        if (dotIdx < 0 || dotIdx >= host.length() - 1) {
            return "";
        }
        final String tld = host.substring(dotIdx + 1);
        final String name = COUNTRY_MAP.get(tld);
        if (name != null) {
            return name;
        }
        return "";
    }
}

