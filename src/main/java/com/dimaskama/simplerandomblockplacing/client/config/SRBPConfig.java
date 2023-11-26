package com.dimaskama.simplerandomblockplacing.client.config;

import java.util.ArrayList;

public class SRBPConfig extends Config {
    public boolean save_enabled_state = true;
    public boolean enabled = false;
    public ArrayList<SlotOption> slots = new ArrayList<>();

    public SRBPConfig(String path) {
        super(path);
        for (int i = 0; i < 9; i++) {
            slots.add(new SlotOption(0.5, true));
        }
    }
}
