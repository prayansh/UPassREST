package com.prayansh.upass.controllers;

import com.prayansh.upass.services.RenewService;
import com.prayansh.upass.models.RenewJob;
import com.prayansh.upass.models.RenewJobPayload;
import com.prayansh.upass.utils.CryptUtils;
import com.prayansh.upass.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Prayansh on 2017-10-21.
 */
@RestController
@RequestMapping("/api")
public class RenewServiceController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RenewService renewService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<Object> index() {
        logger.info("\"/\": called");
        return new ResponseEntity<>(Util.simpleKVP("version", String.valueOf(2)), HttpStatus.OK);
    }

    @RequestMapping(value = "/renew", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> renew(@RequestHeader(value = "username") String username,
                                        @RequestHeader(value = "password") String password,
                                        @RequestHeader(value = "school") String school) {
        logger.info("\"/renew\": called");
        logger.info("Params: username=" + username + ", password=" + password + ", school=" + school);
        CryptUtils crypt = new CryptUtils();
        String decryptedUsername, decryptedPassword;
        try {
            decryptedUsername = crypt.decrypt(username);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(Util.simpleKVP("error", "Username is encrypted incorrectly"),
                    HttpStatus.UNAUTHORIZED);
        }
        try {
            decryptedPassword = crypt.decrypt(password);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(Util.simpleKVP("error", "Password is encrypted incorrectly"),
                    HttpStatus.UNAUTHORIZED);
        }
        RenewJobPayload payload = new RenewJobPayload(decryptedUsername, decryptedPassword, school);
        if (renewService.jobExists(payload)) {
            return new ResponseEntity<>(Util.simpleKVP("response", "Job already submitted"),
                    HttpStatus.ALREADY_REPORTED);
        }
        RenewJob job = renewService.createJob(payload);
        return new ResponseEntity<>(job, HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> renew(@RequestHeader(value = "id") String id) {
        logger.info("\"/get\": called");
        logger.info("Params: id=" + id);
        RenewJob job = renewService.getJob(id);
        if (job == null) {
            return new ResponseEntity<>(Util.simpleKVP("error", "No job found"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(job, HttpStatus.OK);
    }

    @RequestMapping(value = "/shutdown", method = RequestMethod.GET)
    public ResponseEntity<Object> shutdown(@RequestHeader(value = "key") String key) {
        logger.info("\"/shutdown\": called");
        if (key.equalsIgnoreCase(Util.readConfigVar("SECRET"))) {
            renewService.shutdown();
            return new ResponseEntity<>(Util.simpleKVP("result", "Job pool shutdown"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Util.simpleKVP("error", "That is the incorrect key"), HttpStatus.FORBIDDEN);
        }
    }
}
