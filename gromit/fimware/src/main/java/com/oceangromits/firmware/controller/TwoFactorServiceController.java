package com.oceangromits.firmware.controller;

import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.oceangromits.firmware.service.DAOService;
import com.oceangromits.firmware.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwoFactorServiceController {

  @Autowired
  EmailService emailService;
  @Autowired
  DAOService daoService;

  public ResponseEntity<Object> sendEmail(String username, String emailid, ServletContext servletContext) throws AddressException, MessagingException {
    String code = String.valueOf(new Random().nextInt(9999)+1000);
    emailService = new EmailService();
    daoService = new DAOService();
    emailService.sendEmail(emailid, code);
    daoService.update2FAProperties(username, code,servletContext);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
