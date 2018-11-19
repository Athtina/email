package com.self.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author yanli.zhang
 * @dateTime 2018/11/18 17:27
 */
@RestController
public class helloController {
  @RequestMapping("/hello")
  public String index() {
    return "Hello World";
  }
}