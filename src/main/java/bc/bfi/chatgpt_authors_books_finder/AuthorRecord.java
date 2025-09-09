package bc.bfi.chatgpt_authors_books_finder;

import java.util.Objects;

public class AuthorRecord {

    private final String position;
    private final String author;
    private final String title;
    private final String url;
    private final String domain;
    private final String snippet;
    private final String isExactMatch;
    private final String aiVerified;
    private final String success;
    private final String totalResults;
    private final String processingTimeMs;
    private final String aiUsed;
    private final String searchEngine;
    private final String filtersApplied;
    private final String timestamp;
    private final String domainCountry;

    public AuthorRecord(
            final String position,
            final String author,
            final String title,
            final String url,
            final String domain,
            final String snippet,
            final String isExactMatch,
            final String aiVerified,
            final String success,
            final String totalResults,
            final String processingTimeMs,
            final String aiUsed,
            final String searchEngine,
            final String filtersApplied,
            final String timestamp,
            final String domainCountry) {
        this.position = Objects.requireNonNull(position, "position");
        this.author = Objects.requireNonNull(author, "author");
        this.title = Objects.requireNonNull(title, "title");
        this.url = Objects.requireNonNull(url, "url");
        this.domain = Objects.requireNonNull(domain, "domain");
        this.snippet = Objects.requireNonNull(snippet, "snippet");
        this.isExactMatch = Objects.requireNonNull(isExactMatch, "isExactMatch");
        this.aiVerified = Objects.requireNonNull(aiVerified, "aiVerified");
        this.success = Objects.requireNonNull(success, "success");
        this.totalResults = Objects.requireNonNull(totalResults, "totalResults");
        this.processingTimeMs = Objects.requireNonNull(processingTimeMs, "processingTimeMs");
        this.aiUsed = Objects.requireNonNull(aiUsed, "aiUsed");
        this.searchEngine = Objects.requireNonNull(searchEngine, "searchEngine");
        this.filtersApplied = Objects.requireNonNull(filtersApplied, "filtersApplied");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.domainCountry = Objects.requireNonNull(domainCountry, "domainCountry");
    }

    public String getPosition() {
        return position;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDomain() {
        return domain;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getIsExactMatch() {
        return isExactMatch;
    }

    public String getAiVerified() {
        return aiVerified;
    }

    public String getSuccess() {
        return success;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public String getProcessingTimeMs() {
        return processingTimeMs;
    }

    public String getAiUsed() {
        return aiUsed;
    }

    public String getSearchEngine() {
        return searchEngine;
    }

    public String getFiltersApplied() {
        return filtersApplied;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDomainCountry() {
        return domainCountry;
    }
}
