package com.prayansh.upass.schools;

import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.Password;
import com.gistlabs.mechanize.document.html.form.Text;
import com.prayansh.upass.exceptions.SchoolAuthenticationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Prayansh on 2017-10-21.
 */
public class UniversityOfBritishColumbia implements School {
    public final String ID = "ubc";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public HtmlDocument login(HtmlDocument authPage, String username, String password) throws SchoolAuthenticationFailedException {
        Form authForm = authPage.form("loginForm");

        Text usernameField = (Text) authForm.get("j_username");
        Password passwordField = (Password) authForm.get("password");

        usernameField.setValue(username);
        passwordField.setValue(password);
        HtmlDocument ubcRedirect = authForm.submit();

        HtmlDocument submittedPage;
        try {
            HtmlDocument translinkRedirect = ubcRedirect.forms().get(0).submit();
            logger.debug("translinkRedirect: " + translinkRedirect.getUri());
            submittedPage = translinkRedirect.forms().get(0).submit();
            logger.debug("submittedPage: " + submittedPage.getUri());
        } catch (Exception e) {
            throw new SchoolAuthenticationFailedException(e);
        }

        if (submittedPage.getUri().contains("https://upassbc.translink.ca")) {
            return submittedPage;
        } else {
            throw new SchoolAuthenticationFailedException(new Exception("Invalid submitted page URI"));
        }
    }

    @Override
    public String getID() {
        return ID;
    }
}
