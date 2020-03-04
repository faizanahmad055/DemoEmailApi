package com.myemail.api.util;

import com.myemail.api.dto.Response;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MailUtil {

    private static final Logger LOG = LoggerFactory.getLogger(MailUtil.class);

    public static String getErrorMessage(final Exception error) {
        if (StringUtils.isBlank(error.getMessage())) {
            return error.toString();
        } else {
            return error.getMessage();
        }
    }

    public static Response getMailErrorResponse(final Exception error) {
        String errorMessage = String.format("Error occurred while sending email. Error details: %s", getErrorMessage(error));
        LOG.error(errorMessage);
        return new Response(false, errorMessage);
    }

    public static ResponseEntity<Response> getFailureResponse(final String errorMessage) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response(false, errorMessage));
    }
}
