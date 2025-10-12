package net.blackcat64.create_schematic_dependency_checker.controllers;

import net.blackcat64.create_schematic_dependency_checker.CreateSchematicDependencyChecker;
import net.blackcat64.create_schematic_dependency_checker.dtos.SchematicDependencies;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RestApiController {

    @PostMapping("/schematic")
    public ResponseEntity<SchematicDependencies> getDependencies(@RequestParam MultipartFile file) {
        try {
            // store file temporarily
            File tempFile = File.createTempFile("uploaded-", ".nbt");
            file.transferTo(tempFile);

            String schematicName = file.getOriginalFilename();

            // call existing method to get dependencies
            List<String> dependencies = CreateSchematicDependencyChecker.getAllSchematicDependencies(tempFile);

            // package in DTO
            SchematicDependencies result = new SchematicDependencies(
                    schematicName,
                    LocalDateTime.now(),
                    dependencies
            );

            // return to client
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
