package com.dimaskama.simplerandomblockplacing.client;

import com.dimaskama.simplerandomblockplacing.client.config.SRBPConfig;
import com.dimaskama.simplerandomblockplacing.client.screen.SRBPScreen;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SRBPMod implements ClientModInitializer {
    public static final String MOD_ID = "simplerandomblockplacing";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final SRBPConfig CONFIG = new SRBPConfig("config/simplerandomblockplacing.json");

    @Override
    public void onInitializeClient() {
        CONFIG.loadOrCreate();
        CONFIG.enabled = CONFIG.save_enabled_state && CONFIG.enabled;
        ClientCommandRegistrationCallback.EVENT.register(new ModCommand());
    }

    public static void openScreen() {
        MinecraftClient.getInstance().setScreen(new SRBPScreen());
    }

    private static final class ModCommand implements ClientCommandRegistrationCallback {
        private static final SimpleCommandExceptionType accessException = new SimpleCommandExceptionType(Text.literal("Access exception"));

        @Override
        public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
            Command<FabricClientCommandSource> open = context -> {
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(50);
                        MinecraftClient.getInstance().execute(SRBPMod::openScreen);
                    } catch (InterruptedException ignored) {
                    }
                });
                thread.setDaemon(true);
                thread.start();
                return 0;
            };
            LiteralCommandNode<FabricClientCommandSource> command =
                    dispatcher.register(literal("randomblock")
                            .executes(open)
                            .then(literal("screen")
                                    .executes(open))
                            .then(literal("toggle")
                                    .executes(context -> {
                                        CONFIG.enabled = !CONFIG.enabled;
                                        context.getSource().sendFeedback(Text.translatable(CONFIG.enabled ? "commands.srbp.on" : "commands.srbp.off"));
                                        CONFIG.saveJson();
                                        return 1;
                                    }))
                            .then(literal("config")
                                    .then(literal("save_enabled_state")
                                            .then(argument("value", BoolArgumentType.bool())
                                                    .executes(context -> changeConfigValue("save_enabled_state", BoolArgumentType.getBool(context, "value")))))));

            dispatcher.register(literal("srbp").executes(open).redirect(command));
        }

        private int changeConfigValue(String field, Object value) throws CommandSyntaxException {
            try {
                CONFIG.getClass().getField(field).set(CONFIG, value);
                CONFIG.saveJson();
            } catch (NoSuchFieldException e) {
                LOGGER.error(e);
            } catch (IllegalAccessException e) {
                LOGGER.warn(e);
                throw accessException.create();
            }
            return 1;
        }
    }
}
