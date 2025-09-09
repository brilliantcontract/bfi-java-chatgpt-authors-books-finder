package bc.bfi.chatgpt_authors_books_finder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;

public class BaseTest {

    @Test
    public void addSetsDomainParameter() throws SQLException {
        // Initialization.
        final AuthorRecord record = new AuthorRecord(
                "1",
                "Author",
                "Title",
                "http://example.com",
                "example.com",
                "Snippet",
                "true",
                "true",
                "1",
                "10",
                "false",
                "engine",
                "filter",
                "time",
                "US");
        final Connection connection = mock(Connection.class);
        final PreparedStatement stmt = mock(PreparedStatement.class);
        when(connection.isClosed()).thenReturn(false);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);

        // Execution.
        final Base base = new Base(connection);
        base.add(record);

        // Assertion.
        verify(stmt).setString(5, record.getDomain());
        assertThat(record.getDomain(), is("example.com"));
    }
}
