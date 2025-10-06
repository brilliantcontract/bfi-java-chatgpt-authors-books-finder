package bc.bfi.chatgpt_authors_books_finder;

public final class Config {

    /**
     * Database.
     */
    static final String DB_HOST = "3.140.167.34";
    static final String DB_PORT = "5432";
    static final String DB_USERNAME = "redash";
    static final String DB_PASSWORD = "te83NECug38ueP";
    static final String DB_DATABASE = "scrapers";
    static final String DB_URL = "jdbc:postgresql://"
            + DB_HOST + ":"
            + DB_PORT + "/"
            + DB_DATABASE;
    static final String DB_TABLE = "chatgpt_websites_finder.chatgpt_authors";

    public static final String SERVICE_BASE_URL_ENV = "SERVICE_BASE_URL";
    public static final String BASE_URL = "http://localhost:8080";
    public static final String CHATGPT_API_KEY = "ask_live_7f9d2e1a4b8c6f3e9d2c5a8b7e4f1a9c";

    private Config() {
    }
}
