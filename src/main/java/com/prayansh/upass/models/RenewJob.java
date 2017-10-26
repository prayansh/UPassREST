package com.prayansh.upass.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.form.*;
import com.gistlabs.mechanize.exceptions.MechanizeException;
import com.gistlabs.mechanize.impl.MechanizeAgent;
import com.prayansh.upass.exceptions.NothingToRenewException;
import com.prayansh.upass.exceptions.SchoolAuthenticationFailedException;
import com.prayansh.upass.exceptions.SchoolNotFoundException;
import com.prayansh.upass.schools.School;
import com.prayansh.upass.schools.UniversityOfBritishColumbia;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by Prayansh on 2017-10-21.
 */
public class RenewJob implements Callable<Status> {
    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @JsonIgnore
    private final String UPASS_SITE_URL = "http://upassbc.translink.ca";
    @JsonIgnore
    private final School school;
    @JsonIgnore
    private final RenewJobPayload payload;

    private Status status;
    private final String jobId;

    public RenewJob(RenewJobPayload payload) {
        this.jobId = UUID.randomUUID().toString();
        this.payload = payload;
        this.school = makeNewSchool(payload.getSchool());
        this.status = Status.RUNNING;
    }

    private static School makeNewSchool(String schoolID) {
        switch (schoolID) {
            case "UBC":
                return new UniversityOfBritishColumbia();
            default:
                return null;
        }
    }

    private Status renew() throws SchoolAuthenticationFailedException, NothingToRenewException, SchoolNotFoundException {
        Status returnStatus;
        logger.info("Starting renew process for jobId=" + jobId);
        HtmlDocument authPage = selectSchool(UPASS_SITE_URL, school.getID());
        logger.info("Selected school");
        HtmlDocument upassPage = authorizeAccount(authPage);
        logger.info("Logged in");
        checkUpass(upassPage);
        boolean requestSuccess = requestUpass(upassPage);
        if (requestSuccess) {
            returnStatus = Status.RENEW_SUCCESSFUL;
        } else {
            returnStatus = Status.RENEW_FAILED;
        }
        return returnStatus;
    }

    private HtmlDocument selectSchool(String siteURL, String schoolId) throws SchoolNotFoundException {
        MechanizeAgent agent = new MechanizeAgent();
        agent.getClient().setRedirectStrategy(new LaxRedirectStrategy());
        HtmlDocument page = agent.get(siteURL);
        Form schoolSelectionForm = page.forms().get(0);
        Select schoolDropdown = (Select) schoolSelectionForm.get("PsiId");
        SubmitButton submit = (SubmitButton) schoolSelectionForm.get("goButton");
        List<Select.Option> schools = schoolDropdown.getOptions();
        Select.Option schoolOption = null;
        for (Select.Option school : schools) {
            if (school.getValue().equals(schoolId)) {
                schoolOption = school;
            }
        }
        if (schoolOption != null) {
            schoolOption.setSelected(true);
        } else {
            throw new SchoolNotFoundException();
        }
        return submit.submit();
    }

    private HtmlDocument authorizeAccount(HtmlDocument authPage) throws SchoolAuthenticationFailedException {
        return this.school.login(authPage, payload.getUsername(), payload.getPassword());
    }

    private boolean checkUpass(HtmlDocument upassPage) throws NothingToRenewException {
        Form requestForm = upassPage.form("form-request");
        Checkbox requestCheckbox = null;
        for (Object element : requestForm) {
            if (element instanceof Checkbox) {
                requestCheckbox = (Checkbox) element;
            }
        }
        if (requestCheckbox != null) {
            logger.info("Checkbox found: renewing UPass");
            return true;
        } else {
            logger.info("No checkbox found: nothing to renew");
            throw new NothingToRenewException();
        }
    }

    private boolean requestUpass(HtmlDocument upassPage) {
        List prevRequestedUpasses = upassPage.findAll(".status");

        Form requestForm = upassPage.form("form-request");
        Checkbox requestCheckbox = requestForm.findCheckbox("input");
        requestCheckbox.check();
        String boxName = requestCheckbox.getName();
        Iterator elementIter = requestForm.iterator();
        while (elementIter.hasNext()) {
            Object element = elementIter.next();
            if (element instanceof Hidden) {
                Hidden hiddenElement = (Hidden) element;
                if (boxName.equals(hiddenElement.getName())) {
                    elementIter.remove();
                }
            }
        }
        SubmitButton requestButton = requestForm.findSubmitButton("input");
        HtmlDocument resultPage = requestButton.submit();
        logger.info("Submitting form");

        List requestedUpasses = resultPage.findAll(".status");
        if (requestedUpasses.size() > prevRequestedUpasses.size()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Status call() {
        try {
            renew();
        } catch (SchoolNotFoundException e) {
            logger.error("School not found: ", e);
            status = Status.SCHOOL_NOT_FOUND;
        } catch (SchoolAuthenticationFailedException e) {
            logger.error("School authentication failed: ", e);
            status = Status.AUTHENTICATION_ERROR;
        } catch (NothingToRenewException e) {
            logger.error("Nothing to renew: ", e);
            status = Status.NOTHING_TO_RENEW;
        } catch (MechanizeException e) {
            logger.error("Mechanize exception: ", e);
            status = Status.NETWORK_ERROR;
        } catch (Exception e) {
            logger.error("Unknown exception: ", e);
            status = Status.ERROR;
        }
        logger.info("Status=" + status);
        return status;
    }

    public RenewJobPayload getPayload() {
        return payload;
    }

    public Status getStatus() {
        return status;
    }

    public String getJobId() {
        return jobId;
    }
}
