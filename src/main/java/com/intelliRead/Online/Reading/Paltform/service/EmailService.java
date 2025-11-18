package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.model.Suggestion;
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

    // 🔹 NEW: Publisher Approval Email
    public void sendPublisherApprovalEmail(User publisher) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(publisher.getEmail());
            helper.setSubject("🎉 Publisher Account Approved - IntelliRead");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<div style='text-align: center; margin-bottom: 30px;'>" +
                    "<img src='cid:logoImage' width='150' alt='IntelliRead Logo' style='margin-bottom: 20px;'>" +
                    "<h1 style='color: #28a745; margin-bottom: 10px;'>Publisher Account Approved!</h1>" +
                    "<p style='color: #666; font-size: 16px;'>Start publishing your books on IntelliRead</p>" +
                    "</div>" +

                    "<div style='background: #d4edda; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #28a745;'>" +
                    "<h2 style='color: #155724; margin-bottom: 15px;'>Congratulations " + publisher.getName() + "!</h2>" +
                    "<p style='color: #155724; margin-bottom: 15px;'>Your <strong>Publisher account</strong> has been <strong>approved</strong> by our admin team.</p>" +
                    "<p style='color: #155724; margin-bottom: 15px;'>You can now start uploading and publishing your books on IntelliRead platform.</p>" +
                    "</div>" +

                    "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;'>" +
                    "<h3 style='color: #2c5aa0; margin-bottom: 15px;'>🚀 What You Can Do Now:</h3>" +
                    "<ul style='color: #495057; margin-bottom: 15px; padding-left: 20px;'>" +
                    "<li>Upload your books in PDF or TXT format</li>" +
                    "<li>Add book covers and descriptions</li>" +
                    "<li>Manage your book catalog</li>" +
                    "<li>Track reader engagement and reviews</li>" +
                    "<li>Reach thousands of readers worldwide</li>" +
                    "</ul>" +
                    "</div>" +

                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<a href='http://localhost:8035/publisher-dashboard' " +
                    "style='display: inline-block; padding: 12px 30px; background: #28a745; color: white; " +
                    "text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>" +
                    "Go to Publisher Dashboard" +
                    "</a>" +
                    "</div>" +

                    "<div style='border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 20px; text-align: center;'>" +
                    "<p style='color: #666; margin-bottom: 10px;'>Need help getting started?</p>" +
                    "<p style='color: #666; margin-bottom: 5px;'>📧 Email: <a href='mailto:noreply.intelliread@gmail.com' style='color: #2c5aa0;'>noreply.intelliread@gmail.com</a></p>" +
                    "<p style='color: #999; font-size: 12px; margin-top: 20px;'>Welcome to the IntelliRead publishing community! 📚</p>" +
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
            System.out.println("✅ Publisher approval email sent to: " + publisher.getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send publisher approval email to: " + publisher.getEmail());
            e.printStackTrace();
            throw new RuntimeException("Failed to send publisher approval email", e);
        }
    }

    // 🔹 NEW: Book Approval Email to Publisher
    public void sendBookApprovalEmail(User publisher, Book book) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(publisher.getEmail());
            helper.setSubject("✅ Book Approved - \"" + book.getTitle() + "\" - IntelliRead");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<div style='text-align: center; margin-bottom: 30px;'>" +
                    "<img src='cid:logoImage' width='150' alt='IntelliRead Logo' style='margin-bottom: 20px;'>" +
                    "<h1 style='color: #28a745; margin-bottom: 10px;'>Book Approved! 🎉</h1>" +
                    "<p style='color: #666; font-size: 16px;'>Your book is now live on IntelliRead</p>" +
                    "</div>" +

                    "<div style='background: #d4edda; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #28a745;'>" +
                    "<h2 style='color: #155724; margin-bottom: 15px;'>Great News " + publisher.getName() + "!</h2>" +
                    "<p style='color: #155724; margin-bottom: 15px;'>Your book <strong>\"" + book.getTitle() + "\"</strong> has been <strong>approved</strong> by our admin team and is now available for readers on IntelliRead.</p>" +
                    "</div>" +

                    "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;'>" +
                    "<h3 style='color: #2c5aa0; margin-bottom: 15px;'>📖 Book Details:</h3>" +
                    "<p style='color: #495057; margin-bottom: 8px;'><strong>Title:</strong> " + book.getTitle() + "</p>" +
                    "<p style='color: #495057; margin-bottom: 8px;'><strong>Author:</strong> " + book.getAuthor() + "</p>" +
                    "<p style='color: #495057; margin-bottom: 8px;'><strong>Category:</strong> " + (book.getCategory() != null ? book.getCategory().getCategoryName() : "Not specified") + "</p>" +
                    "<p style='color: #495057; margin-bottom: 8px;'><strong>Language:</strong> " + book.getLanguage() + "</p>" +
                    "<p style='color: #495057; margin-bottom: 8px;'><strong>Status:</strong> <span style='color: #28a745; font-weight: bold;'>PUBLISHED</span></p>" +
                    "</div>" +

                    "<div style='background: #e7f3ff; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #2c5aa0;'>" +
                    "<h3 style='color: #2c5aa0; margin-bottom: 15px;'>📈 What's Next?</h3>" +
                    "<ul style='color: #495057; margin-bottom: 15px; padding-left: 20px;'>" +
                    "<li>Your book is now available to all IntelliRead users</li>" +
                    "<li>Readers can start reading and reviewing your book</li>" +
                    "<li>Track engagement and reviews in your publisher dashboard</li>" +
                    "<li>Share your book with your audience</li>" +
                    "</ul>" +
                    "</div>" +

                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<a href='http://localhost:8035/publisher-dashboard' " +
                    "style='display: inline-block; padding: 12px 30px; background: #2c5aa0; color: white; " +
                    "text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px; margin-right: 10px;'>" +
                    "View Publisher Dashboard" +
                    "</a>" +
                    "<a href='http://localhost:8035/books' " +
                    "style='display: inline-block; padding: 12px 30px; background: #28a745; color: white; " +
                    "text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>" +
                    "Browse Books" +
                    "</a>" +
                    "</div>" +

                    "<div style='border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 20px; text-align: center;'>" +
                    "<p style='color: #666; margin-bottom: 10px;'>Thank you for publishing with IntelliRead!</p>" +
                    "<p style='color: #666; margin-bottom: 5px;'>📧 Need assistance? <a href='mailto:noreply.intelliread@gmail.com' style='color: #2c5aa0;'>Contact our support team</a></p>" +
                    "<p style='color: #999; font-size: 12px; margin-top: 20px;'>Happy publishing! 📚✨</p>" +
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
            System.out.println("✅ Book approval email sent to publisher: " + publisher.getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send book approval email to: " + publisher.getEmail());
            e.printStackTrace();
            throw new RuntimeException("Failed to send book approval email", e);
        }
    }

    //  Welcome Email for Regular Users
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

    // 🔹 6. Password Reset Email
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(user.getEmail());
            helper.setSubject("🔐 Password Reset Request - IntelliRead");

            String resetLink = "http://localhost:8035/reset-password?token=" + resetToken;

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<div style='text-align: center; margin-bottom: 30px;'>" +
                    "<img src='cid:logoImage' width='150' alt='IntelliRead Logo' style='margin-bottom: 20px;'>" +
                    "<h1 style='color: #2c5aa0; margin-bottom: 10px;'>Password Reset Request</h1>" +
                    "<p style='color: #666; font-size: 16px;'>Secure your IntelliRead account</p>" +
                    "</div>" +

                    "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;'>" +
                    "<h2 style='color: #2c5aa0; margin-bottom: 15px;'>Hello " + user.getName() + ",</h2>" +
                    "<p style='margin-bottom: 15px;'>We received a request to reset your password for your <strong>IntelliRead</strong> account.</p>" +
                    "<p style='margin-bottom: 15px;'>If you didn't make this request, you can safely ignore this email.</p>" +
                    "</div>" +

                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<a href='" + resetLink + "' " +
                    "style='display: inline-block; padding: 12px 30px; background: #2c5aa0; color: white; " +
                    "text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>" +
                    "Reset Your Password" +
                    "</a>" +
                    "</div>" +

                    "<div style='background: #fff3cd; padding: 15px; border-radius: 5px; margin-bottom: 20px; border-left: 4px solid #ffc107;'>" +
                    "<h4 style='color: #856404; margin-bottom: 10px;'>⚠️ Important Security Notice</h4>" +
                    "<p style='color: #856404; margin-bottom: 5px; font-size: 14px;'>" +
                    "• This link will expire in 24 hours<br>" +
                    "• Do not share this link with anyone<br>" +
                    "• If you didn't request this, please secure your account" +
                    "</p>" +
                    "</div>" +

                    "<div style='border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 20px; text-align: center;'>" +
                    "<p style='color: #666; margin-bottom: 10px;'>Need help? Contact our support team</p>" +
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
            System.out.println("✅ Password reset email sent to: " + user.getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send password reset email to: " + user.getEmail());
            e.printStackTrace();
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    // 🔹 7. Password Reset Success Email
    public void sendPasswordResetSuccessEmail(User user) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(user.getEmail());
            helper.setSubject("✅ Password Successfully Reset - IntelliRead");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<div style='text-align: center; margin-bottom: 30px;'>" +
                    "<img src='cid:logoImage' width='150' alt='IntelliRead Logo' style='margin-bottom: 20px;'>" +
                    "<h1 style='color: #28a745; margin-bottom: 10px;'>Password Reset Successful</h1>" +
                    "<p style='color: #666; font-size: 16px;'>Your account is now secure</p>" +
                    "</div>" +

                    "<div style='background: #d4edda; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #28a745;'>" +
                    "<h2 style='color: #155724; margin-bottom: 15px;'>Hello " + user.getName() + ",</h2>" +
                    "<p style='color: #155724; margin-bottom: 15px;'>Your <strong>IntelliRead</strong> account password has been successfully reset.</p>" +
                    "<p style='color: #155724; margin-bottom: 15px;'>You can now login with your new password.</p>" +
                    "</div>" +

                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<a href='http://localhost:8035/auth/login' " +
                    "style='display: inline-block; padding: 12px 30px; background: #28a745; color: white; " +
                    "text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>" +
                    "Login to Your Account" +
                    "</a>" +
                    "</div>" +

                    "<div style='background: #f8f9fa; padding: 15px; border-radius: 5px; margin-bottom: 20px;'>" +
                    "<h4 style='color: #495057; margin-bottom: 10px;'>🔒 Security Tips</h4>" +
                    "<p style='color: #495057; margin-bottom: 5px; font-size: 14px;'>" +
                    "• Use a strong, unique password<br>" +
                    "• Enable two-factor authentication if available<br>" +
                    "• Never share your password with anyone<br>" +
                    "• Regularly update your password" +
                    "</p>" +
                    "</div>" +

                    "<div style='border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 20px; text-align: center;'>" +
                    "<p style='color: #666; margin-bottom: 10px;'>If you didn't make this change, please contact us immediately</p>" +
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
            System.out.println("✅ Password reset success email sent to: " + user.getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send password reset success email to: " + user.getEmail());
            e.printStackTrace();
        }
    }

    // 🔹 NEW: Password Reset OTP Email (6-digit OTP)
    public void sendPasswordResetOTPEmail(User user, String otp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(user.getEmail());
            helper.setSubject("🔐 Password Reset OTP - IntelliRead");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<div style='text-align: center; margin-bottom: 30px;'>" +
                    "<img src='cid:logoImage' width='150' alt='IntelliRead Logo' style='margin-bottom: 20px;'>" +
                    "<h1 style='color: #2c5aa0; margin-bottom: 10px;'>Password Reset OTP</h1>" +
                    "<p style='color: #666; font-size: 16px;'>Secure your IntelliRead account</p>" +
                    "</div>" +

                    "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;'>" +
                    "<h2 style='color: #2c5aa0; margin-bottom: 15px;'>Hello " + user.getName() + ",</h2>" +
                    "<p style='margin-bottom: 15px;'>We received a request to reset your password for your <strong>IntelliRead</strong> account.</p>" +
                    "<p style='margin-bottom: 15px;'>Use the OTP below to reset your password:</p>" +
                    "</div>" +

                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<div style='display: inline-block; padding: 20px 40px; background: #2c5aa0; color: white; " +
                    "border-radius: 10px; font-weight: bold; font-size: 32px; letter-spacing: 8px;'>" +
                    otp +
                    "</div>" +
                    "</div>" +

                    "<div style='background: #fff3cd; padding: 15px; border-radius: 5px; margin-bottom: 20px; border-left: 4px solid #ffc107;'>" +
                    "<h4 style='color: #856404; margin-bottom: 10px;'>⚠️ Important Security Notice</h4>" +
                    "<p style='color: #856404; margin-bottom: 5px; font-size: 14px;'>" +
                    "• This OTP will expire in 10 minutes<br>" +
                    "• Do not share this OTP with anyone<br>" +
                    "• If you didn't request this, please secure your account" +
                    "</p>" +
                    "</div>" +

                    "<div style='border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 20px; text-align: center;'>" +
                    "<p style='color: #666; margin-bottom: 10px;'>Need help? Contact our support team</p>" +
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
            System.out.println("✅ Password reset OTP email sent to: " + user.getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send password reset OTP email to: " + user.getEmail());
            e.printStackTrace();
            throw new RuntimeException("Failed to send password reset OTP email", e);
        }
    }

    // 🔹 NEW: Book Rejection Email to Publisher
    public void sendBookRejectionEmail(User publisher, Book book, String rejectionReason) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(publisher.getEmail());
            helper.setSubject("❌ Book Submission Rejected - IntelliRead");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<div style='text-align: center; margin-bottom: 30px;'>" +
                    "<img src='cid:logoImage' width='150' alt='IntelliRead Logo' style='margin-bottom: 20px;'>" +
                    "<h1 style='color: #dc3545; margin-bottom: 10px;'>Book Submission Rejected</h1>" +
                    "<p style='color: #666; font-size: 16px;'>Important update about your book submission</p>" +
                    "</div>" +

                    "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;'>" +
                    "<h2 style='color: #dc3545; margin-bottom: 15px;'>Hello " + publisher.getName() + ",</h2>" +
                    "<p style='margin-bottom: 15px;'>We regret to inform you that your book submission has been <strong>rejected</strong> by our admin team.</p>" +
                    "</div>" +

                    "<div style='background: #f8d7da; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #dc3545;'>" +
                    "<h3 style='color: #721c24; margin-bottom: 15px;'>📖 Book Details</h3>" +
                    "<p style='color: #721c24; margin-bottom: 8px;'><strong>Title:</strong> " + book.getTitle() + "</p>" +
                    "<p style='color: #721c24; margin-bottom: 8px;'><strong>Author:</strong> " + book.getAuthor() + "</p>" +
                    "<p style='color: #721c24; margin-bottom: 8px;'><strong>Category:</strong> " + (book.getCategory() != null ? book.getCategory().getCategoryName() : "Not specified") + "</p>" +
                    "<p style='color: #721c24; margin-bottom: 8px;'><strong>Submitted On:</strong> " + (book.getUploadedAt() != null ? book.getUploadedAt().toLocalDate().toString() : "Unknown") + "</p>" +
                    "</div>" +

                    "<div style='background: #fff3cd; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #ffc107;'>" +
                    "<h3 style='color: #856404; margin-bottom: 15px;'>📝 Rejection Reason</h3>" +
                    "<p style='color: #856404; margin-bottom: 15px; font-style: italic;'>" +
                    (rejectionReason != null && !rejectionReason.trim().isEmpty() ?
                            "\"" + rejectionReason + "\"" : "No specific reason provided") +
                    "</p>" +
                    "</div>" +

                    "<div style='background: #d1ecf1; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #17a2b8;'>" +
                    "<h3 style='color: #0c5460; margin-bottom: 15px;'>💡 Next Steps</h3>" +
                    "<p style='color: #0c5460; margin-bottom: 10px;'>" +
                    "• Review the rejection reason carefully<br>" +
                    "• Make necessary improvements to your book<br>" +
                    "• Ensure your content follows our guidelines<br>" +
                    "• You can submit a new version of the book<br>" +
                    "• Contact support if you need clarification" +
                    "</p>" +
                    "</div>" +

                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<a href='http://localhost:8035/publisher-dashboard' " +
                    "style='display: inline-block; padding: 12px 30px; background: #17a2b8; color: white; " +
                    "text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>" +
                    "Go to Publisher Dashboard" +
                    "</a>" +
                    "</div>" +

                    "<div style='border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 20px; text-align: center;'>" +
                    "<p style='color: #666; margin-bottom: 10px;'>Need clarification or have questions?</p>" +
                    "<p style='color: #666; margin-bottom: 5px;'>📧 Email: <a href='mailto:noreply.intelliread@gmail.com' style='color: #2c5aa0;'>noreply.intelliread@gmail.com</a></p>" +
                    "<p style='color: #999; font-size: 12px; margin-top: 20px;'>This is an automated message. Please do not reply to this email.</p>" +
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
            System.out.println("✅ Book rejection email sent to publisher: " + publisher.getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send book rejection email to: " + publisher.getEmail());
            e.printStackTrace();
            throw new RuntimeException("Failed to send book rejection email", e);
        }
    }
    // Suggestion Approval Email with admin notes
    public void sendSuggestionApprovalEmail(User user, Suggestion suggestion) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(user.getEmail());
            helper.setSubject("Suggestion Approved - IntelliRead");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<div style='text-align: center; margin-bottom: 30px;'>" +
                    "<h1 style='color: #28a745; margin-bottom: 10px;'>Suggestion Approved!</h1>" +
                    "<p style='color: #666; font-size: 16px;'>Your book suggestion has been approved</p>" +
                    "</div>" +

                    "<div style='background: #d4edda; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #28a745;'>" +
                    "<h2 style='color: #155724; margin-bottom: 15px;'>Great News " + user.getName() + "!</h2>" +
                    "<p style='color: #155724; margin-bottom: 15px;'>Your book suggestion <strong>\"" + suggestion.getSuggestedTitle() + "\"</strong> has been <strong>approved</strong> by our admin team.</p>" +
                    "</div>" +

                    "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;'>" +
                    "<h3 style='color: #2c5aa0; margin-bottom: 15px;'>Suggestion Details:</h3>" +
                    "<p style='color: #495057; margin-bottom: 8px;'><strong>Title:</strong> " + suggestion.getSuggestedTitle() + "</p>" +
                    "<p style='color: #495057; margin-bottom: 8px;'><strong>Author:</strong> " + (suggestion.getAuthor() != null ? suggestion.getAuthor() : "Not specified") + "</p>" +
                    "<p style='color: #495057; margin-bottom: 8px;'><strong>Status:</strong> <span style='color: #28a745; font-weight: bold;'>APPROVED</span></p>" +
                    "</div>" +

                    (suggestion.getAdminNotes() != null && !suggestion.getAdminNotes().isEmpty() ?
                            "<div style='background: #e7f3ff; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #2c5aa0;'>" +
                                    "<h3 style='color: #2c5aa0; margin-bottom: 15px;'>Admin Notes:</h3>" +
                                    "<p style='color: #2c5aa0; margin-bottom: 15px; font-style: italic;'>\"" + suggestion.getAdminNotes() + "\"</p>" +
                                    "</div>" : "") +

                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<a href='http://localhost:8035/books' " +
                    "style='display: inline-block; padding: 12px 30px; background: #28a745; color: white; " +
                    "text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>" +
                    "Browse Books" +
                    "</a>" +
                    "</div>" +

                    "<div style='border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 20px; text-align: center;'>" +
                    "<p style='color: #666; margin-bottom: 10px;'>Thank you for your valuable suggestion!</p>" +
                    "<p style='color: #999; font-size: 12px; margin-top: 20px;'>Happy reading!</p>" +
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
            System.out.println("Suggestion approval email sent to: " + user.getEmail());

        } catch (MessagingException e) {
            System.err.println("Failed to send suggestion approval email to: " + user.getEmail());
            e.printStackTrace();
        }
    }

    // Suggestion Rejection Email with admin notes
    public void sendSuggestionRejectionEmail(User user, Suggestion suggestion) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(user.getEmail());
            helper.setSubject("Suggestion Not Approved - IntelliRead");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<div style='text-align: center; margin-bottom: 30px;'>" +
                    "<h1 style='color: #dc3545; margin-bottom: 10px;'>Suggestion Update</h1>" +
                    "<p style='color: #666; font-size: 16px;'>Regarding your book suggestion</p>" +
                    "</div>" +

                    "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;'>" +
                    "<h2 style='color: #dc3545; margin-bottom: 15px;'>Hello " + user.getName() + ",</h2>" +
                    "<p style='margin-bottom: 15px;'>Thank you for your book suggestion. After careful review, we regret to inform you that your suggestion <strong>\"" + suggestion.getSuggestedTitle() + "\"</strong> could not be approved at this time.</p>" +
                    "</div>" +

                    "<div style='background: #fff3cd; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #ffc107;'>" +
                    "<h3 style='color: #856404; margin-bottom: 15px;'>Admin Notes:</h3>" +
                    "<p style='color: #856404; margin-bottom: 15px; font-style: italic;'>" +
                    (suggestion.getAdminNotes() != null && !suggestion.getAdminNotes().isEmpty() ?
                            "\"" + suggestion.getAdminNotes() + "\"" : "No specific reason provided") +
                    "</p>" +
                    "</div>" +

                    "<div style='background: #d1ecf1; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #17a2b8;'>" +
                    "<h3 style='color: #0c5460; margin-bottom: 15px;'>Keep Suggesting!</h3>" +
                    "<p style='color: #0c5460; margin-bottom: 10px;'>" +
                    "• We appreciate all suggestions from our community<br>" +
                    "• You can submit new suggestions anytime<br>" +
                    "• Your feedback helps us improve our library<br>" +
                    "</p>" +
                    "</div>" +

                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<a href='http://localhost:8035/books' " +
                    "style='display: inline-block; padding: 12px 30px; background: #17a2b8; color: white; " +
                    "text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>" +
                    "Browse Available Books" +
                    "</a>" +
                    "</div>" +

                    "<div style='border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 20px; text-align: center;'>" +
                    "<p style='color: #666; margin-bottom: 10px;'>Thank you for being part of IntelliRead community!</p>" +
                    "<p style='color: #999; font-size: 12px; margin-top: 20px;'>We value your input!</p>" +
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
            System.out.println("Suggestion rejection email sent to: " + user.getEmail());

        } catch (MessagingException e) {
            System.err.println("Failed to send suggestion rejection email to: " + user.getEmail());
            e.printStackTrace();
        }
    }
    // ✅ ADD THIS METHOD to EmailService.java
    public void sendPublisherInterestEmail(User user, Suggestion suggestion, User publisher) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(FROM_EMAIL);
            helper.setTo(user.getEmail());
            helper.setSubject("🎯 Publisher Interested in Your Suggestion - IntelliRead");

            String content = "<html>" +
                    "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
                    "<div style='text-align: center; margin-bottom: 30px;'>" +
                    "<h1 style='color: #4a90e2; margin-bottom: 10px;'>Great News! 🎉</h1>" +
                    "<p style='color: #666; font-size: 16px;'>A publisher is interested in your book suggestion</p>" +
                    "</div>" +

                    "<div style='background: #e6f0ff; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #4a90e2;'>" +
                    "<h2 style='color: #2c5aa0; margin-bottom: 15px;'>Hello " + user.getName() + "!</h2>" +
                    "<p style='color: #2c5aa0; margin-bottom: 15px;'>We have exciting news! A publisher has shown interest in your book suggestion.</p>" +
                    "</div>" +

                    "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;'>" +
                    "<h3 style='color: #2c5aa0; margin-bottom: 15px;'>📖 Suggestion Details:</h3>" +
                    "<p style='color: #495057; margin-bottom: 8px;'><strong>Title:</strong> " + suggestion.getSuggestedTitle() + "</p>" +
                    "<p style='color: #495057; margin-bottom: 8px;'><strong>Author:</strong> " + (suggestion.getAuthor() != null ? suggestion.getAuthor() : "Not specified") + "</p>" +
                    "<p style='color: #495057; margin-bottom: 8px;'><strong>Your Reason:</strong> " + (suggestion.getSuggestionReason() != null ? suggestion.getSuggestionReason() : "No reason provided") + "</p>" +
                    "</div>" +

                    "<div style='background: #d4edda; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #28a745;'>" +
                    "<h3 style='color: #155724; margin-bottom: 15px;'>👨‍💼 Publisher Information:</h3>" +
                    "<p style='color: #155724; margin-bottom: 8px;'><strong>Publisher:</strong> " + publisher.getName() + "</p>" +
                    "<p style='color: #155724; margin-bottom: 8px;'><strong>Email:</strong> " + publisher.getEmail() + "</p>" +
                    "</div>" +

                    "<div style='background: #e7f3ff; padding: 20px; border-radius: 8px; margin-bottom: 20px; border-left: 4px solid #2c5aa0;'>" +
                    "<h3 style='color: #2c5aa0; margin-bottom: 15px;'>🚀 What Happens Next?</h3>" +
                    "<ul style='color: #495057; margin-bottom: 15px; padding-left: 20px;'>" +
                    "<li>The publisher may upload this book to our platform</li>" +
                    "<li>Once uploaded, our admin team will review the book</li>" +
                    "<li>If approved, the book will be available for all readers</li>" +
                    "<li>You'll be notified when the book is published</li>" +
                    "</ul>" +
                    "</div>" +

                    "<div style='text-align: center; margin: 30px 0;'>" +
                    "<a href='http://localhost:8035/books' " +
                    "style='display: inline-block; padding: 12px 30px; background: #4a90e2; color: white; " +
                    "text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;'>" +
                    "Browse Books" +
                    "</a>" +
                    "</div>" +

                    "<div style='border-top: 1px solid #e0e0e0; padding-top: 20px; margin-top: 20px; text-align: center;'>" +
                    "<p style='color: #666; margin-bottom: 10px;'>Thank you for your valuable suggestion!</p>" +
                    "<p style='color: #999; font-size: 12px; margin-top: 20px;'>Your input helps us grow our library! 📚</p>" +
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
            System.out.println("✅ Publisher interest email sent to: " + user.getEmail());

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send publisher interest email to: " + user.getEmail());
            e.printStackTrace();
        }
    }


}