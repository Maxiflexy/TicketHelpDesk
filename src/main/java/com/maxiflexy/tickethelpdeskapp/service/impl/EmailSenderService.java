package com.maxiflexy.tickethelpdeskapp.service.impl;


import com.infometics.helpdesk.model.MailModel;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class EmailSenderService {

    private final JavaMailSender javaMailSender;
    private final SimpleMailMessage simpleMail;
    private final Configuration templateConfiguration;

    private final String sender = "Infometics Helpdesk <helpdesk@infometics.net>";
    private final String sender2 = "Infometics Helpdesk <helpdesk@infometics.net>";

    @Async("threadPoolTaskExecutor")
    @Retryable(maxAttempts = 5, value = RuntimeException.class, backoff = @Backoff(delay = 10000, multiplier = 12))
    public void sendEmail(MailModel mailModel) throws InterruptedException {
        log.info("Sending mail");
        System.out.println("Sleeping now...");
        Thread.sleep(10000);

        System.out.println("Sending email...");
        if (mailModel.isHasAttachment()) {
            sendMailWithAttachment(mailModel);
        } else if (mailModel.isUseTemplate()) {
            log.info("sending template email");
            sendHtmlEmail(mailModel);
        } else {
            sendPlainEmail(mailModel);
        }
    }

    @Recover
    public void sendEmailRecover(MailModel model) {
        log.info("=================================================================================");
        log.info("error sending mail: {}", model.toString());
        log.info("=================================================================================");
    }

    private void sendPlainEmail(MailModel model) {
        simpleMail.setFrom(sender);
        simpleMail.setTo(model.getTo());
        simpleMail.setBcc(model.getBcc());
        simpleMail.setCc(model.getCc());
        simpleMail.setSubject(model.getSubject());
        try {
            if (model.isUseTemplate()) {
                simpleMail.setText(FreeMarkerTemplateUtils.processTemplateIntoString(
                        templateConfiguration.getTemplate(model.getTemplateName(), model.getLocale()),
                        model.getMessageMap()));
            } else {
                simpleMail.setText(model.getMessage());
            }
            simpleMail.setSentDate(new Date());
            javaMailSender.send(simpleMail);
            log.info("Plain Email Sent !");
        } catch (IOException | TemplateException e) {
            log.error("error occurred sending plain email to client, error message::: {}", e.getMessage());
        }
    }

    private void sendHtmlEmail(MailModel model) {
        try {
            final MimeMessage mime = javaMailSender.createMimeMessage();
            final MimeMessageHelper messageHelper = new MimeMessageHelper(mime, true);
            messageHelper.setFrom(sender);
            messageHelper.setSubject(model.getSubject());
            messageHelper.setTo(model.getTo());
            messageHelper.setBcc(model.getBcc());
            messageHelper.setCc(model.getCc());
            templateConfiguration.setClassForTemplateLoading(this.getClass(), "/");
            final String htmlContext = FreeMarkerTemplateUtils.processTemplateIntoString(
                    templateConfiguration.getTemplate("/templates/" + model.getTemplateName(), model.getLocale()),
                    model.getMessageMap());
            messageHelper.setText(htmlContext, true);
            javaMailSender.send(mime);
            log.info("Html Email Sent !");
        } catch (IOException | TemplateException | MessagingException e) {
            log.error("error occurred sending HTML email to client, error message::: {}", e.getMessage());
        }
    }

    private void sendMailWithAttachment(MailModel mailModel) {
        try {
            final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setFrom(sender2);
            messageHelper.setSubject(mailModel.getSubject());
            messageHelper.setBcc(mailModel.getBcc());
            messageHelper.setCc(mailModel.getCc());
            messageHelper.setTo(mailModel.getTo());
            templateConfiguration.setClassForTemplateLoading(this.getClass(), "/");
            final String htmlContext = FreeMarkerTemplateUtils.processTemplateIntoString(templateConfiguration
                            .getTemplate("/templates/" + mailModel.getTemplateName(), mailModel.getLocale()),
                    mailModel.getMessageMap());
            messageHelper.setText(htmlContext, true);

            if (mailModel.getHasUrlLocation().equals(Boolean.TRUE)) {
                URL url = new URL(mailModel.getUrl());
                UrlResource urlResource = new UrlResource(url);
                messageHelper.addAttachment(mailModel.getAttachmentFileName(), urlResource,
                        mailModel.getAttachmentContentType());
            } else {
                final InputStreamSource attachmentSource = new ByteArrayResource(mailModel.getAttachmentBytes(),
                        mailModel.getDescription());
                messageHelper.addAttachment(mailModel.getAttachmentFileName(), attachmentSource,
                        mailModel.getAttachmentContentType());
            }
            javaMailSender.send(mimeMessage);
            log.info("Html Email with attachment Sent !");
        } catch (IOException | TemplateException | MessagingException e) {
            log.error("error occurred sending HTML with Attachment email to client  {} ", e);
        }
    }
}

