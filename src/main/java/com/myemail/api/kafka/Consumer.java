package com.myemail.api.kafka;

import com.myemail.api.client.MailClient;
import com.myemail.api.dto.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;

@Service
@Profile("kafka")
public class Consumer {

    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    MailClient mailClient;

    @KafkaListener(topics = "${kafka.mail.topic}")
    public void receive(@Payload Email data,
                        @Headers MessageHeaders headers) {
        LOG.info("Received email data='{}'", data);

        headers.keySet().forEach(key -> {
            LOG.debug("Headers - {}: {}", key, headers.get(key));
        });

        try {
            mailClient.sendEmail(data);
        } catch (Exception e) {
            LOG.error("Error received while sending email for data: {}", data.toString());
        }
    }

}
