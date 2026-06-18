/*
 * Copyright (c) 2026 CaelusAether.
 * This file is licensed under a Creative Commons Attribution-NonCommercial 4.0 International License.
 * You should have received a copy of the license along with this work.
 * If not, see <https://creativecommons.org/licenses/by-nc/4.0/>.
 */
package caelus.aether.antighost;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public class AntiGhost implements ModInitializer {
    public static final String MOD_ID = "antighost";
    static Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(
                CustomBlocksUpdatePayloadS2C.TYPE,
                CustomBlocksUpdatePayloadS2C.STREAM_CODEC
        );

        PayloadTypeRegistry.playC2S().register(
                RequestBlocksUpdatePayloadC2S.TYPE,
                RequestBlocksUpdatePayloadC2S.STREAM_CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(
                RequestBlocksUpdatePayloadC2S.TYPE,
                (payload, context) -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    context.server().execute(() -> {
                        NetworkSender.send9x9x9Area(player, payload.center());
                    });
                }
        );
    }
}
