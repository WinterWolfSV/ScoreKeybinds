package winterwolfsv.scorekeybinds.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;

import java.io.*;
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
    private KeyBinding[] keyBindings = new KeyBinding[Objects.requireNonNull(getConfigData()).length()];
    private void createKeybinds() {
        try {
            int i = 0;
            for (Object obj : Objects.requireNonNull(getConfigData())) {
                if (obj instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) obj;
                    String key = jsonObject.getString("key").toLowerCase();
                    String command = jsonObject.getString("command");
                    String name = jsonObject.getString("name");
                    System.out.println(key + " " + command + " " + name);

                    keyBindings[i]= KeyBindingHelper.registerKeyBinding(new KeyBinding(
                            name, // The translation key of the keybinding's name
                            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                            key.charAt(0)-32, // The keycode of the key),
                            "Testing_category" // The translation key of the keybinding's category.
                    ));
                }
                i++;
            }
        } catch (Exception e) {
            System.out.println(e);

        }
    }

    private static KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.examplemod.spook", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_R, // The keycode of the key
            "category.examplemod.test" // The translation key of the keybinding's category.
    ));


    @Override
    public void onInitializeClient() {
        createKeybinds();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (int i = 0; i < keyBindings.length; i++) {
                if (keyBindings[i].wasPressed()) {
                    assert client.player != null;
                    client.player.sendChatMessage("/tp @s ~ 100 ~"+ Objects.requireNonNull(getConfigData()).getJSONObject(i).getString("command"));
                    break; // Break out of the loop after the first pressed button is found
                }
            }
        });
        //ClientTickEvents.END_CLIENT_TICK.register(client -> {
        //    while (keyBinding.wasPressed()) {
        //        assert client.player != null;
        //        client.player.sendChatMessage("/tp @s ~ 100 ~");
        //    }
        //});
    }
}

