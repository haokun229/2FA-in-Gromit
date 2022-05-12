package com.oceangromits.firmware.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private static final String email = "oceangromits2021@gmail.com";
  private static final String password = "Oceangromits";

  public boolean sendEmail(String emailid, String code) throws AddressException, MessagingException {
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");
    Session session = Session.getInstance(props,
      new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(email, password);
        }
      }
    );

    MimeMessage message = new MimeMessage(session);
    message.setFrom(new InternetAddress(email));
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailid));

    message.setSubject("test");
    message.setText("test "+code);

    Transport.send(message);
    return true;
  }
}