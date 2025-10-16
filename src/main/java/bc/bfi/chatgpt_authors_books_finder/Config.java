package bc.bfi.chatgpt_authors_books_finder;

public final class Config {
    
    /**
     * Tets mode.
     */
    static final Boolean IS_TEST_MODE = true;
    static final String FILE_WITH_TEST_AUTHORS = "authors-to-check.txt";

    /**
     * Database.
     */
    static final String DB_HOST = "3.140.167.34";
    static final String DB_PORT = "5432";
    static final String DB_USERNAME = "redash";
    static final String DB_PASSWORD = "te83NECug38ueP";
    static final String DB_DATABASE = "scrapers";
    static final String DB_TABLE = "chatgpt_websites_finder.chatgpt_authors";
    static final String DB_URL = buildDbUrl();
    static final String DB_USER = DB_USERNAME;
    
    static final String AUTHORS_QUERY = "select author from chatgpt_websites_finder.next_batch_to_scrape_vw";

    public static final String SERVICE_BASE_URL_ENV = "SERVICE_BASE_URL";
    public static final String BASE_URL = "http://localhost:8080";
    public static final String CHATGPT_API_KEY = "ask_live_7f9d2e1a4b8c6f3e9d2c5a8b7e4f1a9c";

    private Config() {
    }

    private static String buildDbUrl() {
        final StringBuilder builder = new StringBuilder();
        builder.append("jdbc:postgresql://");
        builder.append(DB_HOST);
        builder.append(":");
        builder.append(DB_PORT);
        builder.append("/");
        builder.append(DB_DATABASE);
        return builder.toString();
    }
}
