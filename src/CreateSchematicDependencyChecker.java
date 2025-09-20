import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;

public class CreateSchematicDependencyChecker {
    public static void main(String[] args) {
        Frame frame = new Frame();

        FileDialog dialog = new FileDialog(frame, "Select a Create schematic NBT file", FileDialog.LOAD);
        dialog.setDirectory(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft\\schematics"); // Open the schematics folder in the default minecraft instance
        dialog.setFilenameFilter((dir, name) -> {
            return name.toLowerCase().endsWith(".nbt"); // Filter the search for only NBT files
        });
        dialog.setVisible(true);

        String directory = dialog.getDirectory();
        String file = dialog.getFile();

        if (file != null) {
            File selectedFile = new File(directory, file);
            System.out.println("Schematic: " + selectedFile.getAbsolutePath());
        }
        else {
            System.err.println("No file selected!");
        }

        frame.dispose(); // Close the file chooser when the program finishes
    }
}