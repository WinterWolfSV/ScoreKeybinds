package winterwolfsv.scorekeybinds.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

@Environment(EnvType.CLIENT)
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
            for (Object obj : Objects.requireNonNull(getConfigData())) {
                if (obj instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) obj;
                    String stringDefaultKey = jsonObject.getString("default_key").toLowerCase();
                    int key = -1;
                    if (!stringDefaultKey.isEmpty() && stringDefaultKey.charAt(0) != ' ') {
                        key = stringDefaultKey.charAt(0) - 32;
                    }
                    String command = jsonObject.getString("command");
                    String name = jsonObject.getString("name");
                    System.out.println("Creating keybind: '" + name + "' With keybind: '" + stringDefaultKey + "' For command: '" + command + "'");
                    keyBindings[i] = KeyBindingHelper.registerKeyBinding(new KeyBinding(name, InputUtil.Type.KEYSYM, key, "Score Keybindings"));
                }
                i++;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void onInitializeClient() {
        try {
            createKeybinds();
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                for (int i = 0; i < keyBindings.length; i++) {
                    if (keyBindings[i].wasPressed()) {
                        assert client.player != null;
                        if (Objects.requireNonNull(getConfigData()).getJSONObject(i).getString("command").charAt(0) == '/') {
                            client.player.sendChatMessage(Objects.requireNonNull(getConfigData()).getJSONObject(i).getString("command"));
                        } else {
                            client.player.sendChatMessage("/" + Objects.requireNonNull(getConfigData()).getJSONObject(i).getString("command"));
                        }
                        break;
                    }
                }
            });
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

