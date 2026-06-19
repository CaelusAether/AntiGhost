/*
 * Copyright (c) 2026 CaelusAether.
 * This file is licensed under a MIT License.
 * You should have received a copy of the license along with this work.
 * If not, see <https://creativecommons.org/licenses/by-nc/4.0/>.
 */
package caelus.aether.antighost;

import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;


import java.util.ArrayList;
import java.util.List;

public class NetworkSender {
   static Logger LOGGER = LogUtils.getLogger();
    public static void send9x9x9Area(ServerPlayer player, BlockPos center) {
        ServerLevel world = player.level();

        int minX = center.getX() - 4;
        int minY = center.getY() - 4;
        int minZ = center.getZ() - 4;
        int maxX = center.getX() + 4;
        int maxY = center.getY() + 4;
        int maxZ = center.getZ() + 4;

        List<CustomBlocksUpdatePayloadS2C.BlockUpdateData> updates = new ArrayList<>(9 * 9 * 9);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    updates.add(new CustomBlocksUpdatePayloadS2C.BlockUpdateData(pos, state));
                }
            }
        }

        CustomBlocksUpdatePayloadS2C payload = new CustomBlocksUpdatePayloadS2C(
                minX, minY, minZ,
                maxX, maxY, maxZ,
                updates
        );

        ServerPlayNetworking.send(player, payload);
    }
}
