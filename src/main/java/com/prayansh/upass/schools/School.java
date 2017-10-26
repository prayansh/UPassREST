package com.prayansh.upass.schools;

import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.prayansh.upass.exceptions.SchoolAuthenticationFailedException;

/**
 * Created by Prayansh on 2017-10-21.
 */
public interface School {
    HtmlDocument login(HtmlDocument authPage, String username, String password) throws SchoolAuthenticationFailedException;

    String getID();
}
