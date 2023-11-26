package com.dimaskama.simplerandomblockplacing.client.screen;

import com.dimaskama.simplerandomblockplacing.client.SRBPMod;
import com.dimaskama.simplerandomblockplacing.client.config.SlotOption;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class SRBPScreen extends Screen {
    private static final Text ENABLED_TEXT = Text.translatable("options.srbp.enabled");
    private final SlotCheckboxWidget[] checkboxes = new SlotCheckboxWidget[9];
    private final SlotChanceSlider[] sliders = new SlotChanceSlider[9];
    private boolean firstInit = true;

    public SRBPScreen() {
        super(Text.translatable("srbp"));
    }

    @Override
    protected void init() {
        int x = (width >> 1) - 90;
        int y = height - 150;
        if (firstInit) {
            firstInit = false;
            for (int i = 0; i < 9; i++) {
                SlotOption s = SRBPMod.CONFIG.slots.get(i);
                checkboxes[i] = new SlotCheckboxWidget(x + 20 * i, y, s);
                sliders[i] = new SlotChanceSlider(x + 1 + 20 * i, y + 20, s);
            }
        } else {
            for (int i = 0; i < 9; i++) {
                checkboxes[i].setPosition(x + 20 * i, y);
                sliders[i].setPosition(x + 1 + 20 * i, y + 20);
            }
        }
        for (int i = 0; i < 9; i++) {
            addDrawableChild(checkboxes[i]);
            addDrawableChild(sliders[i]);
        }

        // State toggle button
        addDrawableChild(CyclingButtonWidget.onOffBuilder(SRBPMod.CONFIG.enabled)
                .build(x, y - 35, 85, 20, ENABLED_TEXT, (button, value) -> SRBPMod.CONFIG.enabled = value));

        // Back button
        addDrawableChild(ButtonWidget.builder(ScreenTexts.BACK, button -> close())
                .dimensions(x + 95, y - 35, 85, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        context.drawCenteredTextWithShadow(textRenderer, title, width >> 1, 8, 0xffffff);

        super.render(context, mouseX, mouseY, delta);

        int x = (width >> 1) - 80;
        for (int i = 0; i < 9; i++) {
            boolean off = !checkboxes[i].isChecked() || sliders[i].getValue() <= 0.0;
            context.drawCenteredTextWithShadow(
                    textRenderer,
                    off ? ScreenTexts.OFF : Text.literal(String.valueOf((int) (sliders[i].getValue() * 100.0))),
                    x + 20 * i,
                    height - 161,
                    0xffffff
            );
        }
    }

    @Override
    public void close() {
        SRBPMod.CONFIG.saveJson();
        super.close();
    }

    private static class SlotCheckboxWidget extends CheckboxWidget {
        public final SlotOption slot;

        public SlotCheckboxWidget(int x, int y, SlotOption slot) {
            super(x, y, 20, 20, Text.empty(), slot.enabled, false);
            this.slot = slot;
        }

        @Override
        public void onPress() {
            super.onPress();
            slot.enabled = isChecked();
        }
    }

    private static class SlotChanceSlider extends VerticalSliderWidget {
        public final SlotOption slot;

        public SlotChanceSlider(int x, int y, SlotOption slot) {
            super(x, y, 18, 108, Text.empty(), false, MathHelper.clamp(slot.chance, 0.0, 1.0));
            this.slot = slot;
        }

        public double getValue() {
            return value;
        }

        @Override
        protected void updateMessage() {
        }

        @Override
        protected void applyValue() {
            slot.chance = value;
        }
    }
}
