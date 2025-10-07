package bc.bfi.chatgpt_authors_books_finder;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ContactFormDetectorTest {

    @Test
    public void updateWebsiteContactInfoKeepsUrlWhenFormMissing() {
        // Initialization.
        final ContactFormDetector detector = new ContactFormDetector();
        final Website website = new Website("https://example.com");
        final String contactUrl = "https://example.com/contact";
        final String html = "<html><body><h1>Contact us</h1></body></html>";

        // Execution.
        detector.updateWebsiteContactInfo(website, contactUrl, html);

        // Assertion.
        assertThat(website.getContactPageUrl(), is(contactUrl));
        assertThat(website.isContactFormFound(), is(false));
    }

    @Test
    public void updateWebsiteContactInfoMarksFormWhenFound() {
        // Initialization.
        final ContactFormDetector detector = new ContactFormDetector();
        final Website website = new Website("https://example.com");
        final String contactUrl = "https://example.com/contact";
        final String html = "<html><body><form id=\"contact-form\"></form></body></html>";

        // Execution.
        detector.updateWebsiteContactInfo(website, contactUrl, html);

        // Assertion.
        assertThat(website.getContactPageUrl(), is(contactUrl));
        assertThat(website.isContactFormFound(), is(true));
    }
}
