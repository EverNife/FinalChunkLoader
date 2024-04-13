package br.com.finalcraft.finalchunkloader.config.rank;

import br.com.finalcraft.evernifecore.config.Config;
import br.com.finalcraft.finalchunkloader.FinalChunkLoader;
import br.com.finalcraft.finalchunkloader.config.ConfigManager;
import br.com.finalcraft.finalchunkloader.config.data.FCLPlayerData;
import br.com.finalcraft.finalchunkloader.config.settings.ChunkLoaderType;

import java.util.*;
import java.util.stream.Collectors;

public class ChunksByGroup {

    public static boolean enableRankLimit;
    public static List<GroupLimiter> GROUP_LIMITERS = new ArrayList<>();

    public static void initialize(){
        Config config = ConfigManager.getChunksByRank();

        enableRankLimit = config.getOrSetDefaultValue(
                "ChunksLimitByGroup.enable",
                true,
                "Enable the Group Limit System! " +
                        "\nIn this mode the player has a limit of chunkloaders based on his group." +
                        "\nThe group is defined by his permission."
        );

        GROUP_LIMITERS = config.getOrSetDefaultValue(
                "ChunksByGroup.CustomGroups",
                new ArrayList<>(Arrays.asList(
                        new GroupLimiter("Visitante", 9, 0, "group.default"),
                        new GroupLimiter("Novato", 12, 0, "group.novato"),
                        new GroupLimiter("Membro", 16, 0, "group.membro"),
                        new GroupLimiter("Aprendiz", 20, 0, "group.aprendiz"),
                        new GroupLimiter("Veterano", 25, 0, "group.veterano"),
                        new GroupLimiter("Prodigio", 31, 0, "group.prodigio"),
                        new GroupLimiter("Genio", 37, 0, "group.genio"),
                        new GroupLimiter("Mito", 43, 0, "group.mito"),
                        new GroupLimiter("Lenda", 49, 0, "group.lenda"),

                        new GroupLimiter("Vip1", 0, 1, "be.vip1"),
                        new GroupLimiter("Vip2", 0, 1, "be.vip2"),
                        new GroupLimiter("Vip3", 0, 2, "be.vip3"),
                        new GroupLimiter("Vip4", 0, 3, "be.vip4"),
                        new GroupLimiter("Vip5", 0, 4, "be.vip5"),
                        new GroupLimiter("Vip6", 0, 6, "be.vip6"),
                        new GroupLimiter("Vip7", 0, 9, "be.vip7")
                )),
                "Definition of chunklaoders a player will have depending on the permissions he has." +
                        "\nIf the player has more than one permission, amount will be the merge between the max value of both."
        );

        config.saveIfNewDefaults();
    }

    public static boolean isGroupLimiterEnblaed(){
        return enableRankLimit == true && GROUP_LIMITERS.size() > 0;
    }

    public static void recalibratePlayerLimits(FCLPlayerData playerData){

        if (!playerData.isPlayerOnline()){
            return;
        }

        List<GroupLimiter> groupsWithPermission = GROUP_LIMITERS.stream()
                .filter(groupLimiter -> playerData.getPlayer().hasPermission(groupLimiter.getPermission()))
                .collect(Collectors.toList());

        int maxOnlineOnly = 0;
        int maxAlwaysOn = 0;

        for (GroupLimiter groupLimiter : groupsWithPermission) {
            maxOnlineOnly = Math.max(maxOnlineOnly, groupLimiter.getOnlineOnly());
            maxAlwaysOn = Math.max(maxAlwaysOn, groupLimiter.getAlwaysOn());
        }

        int currentOnlineOnly = playerData.getChunksLimit(ChunkLoaderType.ONLINE_ONLY);
        int currentAlwaysOn = playerData.getChunksLimit(ChunkLoaderType.ALWAYS_ON);

        if (currentOnlineOnly != maxOnlineOnly){
            playerData.setChunksLimit(ChunkLoaderType.ONLINE_ONLY, maxOnlineOnly);
            FinalChunkLoader.getLog().info("Fixing [OnlineOnly Chunks]' amount of " + playerData.getPlayerName() + " from " + currentOnlineOnly + " to " + maxOnlineOnly);
        }

        if (currentAlwaysOn != maxAlwaysOn){
            playerData.setChunksLimit(ChunkLoaderType.ALWAYS_ON, maxAlwaysOn);
            FinalChunkLoader.getLog().info("Fixing [AlwaysOn Chunks]' amount of " + playerData.getPlayerName() + " from " + currentAlwaysOn + " to " + maxAlwaysOn);
        }
    }

}
