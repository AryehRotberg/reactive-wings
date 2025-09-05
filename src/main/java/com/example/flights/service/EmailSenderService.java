package com.example.flights.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class EmailSenderService
{
    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body)
    {
        if (mailSender == null) {
            System.out.println("Mail sender not available. Would send email to: " + toEmail + " with subject: " + subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("flightsapispringboot@gmail.com");
            message.setTo(toEmail);
            message.setText(body);
            message.setSubject(subject);

            mailSender.send(message);

            System.out.println("Mail sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
            System.err.println("Make sure you're using a Gmail App Password, not your regular password");
        }
    }

    public Mono<Void> sendEmailAsync(String toEmail, String subject, String body)
    {
        return Mono.fromRunnable(() -> sendEmail(toEmail, subject, body))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public void sendFlightUpdateEmail(String toEmail, String airline_code, String flightNumber, String changes)
    {
        String subject = "Flight Update Alert - " + airline_code + " " + flightNumber;
        String body = "Hello,\n\n" +
                     "Your flight " + airline_code + " " + flightNumber + " has been updated with the following changes:\n\n" +
                     changes + "\n\n" +
                     "Please check your flight details and arrive at the airport accordingly.\n\n" +
                     "Best regards,\n" +
                     "Flight Notification System";
        
        sendEmail(toEmail, subject, body);
    }

    public void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        if (mailSender == null) {
            System.out.printf("MailSender not available. Would have sent HTML email to %s with subject '%s'%n", toEmail, subject);
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setFrom("flightsapispringboot@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);

            String finalHtml = """
                <html>
                <head>
                    <style>
                    body { font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.15); }
                    h2 { color: #2c3e50; }
                    p { font-size: 14px; color: #333333; }
                    .footer { font-size: 12px; color: #777777; margin-top: 20px; border-top: 1px solid #eeeeee; padding-top: 10px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                    <h2>%s</h2>
                    <div>
                        %s
                    </div>
                    <div class="footer">
                        <p>Flight Notification System</p>
                    </div>
                    </div>
                </body>
                </html>
                """.formatted(subject, htmlContent);

            helper.setText(finalHtml, true);

            mailSender.send(mimeMessage);
            System.out.printf("HTML email sent successfully to %s%n", toEmail);
        } catch (MessagingException e) {
            System.err.printf("Failed to send HTML email to %s: %s%n", toEmail, e.getMessage());
        }
    }

    public void sendFlightUpdateEmailHtml(String toEmail, String airlineCode, String flightNumber, String changes) {
        String subject = "Flight Update Alert - " + airlineCode + " " + flightNumber;

        // Build a styled HTML template
        String htmlContent = """
            <html>
            <head>
                <style>
                body { font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 20px; }
                .container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.15); }
                h2 { color: #2c3e50; }
                p { font-size: 14px; color: #333333; line-height: 1.5; }
                .changes { background: #f4f6f9; padding: 10px; border-left: 4px solid #3498db; margin: 15px 0; }
                .footer { font-size: 12px; color: #777777; margin-top: 20px; border-top: 1px solid #eeeeee; padding-top: 10px; }
                </style>
            </head>
            <body>
                <div class="container">
                <h2>Flight Update Alert ✈️</h2>
                <p>Hello,</p>
                <p>Your flight <b>%s %s</b> has been updated with the following changes:</p>
                <div class="changes">
                    <p>%s</p>
                </div>
                <p>Please check your flight details and arrive at the airport accordingly.</p>
                <p>Best regards,<br/>Flight Notification System</p>
                <div class="footer">
                    <p>This is an automated message. Please do not reply.</p>
                </div>
                </div>
            </body>
            </html>
            """.formatted(airlineCode, flightNumber, changes.replace("\n", "<br/>"));

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    public Mono<Void> sendFlightUpdateEmailAsync(String toEmail, String airline_code, String flightNumber, String changes)
    {
        return Mono.fromRunnable(() -> sendFlightUpdateEmail(toEmail, airline_code, flightNumber, changes))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<Void> sendHtmlEmailAsync(String toEmail, String airline_code, String flightNumber, String changes) {
        return Mono.fromRunnable(() -> sendFlightUpdateEmailHtml(toEmail, airline_code, flightNumber, changes))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
