package winterwolfsv.scorekeybinds.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import winterwolfsv.scorekeybinds.ScoreKeybinds;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;


public class ScoreKeybindsClient implements ClientModInitializer {

    private JSONArray getConfigData() {
        String path = FabricLoader.getInstance().getConfigDir() + "\\scorekeybinds.json";
        if (new File(path).exists()) {
            try {
                InputStream inputStream = Files.newInputStream(Paths.get(path));
                Scanner scanner = new Scanner(inputStream, "UTF-8");
                String jsonString = scanner.useDelimiter("\\A").next();
                return new JSONArray(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    private final KeyBinding[] keyBindings = new KeyBinding[Objects.requireNonNull(getConfigData()).length()];

    private void createKeybinds() {
        try {
            int i = 0;
            if (getConfigData() != null) {
                for (Object obj : getConfigData()) {
                    if (obj instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) obj;
                        String stringKey = jsonObject.getString("default_key").toLowerCase();
                        int key = -1;
                        if (!stringKey.isEmpty() && stringKey.charAt(0) != ' ') {
                            key = stringKey.charAt(0) - 32;
                        }
                        String command = jsonObject.getString("command");
                        String name = jsonObject.getString("name");
                        System.out.println("Creating keybind: '" + name + "' With keybind: '" + stringKey + "' For command: '" + command + "'");
                        keyBindings[i] = KeyBindingHelper.registerKeyBinding(new KeyBinding(name, InputUtil.Type.KEYSYM, key, "Score Keybindings"));
                    }
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String path = FabricLoader.getInstance().getConfigDir() + "\\scorekeybinds.json";
            System.out.println("Config file not found for mod: ScoreKeybinds. Regenerating config file from defaults.");
            File configFile = new File(path);
            configFile.delete();
            ScoreKeybinds.createConfigFile();
            throw new RuntimeException("Config file for mod: ScoreKeybinds has been incorrectly set up. The config file has been regenerated from defaults. Please restart the game to fix this error.");

        }
    }

    @Override
    public void onInitializeClient() {
        try {
            if (getConfigData() != null) {
                createKeybinds();
                ClientTickEvents.END_CLIENT_TICK.register(client -> {
                    for (int i = 0; i < keyBindings.length; i++) {
                        try {
                            if (keyBindings[i].wasPressed()) {
                                if (getConfigData().getJSONObject(i).getString("command").charAt(0) == '/') {
                                    assert client.player != null;
                                    client.player.sendChatMessage(Objects.requireNonNull(getConfigData()).getJSONObject(i).getString("command"));
                                } else {
                                    assert client.player != null;
                                    client.player.sendChatMessage("/" + Objects.requireNonNull(getConfigData()).getJSONObject(i).getString("command"));
                                }
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                System.out.println("Config file not found for mod: ScoreKeybinds. Disabling mod.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}