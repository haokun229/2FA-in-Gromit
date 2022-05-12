package com.oceangromits.firmware.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.ContextLoader;

import javax.servlet.ServletContext;

@Repository
public class DAOService {
  @Autowired
  ServletContext servletContext;

  public void update2FAProperties(String username, String code,ServletContext servletContext) {
    servletContext.setAttribute("username",username);
    servletContext.setAttribute("code",code);
  }
}
