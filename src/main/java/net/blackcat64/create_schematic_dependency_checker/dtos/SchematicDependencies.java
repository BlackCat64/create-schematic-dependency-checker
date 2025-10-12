package net.blackcat64.create_schematic_dependency_checker.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Basic class representing a JSON object of a Schematic and its dependencies
 * Data Transfer Object - used to send data from server to client
 */
public class SchematicDependencies {
    private String schematicName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private List<String> dependencies;

    public SchematicDependencies(String schematicName, LocalDateTime timestamp, List<String> dependencies) {
        this.schematicName = schematicName;
        this.timestamp = timestamp;
        this.dependencies = dependencies;
    }

    public String getSchematicName() {
        return schematicName;
    }
    public void setSchematicName(String schematicName) {
        this.schematicName = schematicName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getDependencies() {
        return dependencies;
    }
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }
}
