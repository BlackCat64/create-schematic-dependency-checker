package net.blackcat64.create_schematic_dependency_checker.controllers;

import net.blackcat64.create_schematic_dependency_checker.dtos.SchematicDependencies;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RestApiController {

    @GetMapping("/schematic")
    public ResponseEntity<SchematicDependencies> getDependencies() {
        List<String> list = new ArrayList<>(List.of("hello1", "hello2", "hello3"));
        var dependencies = new SchematicDependencies("test", LocalDateTime.now(), list);
//        SchematicDependencies dependencies = null;

        if (dependencies == null) {
            return ResponseEntity.notFound().build();
        }
        else {
            return ResponseEntity.ok(dependencies);
        }
    }
}
