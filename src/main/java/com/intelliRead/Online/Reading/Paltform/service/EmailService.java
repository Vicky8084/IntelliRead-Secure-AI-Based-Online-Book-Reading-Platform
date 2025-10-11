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

    // ✅ Official IntelliRead Email ID
    private static final String FROM_EMAIL = "noreply.intelliread@gmail.com";

    // 🔹 Send Email to Admin for Approval
    public void sendAdminApprovalRequest(User user) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);

            // ✅ Admin receiver (change if you have a different admin address)
            helper.setTo("noreply.intelliread@gmail.com");

            helper.setSubject("📝 New Admin Registration Request - IntelliRead Platform");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333;'>" +
                    "<img src='cid:logoImage' width='120' alt='IntelliRead Logo'><br><br>" +
                    "<h2>New Admin Registration Request</h2>" +
                    "<p>User <b>" + user.getName() + "</b> has requested admin access.</p>" +
                    "<p>Email: " + user.getEmail() + "</p>" +
                    "<p>Please take action:</p>" +
                    "<a href='http://localhost:8048/admin/approve/" + user.getId() + "' " +
                    "style='padding:10px 20px;background:#28a745;color:white;text-decoration:none;border-radius:5px;'>Approve</a> " +
                    "<a href='http://localhost:8048/admin/reject/" + user.getId() + "' " +
                    "style='padding:10px 20px;background:#dc3545;color:white;text-decoration:none;border-radius:5px;'>Reject</a>" +
                    "<hr><p>Thank you,<br><b>IntelliRead Team</b><br>" +
                    "<small>📚 Secure AI-Based Online Book Reading Platform</small></p>" +
                    "</body></html>";

            helper.setText(content, true);
            helper.addInline("logoImage", new ClassPathResource("static/images/logo.png"));
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send admin approval email", e);
        }
    }

    // 🔹 Send Email When Admin Approved
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
                    "<p>Smart Reading. Smarter Learning 💁🏻<p>"+
                    "<small>📚 Secure AI-Based Online Book Reading Platform</small></p>" +
                    "\uD83D\uDCE7 contact@intelliread.com | \uD83C\uDF10 www.intelliread.com"+
                    "</body></html>";

            helper.setText(content, true);
            helper.addInline("logoImage", new ClassPathResource("static/images/logo.png"));
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send approval email", e);
        }
    }

    // 🔹 Send Email When Admin Rejected
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
                    "<a href='mailto:intelliread.healpteam@gmail.com'>noreply.intelliread@gmail.com</a>" +
                    "<hr><p>Thanks & Regards," +
                    "<p><b>Team IntelliRead</b><p>" +
                    "<p>Smart Reading. Smarter Learning 💁🏻<p>"+
                    "<small>📚 Secure AI-Based Online Book Reading Platform</small></p>" +
                    "\uD83D\uDCE7 contact@intelliread.com | \uD83C\uDF10 www.intelliread.com"+
            "</body></html>";

            helper.setText(content, true);
            helper.addInline("logoImage", new ClassPathResource("static/images/logo.png"));
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send rejection email", e);
        }
    }
}
