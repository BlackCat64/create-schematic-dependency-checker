import de.pauleff.api.ICompoundTag;
import de.pauleff.api.IListTag;
import de.pauleff.api.ITag;
import de.pauleff.api.NBTFileFactory;
import de.pauleff.core.Tag;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

public class CreateSchematicDependencyChecker {
    private static final boolean DEBUG = false;

    public static void main(String[] args) {
        Frame frame = new Frame();
        try {
            FileDialog dialog = new FileDialog(frame, "Select a Create schematic NBT file", FileDialog.LOAD);
            dialog.setDirectory(System.getProperty("user.home") + "\\AppData\\Roaming\\.minecraft\\schematics"); // Open the schematics folder in the default minecraft instance
            dialog.setFilenameFilter((dir, name) -> {
                return name.toLowerCase().endsWith(".nbt"); // Filter the search for only NBT files
            });
            dialog.setVisible(true);
            dialog.requestFocus();

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
            ICompoundTag nbtRoot = NBTFileFactory.readNBTFile(schematicFile);
            if (DEBUG)
                System.out.println("__________________________________________________\n");

            Set<String> modIDs = getSchematicBlockDependencies(nbtRoot); // search the schematic's regular blocks
            if (modIDs.isEmpty()) {
                throw new Exception("Invalid NBT format - No blocks found!");
            }

            if (DEBUG)
                System.out.println("__________________________________________________\n");
            Set<String> modIDsInCopycats = getSchematicFramedCopycatDependencies(nbtRoot); // then search for blocks inside framed blocks and copycats
            modIDs.addAll(modIDsInCopycats);

            if (DEBUG)
                System.out.println("=================================================\n");

            System.out.println("Processing complete. Found " + modIDs.size() + " mods.\n");
            System.out.println("<<<DEPENDENCIES>>> of " + schematicFile.getName() + ":");
            List<String> sortedIDs = new ArrayList<>(modIDs).stream().sorted().toList(); // sort and output the IDs of the mods used in the schematic
            for (String modID : sortedIDs) {
                System.out.println(modID);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            frame.dispose(); // Close the file chooser when the program finishes
        }
    }

    public static Set<String> getSchematicBlockDependencies(ICompoundTag schematicNbtRoot) throws Exception {
        IListTag blockPalette = schematicNbtRoot.getList("palette");
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
                String modID = getModIDFromTag(blockTag);

                if (modID != null && !modID.equals("minecraft")) // skip vanilla blocks - minecraft doesn't count as a dependency
                    modIDs.add(modID); // add the mod ID to the set
            }
            else System.err.println("Found block entry which is not a CompoundTag. Skipping..." );
        }

