package bc.bfi.chatgpt_authors_books_finder;

public final class Config {

    public static final String DB_HOST = "3.17.216.88";
    public static final int DB_PORT = 3306;
    public static final String DB_NAME = "chatgpt_authors_books_finder";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "RootSecret1!";
    public static final String DB_TABLE = "authors";
    public static final String DB_URL =
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    public static final String SERVICE_BASE_URL_ENV = "SERVICE_BASE_URL";
    public static final String BASE_URL = "http://localhost:8080";

    private Config() {
    }
}
