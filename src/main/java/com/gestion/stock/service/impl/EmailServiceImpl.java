package com.gestion.stock.service.impl;


import com.gestion.stock.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String fromAddress = "noreply@gestionStock-app.com";


    @Override
    public void simpleMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    public void sendHtmlMail(String to, String subject, String templateName, Context context) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setSubject(subject);

        String htmlContent = templateEngine.process(templateName, context);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    @Override
    public void sendActionNotification(String to, String actionType, String username, String additionalInfo) throws MessagingException {
        Context context = new Context();
        String subject;
        String message;
        String baseUrl = "https://gestionStock-app.com"; // ou localhost:8085

        switch (actionType) {
            case "mail_activation":
                subject = "Activation de compte";
                message = "Bonjour " + username + ", votre compte a été activé avec succès !";
                baseUrl += "/activation";
                break;
            case "mail_deactivation":
                subject = "Désactivation de compte";
                message = "Bonjour " + username + ", votre compte a été désactivé.";
                baseUrl += "/deactivation";
                break;
            case "role_change":
                subject = "Changement de rôle";
                message = "Bonjour " + username + ", votre rôle a été changé vers : " + additionalInfo;
                baseUrl += "/change";
                break;
            case "create":
                subject = "Création de compte";
                message = "Bonjour " + username + ", votre compte a été créé.\n" +
                        "Identifiant : " + to + "\n" +
                        "Mot de passe : " + additionalInfo;
                baseUrl += "/create";
                break;
            case "update":
                subject = "Mise à jour du compte";
                message = "Bonjour " + username + ", vos informations ont été mises à jour.";
                baseUrl += "/update";
                break;
            case "delete_user":
                subject = "Suppression de compte";
                message = "Bonjour " + username + ", votre compte a été supprimé. Rôle : " + additionalInfo;
                baseUrl += "/delete";
                break;
            default:
                subject = "Notification";
                message = "Bonjour " + username + ", une action a été effectuée sur votre compte.";
                baseUrl += "/";
                break;
        }

        context.setVariable("subject", subject);
        context.setVariable("message", message);
        context.setVariable("link", baseUrl);

        sendHtmlMail(to, subject, "email/notification", context);
    }
}
