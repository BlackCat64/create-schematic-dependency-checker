import de.pauleff.api.ICompoundTag;
import de.pauleff.api.IListTag;
import de.pauleff.api.NBTFileFactory;
import de.pauleff.core.Tag;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateSchematicDependencyChecker {
    public static void main(String[] args) {
        Frame frame = new Frame();
        try {
            FileDialog dialog = new FileDialog(frame, "Select a Create schematic NBT file", FileDialog.LOAD);
            dialog.setDirectory(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft\\schematics"); // Open the schematics folder in the default minecraft instance
            dialog.setFilenameFilter((dir, name) -> {
                return name.toLowerCase().endsWith(".nbt"); // Filter the search for only NBT files
            });
            dialog.setVisible(true);

            String directory = dialog.getDirectory();
            String file = dialog.getFile();

            if (file == null) {
                throw new FileNotFoundException("No file selected!");
            }
            else if (!file.toLowerCase().endsWith(".nbt")) {
                throw new IllegalArgumentException("File is not an NBT file!");
            }

            File schematicFile = new File(directory, file);
            System.out.println("Schematic: " + schematicFile.getAbsolutePath());

            ICompoundTag root = NBTFileFactory.readNBTFile(schematicFile);
            IListTag blockPalette = root.getList("palette");
            if (blockPalette == null) {
                throw new Exception("Invalid NBT format - Could not find block palette.");
            }

            List<Tag<?>> blocks = blockPalette.getData(); // Get the list out of the IListTag
            if (blocks == null || blocks.isEmpty()) {
                throw new Exception("Invalid NBT format - No blocks found.");
            }

            // for each block tag, get the Name string value and extract the mod ID into a set
            Set<String> modIDs = new HashSet<>();
            System.out.println("Processing schematic dependencies...");
            for (Tag<?> block : blocks) {
                if (block instanceof ICompoundTag blockTag) {
                    String blockID = blockTag.getString("Name");
                    if (blockID == null || blockID.isEmpty()) {
                        System.err.println("Found empty block ID! Skipping...");
                        continue;
                    }
                    String[] tokens = blockID.split(":");
                    if (tokens.length != 2) {
                        System.err.println("Found invalid block ID! Skipping..."); // block registry ID must contain a colon separating the mod ID from the block ID
                        continue;
                    }

                    modIDs.add(tokens[0]); // add the mod ID to the set
                }
                else System.err.println("Found block entry which is not a CompoundTag. Skipping..." );
            }

            if (modIDs.isEmpty()) {
                throw new Exception("Invalid NBT format - No blocks found!");
            }
            else {
                System.out.println("Processing complete. Found " + modIDs.size() + " mods:\n");
                System.out.println("=====DEPENDENCIES======");
                for (String modID : modIDs) {
                    System.out.println(modID);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            frame.dispose(); // Close the file chooser when the program finishes
        }
    }
}