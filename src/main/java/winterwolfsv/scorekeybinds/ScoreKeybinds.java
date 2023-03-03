package winterwolfsv.scorekeybinds;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Scanner;

public class ScoreKeybinds implements ModInitializer {
    public static void createConfigFile() {
        try {
            InputStream jsonFile = ScoreKeybinds.class.getClassLoader().getResourceAsStream("scorekeybinds.json");
            Path configDir = FabricLoader.getInstance().getConfigDir();
            File configFile = new File(configDir + "\\scorekeybinds.json");
            configFile.createNewFile();
            System.out.println("Config file created for mod: ScoreKeybinds.");
            FileWriter fileWriter = new FileWriter(configFile);
            assert jsonFile != null;
            fileWriter.write(new Scanner(jsonFile, "UTF-8").useDelimiter("\\A").next());
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onInitialize() {
        if (!new File(FabricLoader.getInstance().getConfigDir() + "\\scorekeybinds.json").exists()) {
            createConfigFile();
        }
    }
}


