package com.example.flights.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.flights.template.EmailTemplates;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class EmailSenderService
{
    @Autowired(required = false)
    private JavaMailSender mailSender;

    private void sendFlightUpdateEmail(String toEmail, String airlineCode, String flightNumber, String changes)
    {
        String subject = "Flight Update Alert - " + airlineCode + " " + flightNumber;
        String flightHtmlContent = EmailTemplates.flightUpdateHtml(airlineCode, flightNumber, changes.replace("\n", "<br/>"));
        sendEmail(toEmail, subject, EmailTemplates.genericHtml(subject, flightHtmlContent), true);
    }

    private void sendConfirmationMail(String toEmail, String airlineCode, String flightNumber)
    {
        String subject = "Subscription Confirmed - " + airlineCode + " " + flightNumber;
        String htmlContent = EmailTemplates.subscriptionConfirmationHtml(airlineCode, flightNumber);
        sendEmail(toEmail, subject, htmlContent, true);
    }

    private void sendEmail(String toEmail, String subject, String body, boolean html)
    {
        try
        {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setFrom("flightsapispringboot@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, html);
            mailSender.send(mimeMessage);
        }

        catch (MessagingException e)
        {
            System.err.printf("Failed to send HTML email to %s: %s%n", toEmail, e.getMessage());
        }
    }

    public Mono<Void> sendEmailAsync(String toEmail, String airline_code, String flightNumber, String changes)
    {
        return Mono.fromRunnable(() -> sendFlightUpdateEmail(toEmail, airline_code, flightNumber, changes))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<Void> sendConfirmationEmailAsync(String toEmail, String airline_code, String flightNumber)
    {
        return Mono.fromRunnable(() -> sendConfirmationMail(toEmail, airline_code, flightNumber))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
