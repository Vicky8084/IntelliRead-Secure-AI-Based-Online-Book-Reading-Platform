package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String FROM_EMAIL = "noreply.intelliread@gmail.com";

    // 🔹 1. Welcome Email for Regular Users
    public void sendWelcomeEmail(User user) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(user.getEmail());
            helper.setSubject("🎉 Welcome to IntelliRead - Your Reading Journey Begins!");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<div style='text-align: center; margin-bottom: 30px;'>" +
                    "<img src='cid:logoImage' width='150' alt='IntelliRead Logo' style='margin-bottom: 20px;'>" +
                    "<h1 style='color: #2c5aa0; margin-bottom: 10px;'>Welcome to IntelliRead!</h1>" +
                    "<p style='color: #666; font-size: 16px;'>Your AI-Powered Reading Companion</p>" +
                    "</div>" +

                    "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;'>" +
                    "<h2 style='color: #2c5aa0; margin-bottom: 15px;'>Hello " + user.getName() + ",</h2>" +
                    "<p style='margin-bottom: 15px;'>We're thrilled to welcome you to <strong>IntelliRead</strong> - where reading meets intelligence! 🚀</p>" +
                    "<p style='margin-bottom: 15px;'>Your account has been successfully created and you're all set to explore our vast library of books with AI-powered features.</p>" +
                    "</div>" +

                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<a href='http://localhost:8035/auth/login' " +
                    "style='display: inline-block; padding: 12px 30px; background: #2c5aa0; color: white; " +
                    "text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>" +
                    "Start Reading Now 📚" +
                    "</a>" +
                    "</div>" +

                    "<div style='border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 20px; text-align: center;'>" +
                    "<p style='color: #666; margin-bottom: 10px;'>Need help? We're here for you!</p>" +
                    "<p style='color: #666; margin-bottom: 5px;'>📧 Email: <a href='mailto:noreply.intelliread@gmail.com' style='color: #2c5aa0;'>noreply.intelliread@gmail.com</a></p>" +
                    "</div>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(content, true);

            try {
                helper.addInline("logoImage", new ClassPathResource("static/images/logo.png"));
            } catch (Exception e) {
                System.out.println("Logo image not found, sending email without logo");
            }

            mailSender.send(mimeMessage);
            System.out.println("✅ Welcome email sent to: " + user.getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send welcome email to: " + user.getEmail());
            e.printStackTrace();
        }
    }

    // 🔹 2. NEW: Admin Welcome Email (Direct admin creation)
    public void sendAdminWelcomeEmail(User admin) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(admin.getEmail());
            helper.setSubject("🎉 Welcome to IntelliRead - Admin Account Created");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<div style='text-align: center; margin-bottom: 30px;'>" +
                    "<img src='cid:logoImage' width='150' alt='IntelliRead Logo' style='margin-bottom: 20px;'>" +
                    "<h1 style='color: #2c5aa0; margin-bottom: 10px;'>Welcome to IntelliRead Admin Panel!</h1>" +
                    "<p style='color: #666; font-size: 16px;'>Your Administrator Account is Ready</p>" +
                    "</div>" +

                    "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;'>" +
                    "<h2 style='color: #2c5aa0; margin-bottom: 15px;'>Hello " + admin.getName() + ",</h2>" +
                    "<p style='margin-bottom: 15px;'>Congratulations! Your <strong>Administrator account</strong> has been successfully created on <strong>IntelliRead</strong>.</p>" +
                    "<p style='margin-bottom: 15px;'>You now have full access to manage the platform and its users.</p>" +
                    "</div>" +

                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<a href='http://localhost:8035/auth/login' " +
                    "style='display: inline-block; padding: 12px 30px; background: #2c5aa0; color: white; " +
                    "text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>" +
                    "Access Admin Dashboard 🚀" +
                    "</a>" +
                    "</div>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(content, true);

            try {
                helper.addInline("logoImage", new ClassPathResource("static/images/logo.png"));
            } catch (Exception e) {
                System.out.println("Logo image not found, sending email without logo");
            }

            mailSender.send(mimeMessage);
            System.out.println("✅ Admin welcome email sent to: " + admin.getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send admin welcome email to: " + admin.getEmail());
            e.printStackTrace();
        }
    }

    // 🔹 3. Admin Approval Request Email
    public void sendAdminApprovalRequest(User user) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo("noreply.intelliread@gmail.com");
            helper.setSubject("📝 New Admin Registration Request - IntelliRead Platform");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<img src='cid:logoImage' width='120' alt='IntelliRead Logo'><br><br>" +
                    "<h2>New Admin Registration Request</h2>" +
                    "<p>User <b>" + user.getName() + "</b> has requested admin access.</p>" +
                    "<p>Email: " + user.getEmail() + "</p>" +
                    "<p>Please take action:</p>" +
                    "<a href='http://localhost:8035/admin/approve/" + user.getId() + "' " +
                    "style='padding:10px 20px;background:#28a745;color:white;text-decoration:none;border-radius:5px;'>Approve</a> " +
                    "<a href='http://localhost:8035/admin/reject/" + user.getId() + "' " +
                    "style='padding:10px 20px;background:#dc3545;color:white;text-decoration:none;border-radius:5px;'>Reject</a>" +
                    "<hr><p>Thank you,<br><b>IntelliRead Team</b><br>" +
                    "<small>📚 Secure AI-Based Online Book Reading Platform</small></p>" +
                    "</body></html>";

            helper.setText(content, true);
            try {
                helper.addInline("logoImage", new ClassPathResource("static/images/logo.png"));
            } catch (Exception e) {
                System.out.println("Logo image not found");
            }
            mailSender.send(mimeMessage);
            System.out.println("✅ Admin approval email sent for: " + user.getEmail());

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send admin approval email", e);
        }
    }

    // 🔹 4. Admin Approved Email
    public void sendAdminApproved(User user) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(user.getEmail());
            helper.setSubject("🎉 Welcome to IntelliRead - Admin Access Granted");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<img src='cid:logoImage' width='120' alt='IntelliRead Logo'><br><br>" +
                    "<h2>Hi " + user.getName() + ",</h2>" +
                    "<p>Congratulations! Your <b>ADMIN registration</b> has been approved.</p>" +
                    "<p>Start exploring your dashboard 🚀</p>" +
                    "<p>Now you have access to manage content and users on <b>IntelliRead</b>.</p>" +
                    "<hr><p>Thanks & Regards," +
                    "<p><b>Team IntelliRead</b><p>" +
                    "</body></html>";

            helper.setText(content, true);
            try {
                helper.addInline("logoImage", new ClassPathResource("static/images/logo.png"));
            } catch (Exception e) {
                System.out.println("Logo image not found");
            }
            mailSender.send(mimeMessage);
            System.out.println("✅ Admin approved email sent to: " + user.getEmail());

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send approval email", e);
        }
    }

    // 🔹 5. Admin Rejected Email
    public void sendAdminRejected(User user) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(user.getEmail());
            helper.setSubject("❌ IntelliRead Admin Registration Update");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<img src='cid:logoImage' width='120' alt='IntelliRead Logo'><br><br>" +
                    "<h2>Hello " + user.getName() + ",</h2>" +
                    "<p>We appreciate your interest in joining IntelliRead as an Admin.</p>" +
                    "<p>Unfortunately, your registration request has been <b>rejected</b> at this time.</p>" +
                    "<p>If you believe this was a mistake or wish to reapply, please contact our support team:</p>" +
                    "<a href='mailto:noreply.intelliread@gmail.com'>noreply.intelliread@gmail.com</a>" +
                    "<hr><p>Thanks & Regards,<br><b>Team IntelliRead</b></p>" +
                    "</body></html>";

            helper.setText(content, true);
            try {
                helper.addInline("logoImage", new ClassPathResource("static/images/logo.png"));
            } catch (Exception e) {
                System.out.println("Logo image not found");
            }
            mailSender.send(mimeMessage);
            System.out.println("✅ Admin rejected email sent to: " + user.getEmail());

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send rejection email", e);
        }
    }
}