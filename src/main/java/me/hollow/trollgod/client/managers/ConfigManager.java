/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.property.Bind;
import me.hollow.trollgod.api.property.EnumConverter;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.modules.Module;

public class ConfigManager {
    public ArrayList<Module> features = new ArrayList <> ();
    public String config = "TrollGod/config/";

    public void loadConfig(String name) {
        List files = (List) Arrays.stream(Objects.requireNonNull(new File("TrollGod").listFiles()));
        this.config = files.contains(new File("TrollGod/modules/")) ? "TrollGod/modules/" : "TrollGod/modules/";
        for (Module feature : this.features) {
            try {
                this.loadSettings(feature);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveConfig(String name) {
        this.config = "TrollGod/modules/";
        File path = new File(this.config);
        if (!path.exists()) {
            path.mkdir();
        }
        for (Module feature : this.features) {
            try {
                this.saveSettings(feature);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveSettings(Module feature) throws IOException {
        String featureName;
        Path outputFile;
        JsonObject object = new JsonObject();
        File directory = new File(this.config + this.getDirectory(feature));
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (!Files.exists(outputFile = Paths.get(featureName = this.config + this.getDirectory(feature) + feature.getLabel() + ".json", new String[0]), new LinkOption[0])) {
            Files.createFile(outputFile, new FileAttribute[0]);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson((JsonElement)this.writeSettings(feature));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile, new OpenOption[0])));
        writer.write(json);
        writer.close();
    }

    public static void setValueFromJson(Module feature, Setting setting, JsonElement element) {
        switch (setting.getType()) {
            case "Boolean": {
                setting.setValue(element.getAsBoolean());
                break;
            }
            case "Double": {
                setting.setValue(element.getAsDouble());
                break;
            }
            case "Float": {
                setting.setValue(Float.valueOf(element.getAsFloat()));
                break;
            }
            case "Integer": {
                setting.setValue(element.getAsInt());
                break;
            }
            case "String": {
                String str = element.getAsString();
                setting.setValue(str.replace("_", " "));
                break;
            }
            case "Bind": {
                setting.setValue(new Bind.BindConverter().doBackward(element));
                break;
            }
            case "Enum": {
                try {
                    EnumConverter converter = new EnumConverter(((Enum)setting.getValue()).getClass());
                    Enum value = converter.doBackward(element);
                    setting.setValue(value == null ? setting.getDefaultValue() : value);
                }
                catch (Exception e) {}
                break;
            }
        }
    }

    public void init() {
        this.features.addAll(TrollGod.INSTANCE.getModuleManager().getModules());
        this.loadConfig("modules");
    }

    private void loadSettings(Module feature) throws IOException {
        String featureName = this.config + this.getDirectory(feature) + feature.getLabel() + ".json";
        Path featurePath = Paths.get(featureName, new String[0]);
        if (!Files.exists(featurePath, new LinkOption[0])) {
            return;
        }
        this.loadPath(featurePath, feature);
    }

    private void loadPath(Path path, Module feature) throws IOException {
        InputStream stream = Files.newInputStream(path, new OpenOption[0]);
        try {
            ConfigManager.loadFile(new JsonParser().parse((Reader)new InputStreamReader(stream)).getAsJsonObject(), feature);
        }
        catch (IllegalStateException e) {
            ConfigManager.loadFile(new JsonObject(), feature);
        }
        stream.close();
    }

    private static void loadFile(JsonObject input, Module feature) {
        for (Map.Entry < String, JsonElement > entry : input.entrySet()) {
            String settingName = entry.getKey();
            JsonElement element = entry.getValue();
            if (( entry.getKey() ).equalsIgnoreCase("enabled")) {
                if (feature.isPersistent()) {
                    feature.setEnabled(true);
                }
                if (element.getAsBoolean()) {
                    feature.setEnabled(true);
                }
            }
            for (Setting setting : feature.getSettings()) {
                if (!settingName.equals(setting.getName())) continue;
                try {
                    ConfigManager.setValueFromJson(feature, setting, element);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public JsonObject writeSettings(Module feature) {
        JsonObject object = new JsonObject();
        JsonParser jp = new JsonParser();
        object.add("Enabled", jp.parse(String.valueOf(feature.isEnabled())));
        for (Setting setting : feature.getSettings()) {
            if (setting.isEnumSetting()) {
                EnumConverter converter = new EnumConverter(((Enum)setting.getValue()).getClass());
                object.add(setting.getName(), converter.doForward((Enum)setting.getValue()));
                continue;
            }
            if (setting.isStringSetting()) {
                String str = (String)setting.getValue();
                setting.setValue(str.replace(" ", "_"));
            }
            try {
                object.add(setting.getName(), jp.parse(setting.getValueAsString()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    public String getDirectory(Module feature) {
        String directory = "";
        if (feature != null) {
            directory = directory + feature.getCategory().name() + "/";
        }
        return directory;
    }
}

