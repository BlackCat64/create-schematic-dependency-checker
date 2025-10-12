package net.blackcat64.create_schematic_dependency_checker;

import java.util.List;

/**
 * Basic class representing a JSON object of a Schematic and its dependencies
 */
public class SchematicDependencies {
    private String schematicName;
    private List<String> dependencies;

    public SchematicDependencies(String schematicName, List<String> dependencies) {
        this.schematicName = schematicName;
        this.dependencies = dependencies;
    }

    public String getSchematicName() {
        return schematicName;
    }
    public void setSchematicName(String schematicName) {
        this.schematicName = schematicName;
    }

    public List<String> getDependencies() {
        return dependencies;
    }
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }
}
