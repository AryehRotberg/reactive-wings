package com.example.reactivewings.utils;

public class EmailTemplates
{
    public static String flightUpdateHtml(String airlineCode, String flightNumber, String changes)
    {
        return """
            <html dir="rtl">
            <head>
                <meta charset="UTF-8">
                <style>
                body { font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 20px; direction: rtl; text-align: right; }
                .container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.15); direction: rtl; text-align: right; }
                h2 { color: #2c3e50; text-align: right; }
                p { font-size: 14px; color: #333333; line-height: 1.5; text-align: right; }
                .changes { background: #f4f6f9; padding: 10px; border-right: 4px solid #3498db; margin: 15px 0; text-align: right; }
                .footer { font-size: 12px; color: #777777; margin-top: 20px; border-top: 1px solid #eeeeee; padding-top: 10px; text-align: right; }
                </style>
            </head>
            <body dir="rtl">
                <div class="container" dir="rtl">
                <h2>התראת עדכון טיסה ✈️</h2>
                <p>שלום,</p>
                <p>עודכנה טיסתך <b>%s %s</b> עם השינויים הבאים:</p>
                <div class="changes">
                    <p>%s</p>
                </div>
                <p>נא לבדוק את פרטי הטיסה ולהגיע לשדה התעופה בהתאם.</p>
                <p>בברכה,<br/>reactivewings - מערכת התראות טיסות</p>
                <div class="footer">
                    <p>זוהי הודעה אוטומטית. נא לא להשיב.</p>
                </div>
                </div>
            </body>
            </html>
            """.formatted(airlineCode, flightNumber, changes.replace("\n", "<br/>"));
    }

    public static String genericHtml(String subject, String bodyContent)
    {
        return """
            <html dir="rtl">
            <head>
                <meta charset="UTF-8">
                <style>
                body { font-family: Arial, sans-serif; background-color: #f9f9f9; margin: 0; padding: 20px; direction: rtl; text-align: right; }
                .container { max-width: 600px; margin: auto; background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.15); direction: rtl; text-align: right; }
                h2 { color: #2c3e50; text-align: right; }
                p { font-size: 14px; color: #333333; line-height: 1.5; text-align: right; }
                .footer { font-size: 12px; color: #777777; margin-top: 20px; border-top: 1px solid #eeeeee; padding-top: 10px; text-align: right; }
                </style>
            </head>
            <body dir="rtl">
                <div class="container" dir="rtl">
                <h2>%s</h2>
                <div>
                    %s
                </div>
                <div class="footer">
                    <p>reactivewings - מערכת התראות טיסות</p>
                </div>
                </div>
            </body>
            </html>
            """.formatted(subject, bodyContent);
    }

    public static String subscriptionConfirmationHtml(String airlineCode,
                                                    String flightNumber,
                                                    String direction,
                                                    String city_en) {
        return """
            <html dir="rtl">
            <head>
                <meta charset="UTF-8">
                <style>
                body { font-family: Arial, sans-serif; direction: rtl; text-align: right; }
                .container { max-width: 600px; margin: auto; padding: 20px; direction: rtl; text-align: right; }
                h2 { text-align: right; }
                p { text-align: right; }
                .footer { font-size: 12px; color: #777777; margin-top: 20px; border-top: 1px solid #eeeeee; padding-top: 10px; text-align: right; }
                </style>
            </head>
            <body dir="rtl">
                <div class="container" dir="rtl">
                <h2>המנוי אושר ✅</h2>
                <p>שלום,</p>
                <p>נרשמת בהצלחה לקבלת עדכונים על טיסה <b>%s %s %s %s.</b></p>
                <p>נודיע לך כאשר יהיו עדכונים.</p>
                <p>בברכה,<br/>reactivewings - מערכת התראות טיסות</p>
                <div class="footer">
                    <p>זוהי הודעה אוטומטית. נא לא להשיב.</p>
                </div>
                </div>
            </body>
            </html>
            """.formatted(airlineCode, flightNumber, direction, city_en);
    }
}
