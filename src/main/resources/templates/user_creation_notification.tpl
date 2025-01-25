<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Creation Notification</title>
</head>
<body style="margin: 0; padding: 0; background-color: #f4f4f4; font-family: Arial, sans-serif; color: #333333; line-height: 1.6;">

<!-- Email Container -->
<table width="100%" cellpadding="0" cellspacing="0" style="margin: 0; padding: 0; width: 100%; background-color: #f4f4f4;">
    <tr>
        <td align="center">
            <table width="600" cellpadding="0" cellspacing="0" style="max-width: 600px; width: 100%; background-color: #ffffff; border-radius: 12px; overflow: hidden; border: 1px solid #ddd;">

                <!-- Header -->
                <tr>
                    <td align="center" style="background-color: #0191f1; padding: 30px 20px;">
                        <img src="https://res.cloudinary.com/drk3cmq8j/image/upload/v1732700894/2_r93yck.png" alt="Infometics Limited Logo" width="150" height="auto" style="margin-bottom: 20px; display: block;">
                        <h1 style="margin: 0; font-size: 24px; font-weight: bold; color: #ffffff;">User Creation Notification</h1>
                    </td>
                </tr>

                <!-- Content -->
                <tr>
                    <td style="padding: 40px 30px; font-size: 16px; color: #333333;">
                        <p style="margin: 0 0 20px;">Dear ${initiator},</p>

                        <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #e6f3ff; border-left: 4px solid #0191f1; padding: 15px; margin-bottom: 20px;">
                            <tr>
                                <td>
                                    <p style="margin: 0;">An account has been created with your email address:</p>
                                    <p style="margin: 10px 0; font-weight: bold; color: #0191f1;">${email}</p>
                                    <p style="margin: 0;">Password: <span style="font-weight: bold; color: #0191f1;">${password}</span></p>
                                </td>
                            </tr>
                        </table>

                        <p style="margin: 20px 0;">Please login to your account and change your password:</p>

                        <!-- Button -->
                        <p style="text-align: center; margin: 30px 0;">
                            <a href="https://meticshelp.infometicz.com/login/" style="background-color: #0191f1; color: #ffffff; padding: 12px 24px; border-radius: 6px; font-size: 16px; text-decoration: none; font-weight: bold; display: inline-block;">Login</a>
                        </p>

                        <p style="margin: 20px 0;">If you have any questions or need further assistance, please don't hesitate to contact our support team. We're here to help!</p>

                        <p style="margin: 20px 0;">
                            Best regards,<br>
                            <strong>Infometics Limited</strong>
                        </p>
                    </td>
                </tr>

                <!-- Footer -->
                <tr>
                    <td align="center" style="background-color: #f8f9fa; padding: 20px; font-size: 14px; color: #666666; border-top: 1px solid #e9ecef;">
                        <p style="margin: 0;">&copy; 2024 INFOMETICS. All rights reserved.</p>
                        <p style="margin: 0;">This is an automated message. Please do not reply to this email.</p>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
