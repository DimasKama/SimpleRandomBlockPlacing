package com.dimaskama.simplerandomblockplacing.mixin;

import net.minecraft.client.gui.widget.SliderWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SliderWidget.class)
public interface SliderWidgetAccessor {
    @Accessor
    boolean isSliderFocused();

    @Invoker("setValue")
    void invokeSetValue(double value);

    @Invoker("getYImage")
    int getBackgroundV();

    @Invoker("getTextureV")
    int getSliderV();
}
