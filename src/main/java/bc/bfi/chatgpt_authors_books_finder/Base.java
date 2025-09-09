package bc.bfi.chatgpt_authors_books_finder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    Base(Connection connection) {
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

    void add(ChannelAbout channel) {
        if (channel == null) {
            return;
        }

        try {
            connect();

            String sql = "INSERT INTO " + DB_TABLE
                    + "(url, description, videos, views, join_date, link_to_instagram, link_to_facebook, link_to_twitter, link_to_tiktok, other_links, verification, thumbnail)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, channel.getUrl());
                stmt.setString(2, channel.getDescription());
                stmt.setString(3, channel.getVideosAsNumber());
                stmt.setString(4, channel.getViewsAsNumber());
                stmt.setString(5, channel.getJoinDate());
                stmt.setString(6, channel.getLinkToInstagram());
                stmt.setString(7, channel.getLinkToFacebook());
                stmt.setString(8, channel.getLinkToTwitter());
                stmt.setString(9, channel.getLinkToTiktok());
                stmt.setString(10, channel.getOtherLinks());
                stmt.setString(11, channel.getVerification());
                stmt.setString(12, channel.getThumbnail());

                stmt.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException ex) {
                LOGGER.log(Level.WARNING, "Skip duplicate URL in database: " + channel.getUrl(), ex);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    boolean exists(String url) {
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
