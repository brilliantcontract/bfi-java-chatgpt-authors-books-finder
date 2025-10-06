package bc.bfi.chatgpt_authors_books_finder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Objects;

public class Base {

    private static final Logger LOGGER = Logger.getLogger(Base.class.getName());
    private static final String UNIQUE_CONSTRAINT_SQL_STATE = "23505";

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
            connection = DriverManager.getConnection(
                    Config.DB_URL,
                    Config.DB_USERNAME,
                    Config.DB_PASSWORD);
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

            final String sql = "INSERT INTO " + Config.DB_TABLE
                    + "(position, author, title, url, snippet, is_exact_match, ai_verified, success, total_results, processing_time_ms, ai_used, search_engine, filters_applied, timestamp, domain_country, domain)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                stmt.setString(16, record.getDomain());

                stmt.executeUpdate();
            } catch (SQLException ex) {
                if (UNIQUE_CONSTRAINT_SQL_STATE.equals(ex.getSQLState())) {
                    LOGGER.log(Level.WARNING, "Skip duplicate URL in database: " + record.getUrl(), ex);
                } else {
                    throw ex;
                }
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

            String sql = "SELECT 1 FROM " + Config.DB_TABLE + " WHERE url = ? LIMIT 1";
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
