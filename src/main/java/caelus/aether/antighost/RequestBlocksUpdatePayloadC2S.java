/*
 * Copyright (c) 2026 CaelusAether.
 * This file is licensed under a MIT License.
 * You should have received a copy of the license along with this work.
 * If not, see <https://creativecommons.org/licenses/by-nc/4.0/>.
 */
package caelus.aether.antighost;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public record RequestBlocksUpdatePayloadC2S(BlockPos center) implements CustomPacketPayload {
    public static final Type<RequestBlocksUpdatePayloadC2S> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AntiGhost.MOD_ID, "block_update_payload_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestBlocksUpdatePayloadC2S> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, RequestBlocksUpdatePayloadC2S::center,
                    RequestBlocksUpdatePayloadC2S::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}