package bc.bfi.chatgpt_authors_books_finder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Objects;

public class Base {

    static final String DB_HOST = "3.17.216.88";
    static final int DB_PORT = 3306;
    static final String DB_NAME = "chatgpt_authors_books_finder";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "RootSecret1!";
    static final String DB_TABLE = "authors";

    private static final String DB_URL
            = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    private static final Logger LOGGER = Logger.getLogger(Base.class.getName());

    private Connection connection;

    public Base() {
    }

    /**
     * Constructor for tests which allows to inject connection instance.
     */
    Base(final Connection connection) {
        this.connection = connection;
    }

    void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
    }

    void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    void add(final AuthorRecord record) {
        Objects.requireNonNull(record, "record");

        try {
            connect();

            final String sql = "INSERT INTO " + DB_TABLE
                    + "(position, author, title, url, snippet, is_exact_match, ai_verified, success, total_results, processing_time_ms, ai_used, search_engine, filters_applied, timestamp, domain_country)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, record.getPosition());
                stmt.setString(2, record.getAuthor());
                stmt.setString(3, record.getTitle());
                stmt.setString(4, record.getUrl());
                stmt.setString(5, record.getSnippet());
                stmt.setString(6, record.getIsExactMatch());
                stmt.setString(7, record.getAiVerified());
                stmt.setString(8, record.getSuccess());
                stmt.setString(9, record.getTotalResults());
                stmt.setString(10, record.getProcessingTimeMs());
                stmt.setString(11, record.getAiUsed());
                stmt.setString(12, record.getSearchEngine());
                stmt.setString(13, record.getFiltersApplied());
                stmt.setString(14, record.getTimestamp());
                stmt.setString(15, record.getDomainCountry());

                stmt.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException ex) {
                LOGGER.log(Level.WARNING, "Skip duplicate URL in database: " + record.getUrl(), ex);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    boolean exists(final String url) {
        Objects.requireNonNull(url, "url");

        boolean found = false;

        try {
            connect();

            String sql = "SELECT 1 FROM " + DB_TABLE + " WHERE url = ? LIMIT 1";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, url);

                java.sql.ResultSet rs = stmt.executeQuery();
                found = rs.next();
                rs.close();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return found;
    }

}
