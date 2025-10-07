package bc.bfi.chatgpt_authors_books_finder;

import java.util.Locale;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ContactFormDetector {

    public boolean hasContactForm(final String html) {
        if (html == null) {
            return false;
        }
        final String trimmed = html.trim();
        if (trimmed.length() == 0) {
            return false;
        }
        final Document document = Jsoup.parse(trimmed);
        final Elements forms = document.getElementsByTag("form");
        for (int i = 0; i < forms.size(); i++) {
            final Element form = forms.get(i);
            if (containsContactKeyword(form.id())) {
                return true;
            }
            if (containsContactKeyword(form.attr("name"))) {
                return true;
            }
            if (containsContactKeyword(form.attr("class"))) {
                return true;
            }
            if (containsContactKeyword(form.attr("action"))) {
                return true;
            }
        }
        return false;
    }

    public void updateWebsiteContactInfo(
            final Website website,
            final String contactPageUrl,
            final String contactPageHtml) {
        Objects.requireNonNull(website, "website");

        if (contactPageUrl == null) {
            return;
        }
        final String trimmedUrl = contactPageUrl.trim();
        if (trimmedUrl.length() == 0) {
            return;
        }

        website.setContactPageUrl(trimmedUrl);
        final boolean found = hasContactForm(contactPageHtml);
        website.setContactFormFound(found);
    }

    private boolean containsContactKeyword(final String value) {
        if (value == null) {
            return false;
        }
        final String lower = value.toLowerCase(Locale.ENGLISH);
        return lower.contains("contact");
    }
}
