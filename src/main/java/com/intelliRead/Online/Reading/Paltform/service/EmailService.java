package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendAdminApprovalRequest(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("original-admin@example.com"); // original admin email
        message.setSubject("New Admin Registration Approval");
        message.setText("User " + user.getName() + " is trying to register as ADMIN.\n"
                + "Email: " + user.getEmail() + "\n"
                + "Click to approve: http://localhost:8048/admin/approve/" + user.getId() + "\n"
                + "Click to reject: http://localhost:8048/admin/reject/" + user.getId());

        mailSender.send(message);
    }

    public void sendAdminApproved(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Admin Registration Approved");
        message.setText("Congratulations! Your ADMIN registration has been approved.");
        mailSender.send(message);
    }

    public void sendAdminRejected(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Admin Registration Rejected");
        message.setText("Sorry, your ADMIN registration request has been rejected.");
        mailSender.send(message);
    }
}
