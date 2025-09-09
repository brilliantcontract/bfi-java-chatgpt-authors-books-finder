package bc.bfi.chatgpt_authors_books_finder;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MainTest {

    @Test
    public void getDomainExtractsHost() {
        // Initialization.
        final String url = "https://example.com/path";
        final String expected = "example.com";

        // Execution.
        final String domain = Main.getDomain(url);

        // Assertion.
        assertThat(domain, is(expected));
    }
}
