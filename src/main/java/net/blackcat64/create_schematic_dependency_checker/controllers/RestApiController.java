package net.blackcat64.create_schematic_dependency_checker.controllers;

import net.blackcat64.create_schematic_dependency_checker.CreateSchematicDependencyChecker;
import net.blackcat64.create_schematic_dependency_checker.exceptions.InvalidNbtException;
import net.blackcat64.create_schematic_dependency_checker.dtos.SchematicDependencies;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://blackcat64.github.io")
public class RestApiController {

    @PostMapping("/schematic")
    public ResponseEntity<SchematicDependencies> uploadSchematic(@RequestParam MultipartFile file) throws IOException, InvalidNbtException {
            if (file == null || file.getOriginalFilename() == null || !file.getOriginalFilename().toLowerCase().endsWith(".nbt")) {
                throw new InvalidNbtException("File is not an NBT file!");
            }

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
    }
}
