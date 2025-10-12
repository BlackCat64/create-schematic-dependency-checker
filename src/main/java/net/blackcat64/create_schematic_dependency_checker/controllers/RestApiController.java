package net.blackcat64.create_schematic_dependency_checker.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {

    @RequestMapping("/api/hello")
    public String hello() {
        return "Hello World!";
    }
}
