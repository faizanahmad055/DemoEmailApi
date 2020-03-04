package com.myemail.api.controller;

import com.myemail.api.client.MailClient;
import com.myemail.api.dto.Email;
import com.myemail.api.dto.Response;
import com.myemail.api.kafka.Producer;
import com.myemail.api.util.MailUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class MailController {
    private static final Logger LOG = LoggerFactory.getLogger(MailController.class);

    @Autowired
    private MailClient mailClient;

    @Autowired
    private Producer producer;

    @Autowired
    Environment env;

    @PostMapping(value = "/v1/group/mail/sent")
    public ResponseEntity<Response> sendMail(@RequestBody final Email email) {
        try {
            Response response = mailClient.sendEmail(email);
            return ResponseEntity.ok(response);
        } catch (Exception error) {
            Response response = MailUtil.getMailErrorResponse(error);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @PostMapping(value = "/v1/group/mail/publish")
    public ResponseEntity<Response> publishMail(@RequestBody final Email email) {
        String errorMessage = "";
        if(ArrayUtils.contains(env.getActiveProfiles(), "kafka")) {
            try {
                producer.send(email);
                return ResponseEntity.ok(new Response(true, "Mail has been published to the topic"));
            } catch (Exception error) {
                errorMessage = String.format("Error encountered while sending the data to kafka topic, errorDetails: %s",
                        MailUtil.getErrorMessage(error));
                LOG.error(errorMessage);
                return MailUtil.getFailureResponse(errorMessage);
            }
        } else {
            errorMessage = String.format("'Kafka' profile is not active to publish, active profiles: %s", env.getActiveProfiles());
            LOG.warn(errorMessage);
            return MailUtil.getFailureResponse(errorMessage);
        }
    }
}
