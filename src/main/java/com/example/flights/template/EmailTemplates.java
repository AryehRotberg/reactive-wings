package com.example.flights.template;

public class EmailTemplates
{
    public static String flightUpdateHtml(String airlineCode, String flightNumber, String changes)
    {
        return """
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
    }

    public static String genericHtml(String subject, String bodyContent)
    {
        return """
            <html>
            <head>
                <style>
                body { font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 20px; }
                .container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.15); }
                h2 { color: #2c3e50; }
                p { font-size: 14px; color: #333333; line-height: 1.5; }
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
            """.formatted(subject, bodyContent);
    }

    public static String subscriptionConfirmationHtml(String airlineCode, String flightNumber) {
        return """
            <html>
            <head>
                <style>
                body { font-family: Arial, sans-serif; }
                .container { max-width: 600px; margin: auto; padding: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                <h2>Subscription Confirmed ✅</h2>
                <p>Hello,</p>
                <p>You have successfully subscribed to flight <b>%s %s</b>.</p>
                <p>We'll notify you when there are updates.</p>
                <p>Best regards,<br/>Flight Notification System</p>
                </div>
            </body>
            </html>
            """.formatted(airlineCode, flightNumber);
    }
}