        return modIDs;
    }

    public static Set<String> getSchematicFramedCopycatDependencies(ICompoundTag schematicNbtRoot) throws Exception {
        IListTag blocksTag = schematicNbtRoot.getList("blocks");
        if (blocksTag == null) {
            throw new Exception("Invalid NBT format - Could not find blocks tag.");
        }

        List<Tag<?>> blocks = blocksTag.getData(); // Get the list out of the IListTag
        if (blocks == null || blocks.isEmpty()) {
            throw new Exception("Invalid NBT format - No blocks found.");
        }

        // for each Framed Block / Copycat Block, retrieve the block inside
        Set<String> modIDs = new HashSet<>();
        System.out.println("Processing dependencies of blocks inside Framed Blocks / Copycats...");
        for (Tag<?> block : blocks) {
            if (block instanceof ICompoundTag blockTag) {
                if (!blockTag.hasTag("nbt")) // ignore blocks without extra NBT data
                    continue;

                // use a list filter to get the 'id' tag from the nbt tag because there is another 'id' tag further down the tree which gets returned otherwise
                ICompoundTag blockNbtTag = blockTag.getCompound("nbt");
                List<Tag<?>> blockNbtTags = blockNbtTag.getData();
                ITag<?> blockNbtIDTag = blockNbtTags.stream().filter(tag -> tag.getName().equals("id")).findFirst().orElse(null);
                if (blockNbtIDTag == null)
                    continue;

                Object blockNbtIDObj = blockNbtIDTag.getData();
                if (!(blockNbtIDObj instanceof String blockNbtID)) {
                    System.err.println("Found block NBT ID which is not a String! Skipping...");
                    continue;
                }

                if (DEBUG) {
                    System.out.println("=================================================");
                    System.out.println(blockNbtID);
                }

                if (blockNbtID.isEmpty())
                    continue;
                else if (blockNbtID.startsWith("framedblocks")) {
                    ICompoundTag camo1 = blockNbtTag.getCompound("camo");
                    String camo1ID = getFramedBlockCamoID(camo1);
                    if (camo1ID != null && !camo1ID.equals("minecraft"))
                        modIDs.add(camo1ID);

                    ICompoundTag camo2 = blockNbtTag.getCompound("camo_two");
                    String camo2ID = getFramedBlockCamoID(camo2);
                    if (camo2ID != null && !camo2ID.equals("minecraft"))
                        modIDs.add(camo2ID);
                }
                else if (blockNbtID.contains("copycat")) {
                    ICompoundTag copycatMaterialTag = blockNbtTag.getCompound("Material"); // single-state copycat blocks have this tag
                    if (copycatMaterialTag != null) {
                        String modID = getModIDFromTag(copycatMaterialTag);

                        if (modID != null && !modID.equals("minecraft"))
                            modIDs.add(modID);
                    }
                    else {
                        copycatMaterialTag = blockNbtTag.getCompound("material_data"); // handle multi-state copycat blocks
                        if (copycatMaterialTag == null) {
                            System.err.println("Could not find Copycat Block material tag! Skipping...");
                            continue;
                        }
                        Set<String> copycatModIDs = getMultiStateCopycatBlockModIDs(copycatMaterialTag);
                        if (copycatModIDs != null) {
                            modIDs.addAll(copycatModIDs);
                        }
                    }
                }
            }
            else System.err.println("Found block entry which is not a CompoundTag. Skipping...");
        }

        return modIDs;
    }

    /**
     * Retrieves the mod ID of the block inside a framed block.
     *
     * @param camoTag - The 'camo' or 'camo_two' tag inside a Framed Block's NBT data.
     * @return - The mod ID of the block
     */
    private static String getFramedBlockCamoID(ICompoundTag camoTag) {
        if (camoTag == null)
            return null;

        ICompoundTag camoStateTag = camoTag.getCompound("state");
        if (camoStateTag == null) { // if state tag is missing, the framed block is empty
            return null;
        }
        return getModIDFromTag(camoStateTag);
    }

    /**
     * Retrieves the mod IDs of the blocks used in a copycat block
     *
     * @param copycatMaterialTag - The 'material_data' Compound tag from a copycat block's NBT data
     * @return - A set of mod IDs of the blocks inside the copycat block
     */
    private static Set<String> getMultiStateCopycatBlockModIDs(ICompoundTag copycatMaterialTag) {
        if (copycatMaterialTag == null)
            return null;

        List<ICompoundTag> materialTags = new ArrayList<>();
        String[] materialTagNames = new String[] {
                "bottom", "top", "up", "down", "left", "right", // others, just in case
                "top_right", "top_left", "bottom_right", "bottom_left", // byte panel
                "north", "south", "east", "west", // box / catwalk / board
                "top_southeast", "top_northeast", "top_southwest", "top_northwest", // byte
                "bottom_southeast", "bottom_northeast", "bottom_southwest", "bottom_northwest",
                "positive_layers", "negative_layers" // half layers
        };
        Set<String> modIDs = new HashSet<>();

        // find all the material tags of this multi-state copycat block, from the list of possible tag names
        for (String materialTagName : materialTagNames) {
            ICompoundTag materialTag = copycatMaterialTag.getCompound(materialTagName);
            if (materialTag != null) {
                materialTags.add(materialTag);
            }
        }

        // extract mod IDs from each material tag
        for (ICompoundTag materialTag : materialTags) {
            ICompoundTag materialStateTag = materialTag.getCompound("material");
            if (materialStateTag == null) {
                System.err.println("Could not find block in tag '" + materialTag.getName() + "' of Copycat Block!");
            }
            else {
                if (DEBUG)
                    System.out.print(materialTag.getName() + ": ");

                String modID = getModIDFromTag(materialStateTag); // if a copycat block is empty, the mod ID will just be 'create', which is already a given anyway
                if (modID != null && !modID.equals("minecraft"))
                    modIDs.add(modID);
            }
        }

        return modIDs;
    }

    /**
     * Retrieves only the mod ID from an NBT Compound tag containing a block registry ID in a String tag
     *
     * @param tag - Compound tag containing a String tag called 'Name' which contains a block registry ID
     * @return - The mod ID of the block
     */
    private static String getModIDFromTag(ICompoundTag tag) {
        if (tag == null)
            return null;

        String blockID = tag.getString("Name");
        if (blockID == null || blockID.isEmpty()) {
            System.err.println("Found empty block ID! Skipping...");
            return null;
        }

        String[] tokens = blockID.split(":");
        if (tokens.length != 2 || tokens[0].isEmpty() || tokens[1].isEmpty()) { // block registry ID must contain a colon separating the mod ID from the block ID
            System.err.println("Found invalid block ID! Skipping...");
            return null;
        }

        if (DEBUG)
            System.out.println(blockID);

        return tokens[0];
    }
}