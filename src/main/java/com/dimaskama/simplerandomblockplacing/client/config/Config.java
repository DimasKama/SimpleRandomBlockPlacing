package com.dimaskama.simplerandomblockplacing.client.config;

import com.dimaskama.simplerandomblockplacing.client.SRBPMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public abstract class Config {
    private final transient String CONFPATH;

    public Config(String path) {
        CONFPATH = path;
    }

    public void loadOrCreate() {
        File file = new File(CONFPATH);
        if (file.exists()) {
            try (FileReader f = new FileReader(CONFPATH)) {
                Config c = new Gson().fromJson(f, getClass());
                for (Field field : getClass().getDeclaredFields()) field.set(this, field.get(c));
            } catch (IOException | IllegalAccessException e) {
                SRBPMod.LOGGER.warn("Exception occurred while reading config. " + e);
            }
        } else {
            File parent = file.getParentFile();
            if (!(parent.exists() || parent.mkdirs()))
                SRBPMod.LOGGER.warn("Can't create config: " + parent.getAbsolutePath());
            try {
                saveJsonWithoutCatch();
            } catch (IOException e) {
                SRBPMod.LOGGER.warn("Exception occurred while writing new config. " + e);
            }
        }
    }

    public void saveJson() {
        try {
            saveJsonWithoutCatch();
        } catch (IOException e) {
            SRBPMod.LOGGER.warn("Exception occurred while saving config. " + e);
        }
    }

    public void saveJsonWithoutCatch() throws IOException {
        try (FileWriter w = new FileWriter(CONFPATH)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(this, w);
            SRBPMod.LOGGER.info("Config saved: " + CONFPATH);
        }
    }

    public void reset() {
        try {
            Config n = getClass().getConstructor(String.class).newInstance(CONFPATH);
            for (Field field : getClass().getDeclaredFields()) field.set(this, field.get(n));
        } catch (Exception e) {
            SRBPMod.LOGGER.warn("Exception occurred while resetting config. " + e);
        }
    }
}
