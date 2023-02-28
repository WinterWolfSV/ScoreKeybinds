package winterwolfsv.scorekeybinds;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

import org.json.*;

public class ScoreKeybinds implements ModInitializer {
    @Override
    public void onInitialize() {
        if(new File(FabricLoader.getInstance().getConfigDir() + "\\scorekeybinds.json").exists()){
            System.out.println("Config directory exists");
        } else {
            System.out.println("Config directory does not exist");
            try {
                InputStream jsonFile = getClass().getClassLoader().getResourceAsStream("scorekeybinds.json");
                Path configDir = FabricLoader.getInstance().getConfigDir();
                File configFile = new File(configDir + "\\scorekeybinds.json");
                configFile.createNewFile();
                System.out.println("Config file created");
                FileWriter fileWriter = new FileWriter(configFile);
                fileWriter.write(new Scanner(jsonFile, "UTF-8").useDelimiter("\\A").next());
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public JSONObject GetDataFromFile(String filename){
        InputStream inputStream = getClass().getResourceAsStream(filename);
        assert inputStream != null;
        Scanner scanner = new Scanner(inputStream, "UTF-8");
        String jsonString = scanner.useDelimiter("\\A").next();

        return new JSONObject(jsonString);
    }
}


