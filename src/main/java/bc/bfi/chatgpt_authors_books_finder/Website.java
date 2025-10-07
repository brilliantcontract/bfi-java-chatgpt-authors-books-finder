package bc.bfi.chatgpt_authors_books_finder;

import java.util.Objects;

public class Website {

    private final String url;
    private String contactPageUrl;
    private boolean contactFormFound;

    public Website(final String url) {
        this.url = Objects.requireNonNull(url, "url");
        this.contactPageUrl = "";
        this.contactFormFound = false;
    }

    public String getUrl() {
        return url;
    }

    public String getContactPageUrl() {
        return contactPageUrl;
    }

    public void setContactPageUrl(final String contactPageUrl) {
        Objects.requireNonNull(contactPageUrl, "contactPageUrl");
        this.contactPageUrl = contactPageUrl;
    }

    public boolean isContactFormFound() {
        return contactFormFound;
    }

    public void setContactFormFound(final boolean contactFormFound) {
        this.contactFormFound = contactFormFound;
    }
}
