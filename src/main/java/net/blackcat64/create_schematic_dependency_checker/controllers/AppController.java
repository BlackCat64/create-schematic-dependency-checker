package net.blackcat64.create_schematic_dependency_checker.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {
    @Value("${spring.application.name}")
    private String appName;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("name", "Danny");
        return "index";
    }
}
