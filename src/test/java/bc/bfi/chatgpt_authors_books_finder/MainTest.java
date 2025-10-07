package bc.bfi.chatgpt_authors_books_finder;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

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

    @Test
    public void handleAuthorResultStoresEmptySearches() {
        // Initialization.
        final String author = "Unknown Writer";
        final JsonObject data = Json.createObjectBuilder()
                .add("results", Json.createArrayBuilder().build())
                .add("success", false)
                .add("total_results", 0)
                .add("processing_time_ms", 0)
                .add("ai_analysis_used", false)
                .add("metadata", Json.createObjectBuilder()
                        .add("search_engine", "bing")
                        .add("filters_applied", Json.createArrayBuilder())
                        .add("timestamp", "2024-01-01T00:00:00Z")
                        .build())
                .build();
        final Base base = Mockito.mock(Base.class);
        final ArgumentCaptor<AuthorRecord> captor = ArgumentCaptor.forClass(AuthorRecord.class);

        // Execution.
        Main.handleAuthorResult(author, data, base);

        // Assertion.
        Mockito.verify(base).add(captor.capture());
        final AuthorRecord record = captor.getValue();
        assertThat(record.getAuthor(), is(author));
        assertThat(record.getPosition(), is("0"));
        assertThat(record.getTitle(), is(""));
    }

    @Test
    public void processContactPageDelegatesToDetector() {
        // Initialization.
        final Website website = new Website("https://example.com");
        final ContactFormDetector detector = Mockito.mock(ContactFormDetector.class);
        final String contactUrl = "https://example.com/contact";
        final String html = "<html></html>";

        // Execution.
        Main.processContactPage(website, contactUrl, html, detector);

        // Assertion.
        Mockito.verify(detector).updateWebsiteContactInfo(website, contactUrl, html);
    }
}
