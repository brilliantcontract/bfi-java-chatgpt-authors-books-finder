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

            final String sql
                    = "INSERT INTO " + Config.DB_TABLE
                    + "(position, author, title, url, snippet, "
                    + " is_exact_match, ai_verified, success, "
                    + " total_results, processing_time_ms, ai_used, "
                    + " search_engine, filters_applied, timestamp, "
                    + " domain_country, domain) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                // 1) position (int)
                stmt.setInt(1, parseIntOrZero(record.getPosition()));

                // 2-5) text
                stmt.setString(2, nullIfBlank(record.getAuthor()));
                stmt.setString(3, nullIfBlank(record.getTitle()));
                stmt.setString(4, nullIfBlank(record.getUrl()));
                stmt.setString(5, nullIfBlank(record.getSnippet()));

                // 6-8) booleans
                setBooleanOrNull(stmt, 6, record.getIsExactMatch());
                setBooleanOrNull(stmt, 7, record.getAiVerified());
                setBooleanOrNull(stmt, 8, record.getSuccess());

                // 9-10) numbers
                setLongOrNull(stmt, 9, record.getTotalResults());
                setLongOrNull(stmt, 10, record.getProcessingTimeMs());

                // 11) ai_used (looks like a boolean)
                setBooleanOrNull(stmt, 11, record.getAiUsed());

                // 12-13) text
                stmt.setString(12, nullIfBlank(record.getSearchEngine()));
                stmt.setString(13, nullIfBlank(record.getFiltersApplied()));

                // 14) timestamp
                java.sql.Timestamp ts = parseTimestampOrNow(record.getTimestamp());
                stmt.setTimestamp(14, ts); // assumes column type TIMESTAMP/TIMESTAMPTZ

                // 15-16) text
                stmt.setString(15, nullIfBlank(record.getDomainCountry()));
                stmt.setString(16, nullIfBlank(record.getDomain()));

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

    private static String nullIfBlank(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }

    private static int parseIntOrZero(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private static void setLongOrNull(PreparedStatement ps, int idx, String s) throws SQLException {
        if (s == null || s.isEmpty()) {
            ps.setNull(idx, java.sql.Types.BIGINT);
            return;
        }
        try {
            ps.setLong(idx, Long.parseLong(s));
        } catch (Exception e) {
            ps.setNull(idx, java.sql.Types.BIGINT);
        }
    }

    private static void setBooleanOrNull(PreparedStatement ps, int idx, String s) throws SQLException {
        if (s == null || s.isEmpty()) {
            ps.setNull(idx, java.sql.Types.BOOLEAN);
            return;
        }
        Boolean val = parseFlexibleBoolean(s);
        if (val == null) {
            ps.setNull(idx, java.sql.Types.BOOLEAN);
        } else {
            ps.setBoolean(idx, val);
        }
    }

    private static Boolean parseFlexibleBoolean(String s) {
        String v = s.trim().toLowerCase();
        switch (v) {
            case "true":
            case "t":
            case "1":
            case "yes":
            case "y":
                return true;
            case "false":
            case "f":
            case "0":
            case "no":
            case "n":
                return false;
            default:
                return null;
        }
    }

    private static java.sql.Timestamp parseTimestampOrNow(String s) {
        if (s == null || s.isEmpty()) {
            return new java.sql.Timestamp(System.currentTimeMillis());
        }
        try {
            // Prefer ISO-8601 like "2025-09-26T15:39:00Z" or with offset
            java.time.Instant inst = java.time.Instant.parse(s);
            return java.sql.Timestamp.from(inst);
        } catch (Exception ignore) {
            try {
                // Fallback: "2025-09-26 15:39:00"
                java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(
                        s.replace('T', ' ').split("Z")[0],
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss]")
                );
                return java.sql.Timestamp.valueOf(ldt);
            } catch (Exception e2) {
                return new java.sql.Timestamp(System.currentTimeMillis());
            }
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
