package com.bc.demoproj;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  @GetMapping("/greetings")
  public String index() {
    return "Greetings from Spring Boot!";
  }

}
