package net.blackcat64.create_schematic_dependency_checker.controllers;

import net.blackcat64.create_schematic_dependency_checker.SchematicDependencies;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RestApiController {

    @RequestMapping("/api/get")
    public SchematicDependencies getDependencies() {
        List<String> list = new ArrayList<>(List.of("hello1", "hello2", "hello3"));
        return new SchematicDependencies("test", list);
    }
}
