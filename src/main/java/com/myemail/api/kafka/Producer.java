package com.myemail.api.kafka;

import com.myemail.api.dto.Email;
import com.myemail.api.util.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class Producer {

    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, Email> kafkaTemplate;

    @Value("${kafka.mail.topic}")
    private String topic;

    public void send(Email data){
        LOG.info("Sending Email data='{}' to topic='{}'", data, topic);

        Message<Email> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, Email>> future = kafkaTemplate.send(message);
        checkResult(future, data);
    }

    private void checkResult(final ListenableFuture<SendResult<String, Email>> future, final Email data) {
        future.addCallback(new ListenableFutureCallback<SendResult<String, Email>>() {

            @Override
            public void onSuccess(SendResult<String, Email > result) {
                //do nothing
                //LOG.info("Success in sending the data to topic: {}, data: {}", topic, data.toString());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOG.error("Error in sending the data to topic: {}, data: {}, errorDetails: {}", topic, data.toString(), ex.getMessage());
            }
        });
    }
}
