package com.example.flights.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.example.flights.template.EmailTemplates;
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
public class EmailSenderService
{
    private void sendFlightUpdateEmail(String toEmail, String airlineCode, String flightNumber, String changes) throws IOException
    {
        String subject = "Flight Update Alert - " + airlineCode + " " + flightNumber;
        String flightHtmlContent = EmailTemplates.flightUpdateHtml(airlineCode, flightNumber, changes.replace("\n", "<br/>"));
        sendEmail(toEmail, subject, EmailTemplates.genericHtml(subject, flightHtmlContent), true);
    }

    private void sendConfirmationMail(String toEmail, String airlineCode, String flightNumber, String city_en) throws IOException
    {
        String subject = "Subscription Confirmed - " + airlineCode + " " + flightNumber + " to " + city_en + " ✈️";
        String htmlContent = EmailTemplates.subscriptionConfirmationHtml(airlineCode, flightNumber, city_en);
        sendEmail(toEmail, subject, htmlContent, true);
    }

    private void sendEmail(String toEmail, String subject, String body, boolean html) throws IOException
    {
        Email from = new Email("flightsapispringboot@gmail.com");
        Email to = new Email(toEmail);
        Content content = new Content("text/html", body);

        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");

        request.setBody(mail.build());

        try
        {
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        }
        
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public Mono<Void> sendEmailAsync(String toEmail, String airline_code, String flightNumber, String changes)
    {
        return Mono.fromRunnable(() ->
        {
            try
            {
                sendFlightUpdateEmail(toEmail, airline_code, flightNumber, changes);
            }
            
            catch (IOException e)
            {
                e.printStackTrace();
            }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }

    public Mono<Void> sendConfirmationEmailAsync(String toEmail, String airline_code, String flightNumber, String city_en)
    {
        return Mono.fromRunnable(() ->
        {
            try
            {
                sendConfirmationMail(toEmail, airline_code, flightNumber, city_en);
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        })
        .subscribeOn(Schedulers.boundedElastic())
        .then();
    }
}
