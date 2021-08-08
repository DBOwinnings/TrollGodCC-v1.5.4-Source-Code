package me.hollow.trollgod.client.managers;

import me.hollow.trollgod.client.modules.*;
import java.util.stream.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.*;
import com.google.gson.*;
import java.io.*;
import java.util.*;

public class ConfigManager
{
    public ArrayList<Module> features;
    public String config;
    
    public ConfigManager() {
        this.features = new ArrayList<Module>();
        this.config = "TrollGod/config/";
    }
    
    public void loadConfig(final String name) {
        final List<File> files = Arrays.stream((Object[])Objects.requireNonNull((T[])new File("TrollGod").listFiles())).filter(File::isDirectory).collect((Collector<? super Object, ?, List<File>>)Collectors.toList());
        if (files.contains(new File("TrollGod/modules/"))) {
            this.config = "TrollGod/modules/";
        }
        else {
            this.config = "TrollGod/modules/";
        }
        for (final Module feature : this.features) {
            try {
                this.loadSettings(feature);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void saveConfig(final String name) {
        this.config = "TrollGod/modules/";
        final File path = new File(this.config);
        if (!path.exists()) {
            path.mkdir();
        }
        for (final Module feature : this.features) {
            try {
                this.saveSettings(feature);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void saveSettings(final Module feature) throws IOException {
        final JsonObject object = new JsonObject();
        final File directory = new File(this.config + this.getDirectory(feature));
        if (!directory.exists()) {
            directory.mkdir();
        }
        final String featureName = this.config + this.getDirectory(feature) + feature.getLabel() + ".json";
        final Path outputFile = Paths.get(featureName, new String[0]);
        if (!Files.exists(outputFile, new LinkOption[0])) {
            Files.createFile(outputFile, (FileAttribute<?>[])new FileAttribute[0]);
        }
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String json = gson.toJson((JsonElement)this.writeSettings(feature));
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile, new OpenOption[0])));
        writer.write(json);
        writer.close();
    }
    
    public static void setValueFromJson(final Module feature, final Setting setting, final JsonElement element) {
        final String type = setting.getType();
        switch (type) {
            case "Boolean": {
                setting.setValue(element.getAsBoolean());
                break;
            }
            case "Double": {
                setting.setValue(element.getAsDouble());
                break;
            }
            case "Float": {
                setting.setValue(element.getAsFloat());
                break;
            }
            case "Integer": {
                setting.setValue(element.getAsInt());
                break;
            }
            case "String": {
                final String str = element.getAsString();
                setting.setValue(str.replace("_", " "));
                break;
            }
            case "Bind": {
                setting.setValue(new Bind.BindConverter().doBackward(element));
                break;
            }
            case "Enum": {
                try {
                    final EnumConverter converter = new EnumConverter(((Enum)setting.getValue()).getClass());
                    final Enum value = converter.doBackward(element);
                    setting.setValue((value == null) ? setting.getDefaultValue() : value);
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
    
    private void loadSettings(final Module feature) throws IOException {
        final String featureName = this.config + this.getDirectory(feature) + feature.getLabel() + ".json";
        final Path featurePath = Paths.get(featureName, new String[0]);
        if (!Files.exists(featurePath, new LinkOption[0])) {
            return;
        }
        this.loadPath(featurePath, feature);
    }
    
    private void loadPath(final Path path, final Module feature) throws IOException {
        final InputStream stream = Files.newInputStream(path, new OpenOption[0]);
        try {
            loadFile(new JsonParser().parse((Reader)new InputStreamReader(stream)).getAsJsonObject(), feature);
        }
        catch (IllegalStateException e) {
            loadFile(new JsonObject(), feature);
        }
        stream.close();
    }
    
    private static void loadFile(final JsonObject input, final Module feature) {
        for (final Map.Entry<String, JsonElement> entry : input.entrySet()) {
            final String settingName = entry.getKey();
            final JsonElement element = entry.getValue();
            if (entry.getKey().equalsIgnoreCase("enabled")) {
                if (feature.isPersistent()) {
                    feature.setEnabled(true);
                }
                if (element.getAsBoolean()) {
                    feature.setEnabled(true);
                }
            }
            for (final Setting setting : feature.getSettings()) {
                if (settingName.equals(setting.getName())) {
                    try {
                        setValueFromJson(feature, setting, element);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public JsonObject writeSettings(final Module feature) {
        final JsonObject object = new JsonObject();
        final JsonParser jp = new JsonParser();
        object.add("Enabled", jp.parse(String.valueOf(feature.isEnabled())));
        for (final Setting setting : feature.getSettings()) {
            if (setting.isEnumSetting()) {
                final EnumConverter converter = new EnumConverter(setting.getValue().getClass());
                object.add(setting.getName(), converter.doForward(setting.getValue()));
            }
            else {
                if (setting.isStringSetting()) {
                    final String str = setting.getValue();
                    setting.setValue(str.replace(" ", "_"));
                }
                try {
                    object.add(setting.getName(), jp.parse(setting.getValueAsString()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }
    
    public String getDirectory(final Module feature) {
        String directory = "";
        if (feature != null) {
            directory = directory + feature.getCategory().name() + "/";
        }
        return directory;
    }
}
