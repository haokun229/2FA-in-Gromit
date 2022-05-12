package com.oceangromits.firmware.service;

import com.oceangromits.firmware.controller.TwoFactorServiceController;
import com.oceangromits.firmware.exceptions.GromitsException;
import com.oceangromits.firmware.model.Client;
import com.oceangromits.firmware.model.Role;
import com.oceangromits.firmware.repository.ClientRepository;
import com.oceangromits.firmware.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ClientService { //service for handling the clients sign in. admin roles and video stream tokens etc
    private final ClientRepository clientRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    @Autowired
    ServletContext servletContext;

    @Autowired
    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder,
                         JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public String signIn(String username, String password) {
        try {
            String user = (String) servletContext.getAttribute("username");
            String code = (String) servletContext.getAttribute("code");
            System.out.println(user+"  user+code    "+code);
            if(code != null){
                if(user.equals(username)&&code.equals(password)) {
                    servletContext.setAttribute("code",null);
                    return jwtTokenProvider.createToken(username, clientRepository.findByName(username).getRoles());
                }else {
                    System.out.println("incorrect code");
                    throw new GromitsException("Invalid verification code supplied", HttpStatus.UNPROCESSABLE_ENTITY);
                }
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if(clientRepository.findByName(username).getRoles().contains(Role.ROLE_ADMIN)) {
                TwoFactorServiceController towFactor = new TwoFactorServiceController();
                try {
                    towFactor.sendEmail(username,"oceangromits2021@gmail.com",servletContext);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                return "step-2";
            } else return jwtTokenProvider.createToken(username, clientRepository.findByName(username).getRoles());
        } catch (AuthenticationException e) {
            throw new GromitsException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }



    public String createAdmin(Client client) {
        String name = client.getName();
        String passw = client.getPassword();

        boolean validName = verifyDetails(name, false);
        boolean validPass = verifyDetails(passw, true);

        if (!validName || !validPass) {
            throw new GromitsException("Invalid username/password", HttpStatus.UNPROCESSABLE_ENTITY);
        } else {
            client.setPassword(passwordEncoder.encode(client.getPassword()));
            client.setRoles(Arrays.asList(Role.ROLE_VIDEO, Role.ROLE_ADMIN, Role.ROLE_CONNECT));
            clientRepository.save(client);//commenting out fixes test
    
            return jwtTokenProvider.createToken(client.getName(), client.getRoles());
        }
    }

    private boolean verifyDetails(String data, boolean isPass) {
        String regex;
        if (isPass) {
          regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*?])(?=.{8,})";
        } else {
          regex = "^[a-zA-Z0-9](_(?!(\\.|_))|\\.(?![_.])|[a-zA-Z0-9]){8,20}[a-zA-Z0-9]$";
        }
      
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
      
        return matcher.find();
      }

    public String genClientVideoToken(String clientID) {
        List<Role> roles = Arrays.asList(Role.ROLE_VIDEO, Role.ROLE_CONNECT);
        return jwtTokenProvider.createToken(clientID, roles);
    }

    public String genBasicToken(String clientID) {
        List<Role> roles = Collections.singletonList(Role.ROLE_CONNECT);
        return jwtTokenProvider.createToken(clientID, roles);
    }

    public void resetServerDangerously() {
        clientRepository.deleteAll(); // lol
    } //risky business ~Gromits2
}
