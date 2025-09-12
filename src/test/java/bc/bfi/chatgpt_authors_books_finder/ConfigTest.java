package bc.bfi.chatgpt_authors_books_finder;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConfigTest {

    @Test
    public void dbUrlIsConstructedProperly() {
        // Initialization.
        final String expected = "jdbc:mysql://3.17.216.88:3306/chatgpt_authors_books_finder";

        // Execution.
        final String url = Config.DB_URL;

        // Assertion.
        assertThat(url, is(expected));
    }
}
