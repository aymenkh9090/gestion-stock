package com.gestion.stock.service;

import jakarta.mail.MessagingException;
import org.thymeleaf.context.Context;

public interface EmailService {

    void simpleMail(String to,String subject,String text);
    void sendHtmlMail(String to, String subject, String templateName, Context context) throws MessagingException;
    void sendActionNotification(String to, String actionType, String username, String additionalInfo) throws MessagingException;




}
