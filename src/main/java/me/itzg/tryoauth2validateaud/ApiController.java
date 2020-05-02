package me.itzg.tryoauth2validateaud;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
  @GetMapping("/greeting")
  public Map<String,String> greet() {
    return Map.of("message", "Hello");
  }
}
