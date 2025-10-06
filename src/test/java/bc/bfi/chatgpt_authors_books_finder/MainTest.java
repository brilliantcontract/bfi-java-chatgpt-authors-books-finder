package bc.bfi.chatgpt_authors_books_finder;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void addAuthorIfValidTrimsAndAppendsValue() {
        // Initialization.
        final List<String> authors = new ArrayList<String>();
        final String value = "  Jane Austen  ";

        // Execution.
        Main.addAuthorIfValid(authors, value);

        // Assertion.
        assertThat(authors.size(), is(1));
        assertThat(authors.get(0), is("Jane Austen"));
    }

    @Test
    public void addAuthorIfValidSkipsBlankValues() {
        // Initialization.
        final List<String> authors = new ArrayList<String>();
        final String value = "   ";

        // Execution.
        Main.addAuthorIfValid(authors, value);

        // Assertion.
        assertThat(authors.size(), is(0));
    }
}
