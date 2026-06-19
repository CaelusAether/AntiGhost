/*
 * Copyright (c) 2026 CaelusAether.
 * This file is licensed under a MIT License.
 * You should have received a copy of the license along with this work.
 * If not, see <https://creativecommons.org/licenses/by-nc/4.0/>.
 */
package caelus.aether.antighost;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public record CustomBlocksUpdatePayloadS2C(int minX, int minY, int minZ,
                                           int maxX, int maxY, int maxZ,
                                           List<BlockUpdateData> updates)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CustomBlocksUpdatePayloadS2C> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AntiGhost.MOD_ID, "block_update_payload_s2c"));

    public record BlockUpdateData(BlockPos pos, BlockState state) {
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockUpdateData> STREAM_CODEC =
                StreamCodec.composite(
                        BlockPos.STREAM_CODEC,
                        BlockUpdateData::pos,
                        ByteBufCodecs.fromCodec(BlockState.CODEC),
                        BlockUpdateData::state,
                        BlockUpdateData::new
                );
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, CustomBlocksUpdatePayloadS2C> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, CustomBlocksUpdatePayloadS2C::minX,
                    ByteBufCodecs.INT, CustomBlocksUpdatePayloadS2C::minY,
                    ByteBufCodecs.INT, CustomBlocksUpdatePayloadS2C::minZ,
                    ByteBufCodecs.INT, CustomBlocksUpdatePayloadS2C::maxX,
                    ByteBufCodecs.INT, CustomBlocksUpdatePayloadS2C::maxY,
                    ByteBufCodecs.INT, CustomBlocksUpdatePayloadS2C::maxZ,
                    BlockUpdateData.STREAM_CODEC.apply(ByteBufCodecs.list()), CustomBlocksUpdatePayloadS2C::updates,
                    CustomBlocksUpdatePayloadS2C::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
