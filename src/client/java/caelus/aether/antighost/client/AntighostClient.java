/*
 * Copyright (c) 2026 CaelusAether.
 * This file is licensed under a MIT License.
 * You should have received a copy of the license along with this work.
 * If not, see <https://creativecommons.org/licenses/by-nc/4.0/>.
 */
package caelus.aether.antighost.client;

import caelus.aether.antighost.CustomBlocksUpdatePayloadS2C;
import caelus.aether.antighost.RequestBlocksUpdatePayloadC2S;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;

public class AntighostClient implements ClientModInitializer {
    static KeyMapping requestBlocks;
    static Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        final String category="key.categories.antighost";
        requestBlocks = new KeyMapping("key.antighost.reveal", GLFW_KEY_U, category);
        KeyBindingHelper.registerKeyBinding(requestBlocks);
        LOGGER.info("按键已注册");
        ClientTickEvents.END_CLIENT_TICK.register(e->keyPressed());
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("ghost").executes(c -> {
                        this.execute();
                        return 0;
                    })
            );
        });
        LOGGER.info("指令已注册");
        LOGGER.info("Antighost Client Initialized");
        repair();
    }

    public void keyPressed() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (requestBlocks.isDown()) {
            this.execute();
            player.displayClientMessage(Component.translatable("msg.request"), false);
        }
    }

    public void repair() {
        ClientPlayNetworking.registerGlobalReceiver(
                CustomBlocksUpdatePayloadS2C.TYPE,
                (payload, context) -> {
                    context.client().execute(() -> {
                        Minecraft client = context.client();
                        Level world = client.level;
                        if (world == null) return;

                        for (var update : payload.updates()) {
                            // 3 = UPDATE_NEIGHBORS | UPDATE_CLIENTS
                            // 强制更新客户端缓存并通知渲染
                            world.setBlock(update.pos(), update.state(), 3);
                            LOGGER.debug("(Antighost) 已将方块替换为" + update.state().getBlock().getDescriptionId());
                        }

                        LevelRenderer renderer = client.levelRenderer;
                        renderer.setBlocksDirty(
                                payload.minX(), payload.minY(), payload.minZ(),
                                payload.maxX(), payload.maxY(), payload.maxZ()
                        );
                        LOGGER.debug("(Antighost) 已重绘");
                    });
                }
        );
    }

    public void execute() {
        BlockPos center = Minecraft.getInstance().player.blockPosition();
        RequestBlocksUpdatePayloadC2S request = new RequestBlocksUpdatePayloadC2S(center);
        ClientPlayNetworking.send(request);
        LOGGER.debug("(Antighost) C2S包已发送");
    }
}
