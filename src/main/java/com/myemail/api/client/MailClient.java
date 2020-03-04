package com.myemail.api.client;

import com.myemail.api.dto.Email;
import com.myemail.api.dto.Response;
import com.myemail.api.util.MailUtil;
import com.myemail.api.util.ResilientValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

@Service
public class MailClient {

    private static final Logger LOG = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Retryable(
            value = {Exception.class},
            maxAttempts = ResilientValue.MAX_RETRY,
            backoff = @Backoff(delay = ResilientValue.BACKOFF_MILLISECONDS))
    public Response sendEmail(final Email email) throws MessagingException {
        LOG.warn("Going to send email with data: {}", email.toString());
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(email.getTo());
        helper.setFrom(email.getFrom());
        helper.setSubject(email.getSubject());
        helper.setText(email.getContent(), email.getIsHtmlContent());
        addAttachments(helper, email.getAttachmentUriList());
        javaMailSender.send(msg);
        return new Response(true, "Mail has been sent");
    }

    @Recover
    public Response recover(Exception error, Email email) {
        return MailUtil.getMailErrorResponse(error);
    }

    private void addAttachments(final MimeMessageHelper helper, final List<String> attachmentUriList) throws MessagingException {
        for (String uri : attachmentUriList) {
            File file = new File(uri);
            if (file.exists()) {
                FileSystemResource fileResource = new FileSystemResource(file);
                helper.addAttachment(fileResource.getFilename(), fileResource);
            } else {
                LOG.warn("Skipping the attachment. File: '{}' does not exist.", uri);
            }
        }
    }
}
