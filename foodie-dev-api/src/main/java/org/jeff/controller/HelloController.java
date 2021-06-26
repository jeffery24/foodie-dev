package org.jeff.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//@Controller
@RestController
public class HelloController {

    @GetMapping("/hello")
    public Object hello() {
        return "Hello World~";
    }
}
