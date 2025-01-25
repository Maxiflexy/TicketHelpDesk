package com.maxiflexy.tickethelpdeskapp.model;

import lombok.Data;
import lombok.ToString;

import java.util.Locale;
import java.util.Map;

@Data
@ToString
public class MailModel {

    private String subject;
    private String from;
    private String to[] = new String[0];
    private String bcc[] = new String[0];
    private String cc[] = new String[0];
    private String message;
    private String type = "html";
    private Map<String, String> attachedFiles;
    private Locale locale = Locale.ENGLISH;
    private String templateName;
    private boolean hasAttachment = false;
    private boolean useTemplate = false;
    private Boolean hasUrlLocation = false;
    private Map<String, String> messageMap;
    private byte[] attachmentBytes;
    private String attachmentContentType;
    private String attachmentFileName;
    private String description;
    private String url;
}