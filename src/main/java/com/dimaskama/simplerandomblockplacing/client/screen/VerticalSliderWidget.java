package com.dimaskama.simplerandomblockplacing.client.screen;

import com.dimaskama.simplerandomblockplacing.mixin.SliderWidgetAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public abstract class VerticalSliderWidget extends SliderWidget {
    private static final Identifier TEXTURE = new Identifier("textures/gui/slider.png");
    private final boolean upToDown;

    public VerticalSliderWidget(int x, int y, int width, int height, Text text, boolean upToDown, double value) {
        super(x, y, width, height, text, value);
        this.upToDown = upToDown;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        context.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        context.drawNineSlicedTexture(
                TEXTURE,
                getX(), getY(),
                width, height,
                20, 4,
                200, 20,
                0, ((SliderWidgetAccessor) this).getBackgroundV()
        );
        context.drawNineSlicedTexture(
                TEXTURE,
                getX(), getY() + (int) ((upToDown ? value : 1.0 - value) * (height - 8)),
                width, 8,
                20, 4,
                200, 20,
                0, ((SliderWidgetAccessor) this).getSliderV()
        );

        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        drawScrollableText(context, mc.textRenderer, 1, active ? 0xffffff : 0xa0a0a0 | MathHelper.ceil(alpha * 255.0F) << 24);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_RIGHT) return false;
        if (((SliderWidgetAccessor) this).isSliderFocused()) {
            boolean bl = keyCode == GLFW.GLFW_KEY_UP;
            if (bl || keyCode == GLFW.GLFW_KEY_DOWN) {
                if (!upToDown) bl = !bl;
                ((SliderWidgetAccessor) this).invokeSetValue(value + (bl ? -1.0F : 1.0F) / (width - 8));
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        setValueFromMouse(mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        setValueFromMouse(mouseY);
    }

    private void setValueFromMouse(double mouseY) {
        double h = height - 8.0;
        double y = (mouseY - getY() - 4.0);
        if (!upToDown) y = h - y;
        ((SliderWidgetAccessor) this).invokeSetValue(y / h);
    }
}
