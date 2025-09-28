package com.example.reactivewings.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.example.reactivewings.utils.EmailTemplates;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class EmailSenderService {
    private final SendGrid sendGrid;

    public EmailSenderService() {
        this.sendGrid = new SendGrid(System.getenv("SENDGRID_API_KEY"));
    }

    private void sendEmail(String toEmail,
                            String subject,
                            String body,
                            boolean html) throws IOException {
        Email from = new Email("flightsapispringboot@gmail.com");
        Email to = new Email(toEmail);
        Content content = new Content("text/html", body);

        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");

        request.setBody(mail.build());

        try {
            Response response = this.sendGrid.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Mono<Void> sendFlightUpdateEmailAsync(String toEmail,
                                                String airlineCode,
                                                String flightNumber,
                                                String changes) {
        return Mono.fromRunnable(() -> {
            try {
                String subject = "Flight Update Alert - " + airlineCode + " " + flightNumber;
                String flightHtmlContent = EmailTemplates.flightUpdateHtml(airlineCode, flightNumber, changes);
                sendEmail(toEmail, subject, EmailTemplates.genericHtml(subject, flightHtmlContent), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    public Mono<Void> sendConfirmationEmailAsync(String toEmail,
                                                String airlineCode,
                                                String flightNumber,
                                                String cityEn,
                                                String direction) {
        return Mono.fromRunnable(() -> {
            try {
                String directionStr = direction.equals("D") ? "to" : "from";
                String subject = new StringBuilder()
                    .append("Subscription Confirmed - ")
                    .append(airlineCode).append(" ")
                    .append(flightNumber).append(" ")
                    .append(directionStr).append(" ")
                    .append(cityEn)
                    .append(" ✈️")
                    .toString();
                String htmlContent = EmailTemplates.subscriptionConfirmationHtml(airlineCode, flightNumber, directionStr, cityEn);
                sendEmail(toEmail, subject, htmlContent, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }
}
