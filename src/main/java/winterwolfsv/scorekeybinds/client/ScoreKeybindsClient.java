package winterwolfsv.scorekeybinds.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;


public class ScoreKeybindsClient implements ClientModInitializer {

    private JSONArray getConfigData() {
        String path = FabricLoader.getInstance().getConfigDir() + "\\scorekeybinds.json";
        System.out.println(path);
        if (new File(path).exists()) {
            System.out.println("Path to config file for mod exists.");
            try {
                InputStream inputStream = Files.newInputStream(Paths.get(path));
                Scanner scanner = new Scanner(inputStream, "UTF-8");
                String jsonString = scanner.useDelimiter("\\A").next();
                return new JSONArray(jsonString);
            } catch (Exception e) {
                System.out.println(e);
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
            } else {
                System.out.println("Config file not found for mod: ScoreKeybinds. Disabling mod.");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void onInitializeClient() {
        AtomicBoolean disableMod = new AtomicBoolean(false);
        try {
            if (getConfigData() != null && !disableMod.get()) {
                createKeybinds();
                ClientTickEvents.END_CLIENT_TICK.register(client -> {
                    for (int i = 0; i < keyBindings.length; i++) {
                        try {
                            if (keyBindings[i].wasPressed()) {
                                if (getConfigData().getJSONObject(i).getString("command").charAt(0) == '/') {
                                    client.player.sendChatMessage(Objects.requireNonNull(getConfigData()).getJSONObject(i).getString("command"));
                                } else {
                                    client.player.sendChatMessage("/" + Objects.requireNonNull(getConfigData()).getJSONObject(i).getString("command"));
                                }
                                break;
                            }
                        } catch (Exception e) {
                            System.out.println(e);
                            disableMod.set(true);
                        }

                    }
                });
            } else {
                System.out.println("Config file not found for mod: ScoreKeybinds. Disabling mod.");
                disableMod.set(true);

            }

        } catch (Exception e) {
            System.out.println(e);
            disableMod.set(true);
        }
    }
}

