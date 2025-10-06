package bc.bfi.chatgpt_authors_books_finder;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConfigTest {

    @Test
    public void dbUrlIsConstructedProperly() {
        // Initialization.
        final String expected = "jdbc:postgresql://3.140.167.34:5432/scrapers";

        // Execution.
        final String url = Config.DB_URL;

        // Assertion.
        assertThat(url, is(expected));
    }
}
