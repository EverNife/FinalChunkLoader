package br.com.finalcraft.finalchunkloader.commands;

import br.com.finalcraft.evernifecore.argumento.MultiArgumentos;
import br.com.finalcraft.evernifecore.commands.finalcmd.annotations.Arg;
import br.com.finalcraft.evernifecore.commands.finalcmd.annotations.FinalCMD;
import br.com.finalcraft.evernifecore.commands.finalcmd.help.HelpLine;
import br.com.finalcraft.evernifecore.ecplugin.ECPluginManager;
import br.com.finalcraft.evernifecore.fancytext.FancyText;
import br.com.finalcraft.evernifecore.locale.FCLocale;
import br.com.finalcraft.evernifecore.locale.LocaleMessage;
import br.com.finalcraft.evernifecore.locale.LocaleType;
import br.com.finalcraft.evernifecore.util.FCBukkitUtil;
import br.com.finalcraft.evernifecore.util.FCMessageUtil;
import br.com.finalcraft.finalchunkloader.FinalChunkLoader;
import br.com.finalcraft.finalchunkloader.PermissionNodes;
import br.com.finalcraft.finalchunkloader.chunkloader.CChunkLoader;
import br.com.finalcraft.finalchunkloader.config.data.FCLPlayerData;
import br.com.finalcraft.finalchunkloader.config.datastore.ChunkLoaderManager;
import br.com.finalcraft.finalchunkloader.config.rank.ChunksByGroup;
import br.com.finalcraft.finalchunkloader.config.rank.GroupLimiter;
import br.com.finalcraft.finalchunkloader.config.settings.ChunkLoaderType;
import br.com.finalcraft.finalchunkloader.evernife.EverNifeFunctions;
import net.kaikk.mc.bcl.forgelib.BCLForgeLib;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FinalCMD(
        aliases = {"finalchunkloader","chunkloader","fcl","betterchunkloader","bcl"}
)
public class CMDChunkLoader {

    @FCLocale(lang = LocaleType.EN_US, text = "§2    ▶ §aChunkLoaders Available §e(Online Only): §6%free_chunks%§7§l/§a%total_free_chunks%")
    @FCLocale(lang = LocaleType.PT_BR, text = "§2    ▶ §aChunkLoaders Disponíveis §e(Normal): §6%free_chunks%§7§l/§a%total_free_chunks%")
    public static LocaleMessage AVAILABLE_CHUNKS_ONLINE_ONLY;

    @FCLocale(lang = LocaleType.EN_US, text = "§2    ▶ §aChunkLoaders Available §b(Always On): §6%premium_chunks%§7§l/§a%total_premium_chunks%")
    @FCLocale(lang = LocaleType.PT_BR, text = "§2    ▶ §aChunkLoaders Disponíveis §b(Permanente): §6%premium_chunks%§7§l/§a%total_premium_chunks%")
    public static LocaleMessage AVAILABLE_CHUNKS_ALWAYS_ON;

    @FinalCMD.SubCMD(
            subcmd = {"infoOther"},
            locales = {
                    @FCLocale(lang = LocaleType.EN_US, text = "Show another player's info"),
                    @FCLocale(lang = LocaleType.PT_BR, text = "Mostra informações de outro jogador")
            },
            permission = PermissionNodes.COMMAND_INFO_OTHER
    )
    public void infoOther(CommandSender sender, @Arg(name = "<Player>") FCLPlayerData target) {
        int free_chunks = target.getChunksLimitRemaining(ChunkLoaderType.ONLINE_ONLY);
        int total_free_chunks = target.getChunksLimit(ChunkLoaderType.ONLINE_ONLY);

        int premium_chunks = target.getChunksLimitRemaining(ChunkLoaderType.ALWAYS_ON);
        int total_premium_chunks = target.getChunksLimit(ChunkLoaderType.ALWAYS_ON);

        sender.sendMessage("§a§m-----------------------------------------------------");
        sender.sendMessage("§a  ▶ [" + target.getPlayerName() + "] BCL Info:");
        AVAILABLE_CHUNKS_ONLINE_ONLY
                .addPlaceholder("%free_chunks%", free_chunks)
                .addPlaceholder("%total_free_chunks%", total_free_chunks)
                .send(sender);
        AVAILABLE_CHUNKS_ALWAYS_ON
                .addPlaceholder("%premium_chunks%", premium_chunks)
                .addPlaceholder("%total_premium_chunks%", total_premium_chunks)
                .send(sender);
        sender.sendMessage("§a§m-----------------------------------------------------");
    }

    @FinalCMD.SubCMD(
            subcmd = {"info"},
            locales = {
                    @FCLocale(lang = LocaleType.EN_US, text = "Show your info"),
                    @FCLocale(lang = LocaleType.PT_BR, text = "Mostra suas informações")
            }
    )
    public void info(Player player, FCLPlayerData playerData, MultiArgumentos argumentos) {
        int free_chunks = playerData.getChunksLimitRemaining(ChunkLoaderType.ONLINE_ONLY);
        int total_free_chunks = playerData.getChunksLimit(ChunkLoaderType.ONLINE_ONLY);

        int premium_chunks = playerData.getChunksLimitRemaining(ChunkLoaderType.ALWAYS_ON);
        int total_premium_chunks = playerData.getChunksLimit(ChunkLoaderType.ALWAYS_ON);

        player.sendMessage("§a§m-----------------------------------------------------");
        AVAILABLE_CHUNKS_ONLINE_ONLY
                .addPlaceholder("%free_chunks%", free_chunks)
                .addPlaceholder("%total_free_chunks%", total_free_chunks)
                .send(player);
        AVAILABLE_CHUNKS_ALWAYS_ON
                .addPlaceholder("%premium_chunks%", premium_chunks)
                .addPlaceholder("%total_premium_chunks%", total_premium_chunks)
                .send(player);
        player.sendMessage("§a§m-----------------------------------------------------");
        return;
    }

    @FCLocale(lang = LocaleType.EN_US, text = "§2 ▶ §2ChunkLoaders being used §e(Normal): §6%free_chunks%§7§l/§a%total_free_chunks%")
    @FCLocale(lang = LocaleType.PT_BR, text = "§2 ▶ §2ChunkLoaders em USO §e(Normal): §6%free_chunks%§7§l/§a%total_free_chunks%")
    public static LocaleMessage CHUNKS_IN_USE_ONLINE_ONLY;

    @FCLocale(lang = LocaleType.EN_US, text = "§2 ▶ §2ChunkLoaders being used §b(Permanente): §6§6%free_chunks%§7§l/§a%total_premium_chunks%")
    @FCLocale(lang = LocaleType.PT_BR, text = "§2 ▶ §2ChunkLoaders em USO §b(Permanente): §6%premium_chunks%§7§l/§a%total_premium_chunks%")
    public static LocaleMessage CHUNKS_IN_USE_ALWAYS_ON;

    @FinalCMD.SubCMD(
            subcmd = {"list"},
            usage = "<Normal|Perma>",
            locales = {
                    @FCLocale(lang = LocaleType.EN_US, text = "List all your chunkloaders"),
                    @FCLocale(lang = LocaleType.PT_BR, text = "Lista todas os seus ChunkLoaders")
            }
    )
    public void list(Player player, FCLPlayerData playerData, MultiArgumentos argumentos, HelpLine helpLine) {

        if (argumentos.emptyArgs(1)){
            helpLine.sendTo(player);
            return;
        }

        List<CChunkLoader> chunkLoaderList = playerData.getChunkLoaders();

        switch (argumentos.get(1).toLowerCase()) {
            case "free":
            case "comum":
            case "normal":
            case "onlineonly":
                int free_chunks = 0;
                int total_free_chunks = playerData.getChunksLimit(ChunkLoaderType.ONLINE_ONLY);
                player.sendMessage("§a§m-----------------------------------------------------");
                for (CChunkLoader chunkLoader : chunkLoaderList) {
                    if (!chunkLoader.isAlwaysOn()){
                        free_chunks += chunkLoader.totalChunks();
                        FancyText.of("§7  - §e[" + chunkLoader.sizeInString() + "] " + chunkLoader.getLoc().toString())
                                .setRunCommandAction(!player.hasPermission(PermissionNodes.COMMAND_ADMIN) ? null : "/tppos " + chunkLoader.getLoc().getX() + " " + chunkLoader.getLoc().getY() + " " + chunkLoader.getLoc().getZ() + " " + chunkLoader.getWorldName())
                                .send(player);
                    }
                }
                player.sendMessage("");
                CHUNKS_IN_USE_ONLINE_ONLY
                        .addPlaceholder("%free_chunks%", free_chunks)
                        .addPlaceholder("%total_free_chunks%", total_free_chunks)
                        .send(player);
                player.sendMessage("§a§m-----------------------------------------------------");
                break;
            case "vip":
            case "perma":
            case "premium":
            case "alwayson":
            case "permanente":
                int premium_chunks = 0;
                int total_premium_chunks = playerData.getChunksLimit(ChunkLoaderType.ALWAYS_ON);
                player.sendMessage("§a§m-----------------------------------------------------");
                for (CChunkLoader chunkLoader : chunkLoaderList) {
                    if (chunkLoader.isAlwaysOn()){
                        premium_chunks += chunkLoader.totalChunks();
                        FancyText.of("§7  - §b[" + chunkLoader.sizeInString() + "] " + chunkLoader.getLoc().toString())
                                .setRunCommandAction(!player.hasPermission(PermissionNodes.COMMAND_ADMIN) ? null : "/tppos " + chunkLoader.getLoc().getX() + " " + chunkLoader.getLoc().getY() + " " + chunkLoader.getLoc().getZ() + " " + chunkLoader.getWorldName())
                                .send(player);
                    }
                }
                player.sendMessage("");
                CHUNKS_IN_USE_ALWAYS_ON
                        .addPlaceholder("%premium_chunks%", premium_chunks)
                        .addPlaceholder("%total_premium_chunks%", total_premium_chunks)
                        .send(player);
                player.sendMessage("§a§m-----------------------------------------------------");
                break;
        }

        return;
    }

    @FinalCMD.SubCMD(
            subcmd = {"listOther"},
            permission = PermissionNodes.COMMAND_LIST_OTHER,
            locales = {
                    @FCLocale(lang = LocaleType.EN_US, text = "List all ChunkLoaders from a specific player."),
                    @FCLocale(lang = LocaleType.PT_BR, text = "Lista todos os ChunkLoaders de um jogador especifico.")
            }
    )
    public void listOther(CommandSender sender, @Arg(name = "<Player>") FCLPlayerData playerData, @Arg(name = "<Normal|Premium>") String type) {

        List<CChunkLoader> chunkLoaderList = playerData.getChunkLoaders();

        switch (type.toLowerCase()) {
            case "normal":
                int free_chunks = 0;
                int total_free_chunks = playerData.getChunksLimit(ChunkLoaderType.ONLINE_ONLY);
                sender.sendMessage("§a§m-----------------------------------------------------");
                for (CChunkLoader chunkLoader : chunkLoaderList) {
                    if (!chunkLoader.isAlwaysOn()){
                        free_chunks += chunkLoader.totalChunks();
                        FancyText.of("§7  - §e[" + chunkLoader.sizeInString() + "] " + chunkLoader.getLoc().toString())
                                .setRunCommandAction("/tppos " + chunkLoader.getLoc().getX() + " " + chunkLoader.getLoc().getY() + " " + chunkLoader.getLoc().getZ() + " " + chunkLoader.getWorldName())
                                .send(sender);
                    }
                }
                sender.sendMessage("");
                CHUNKS_IN_USE_ONLINE_ONLY
                        .addPlaceholder("%free_chunks%", free_chunks)
                        .addPlaceholder("%total_free_chunks%", total_free_chunks)
                        .send(sender);
                sender.sendMessage("§a§m-----------------------------------------------------");
                break;
            case "premium":
                int premium_chunks = 0;
                int total_premium_chunks = playerData.getChunksLimit(ChunkLoaderType.ALWAYS_ON);
                sender.sendMessage("§a§m-----------------------------------------------------");
                for (CChunkLoader chunkLoader : chunkLoaderList) {
                    if (chunkLoader.isAlwaysOn()){
                        premium_chunks += chunkLoader.totalChunks();
                        FancyText.of("§7  - §b[" + chunkLoader.sizeInString() + "] " + chunkLoader.getLoc().toString())
                                .setRunCommandAction("/tppos " + chunkLoader.getLoc().getX() + " " + chunkLoader.getLoc().getY() + " " + chunkLoader.getLoc().getZ() + " " + chunkLoader.getWorldName())
                                .send(sender);
                    }
                }
                sender.sendMessage("");
                CHUNKS_IN_USE_ALWAYS_ON
                        .addPlaceholder("%premium_chunks%", premium_chunks)
                        .addPlaceholder("%total_premium_chunks%", total_premium_chunks)
                        .send(sender);
                sender.sendMessage("§a§m-----------------------------------------------------");
                break;
        }

    }

    @FCLocale(lang = LocaleType.PT_BR, text = "§4 ▶ §cO sistema de §b[chunks por grupo]§c está ativado, você não pode alterar manualmente.")
    @FCLocale(lang = LocaleType.EN_US, text = "§4 ▶ §cThe §b[chunks by group]§c system is enabled, you can't change it manually.")
    public static LocaleMessage CHUNKS_BY_GROUP_SYSTEM_IS_ENABLED_YOU_CAN_CHANGE_IT_MANUALLY;

    @FinalCMD.SubCMD(
            subcmd = {"chunks"},
            usage = "%name% <PlayerName> <free|premium> <add|set> <amount> [-force]",
            locales = {
                    @FCLocale(lang = LocaleType.EN_US, text = "Edit specific player chunkloaders's values."),
                    @FCLocale(lang = LocaleType.PT_BR, text = "Edita os chunkloaders de um jogador específico.")
            },
            permission = PermissionNodes.COMMAND_ADMIN
    )
    public void chunks(CommandSender sender, MultiArgumentos argumentos, HelpLine helpLine) {

        if (ChunksByGroup.isGroupLimiterEnblaed()){
            CHUNKS_BY_GROUP_SYSTEM_IS_ENABLED_YOU_CAN_CHANGE_IT_MANUALLY.send(sender);
            return;
        }

        if (argumentos.emptyArgs(1,2,3,4)){
            helpLine.sendTo(sender);
            return;
        }

        FCLPlayerData playerData = argumentos.get(1).getPDSection(FCLPlayerData.class);

        if (playerData == null){
            FCMessageUtil.playerDataNotFound(sender, argumentos.getStringArg(1));
            return;
        }

        Boolean premium;
        switch (argumentos.get(2).toLowerCase()) {
            case "free":
            case "comum":
            case "normal":
            case "onlineonly":
                premium = false;
                break;
            case "vip":
            case "perma":
            case "premium":
            case "alwayson":
            case "permanente":
                premium = true;
                break;
            default: //Wrong Argument
                helpLine.sendTo(sender);
                return;
        }

        Integer amount = argumentos.get(4).getInteger();
        if (amount == null){
            FCMessageUtil.needsToBeInteger(sender, argumentos.getStringArg(4));
            return;
        }

        switch (argumentos.get(3).toLowerCase()) {
            case "add":
                if (premium){
                    playerData.setChunksLimit(ChunkLoaderType.ALWAYS_ON, playerData.getChunksLimit(ChunkLoaderType.ALWAYS_ON) + amount);
                }else {
                    playerData.setChunksLimit(ChunkLoaderType.ONLINE_ONLY, playerData.getChunksLimit(ChunkLoaderType.ONLINE_ONLY) + amount);
                }
                sender.sendMessage("Added " + amount + " " + (premium ? "always-on" : "online-only") + " chunks to " + playerData.getPlayerName());
                break;
            case "set":
                if (premium){
                    playerData.setChunksLimit(ChunkLoaderType.ALWAYS_ON, amount);
                }else {
                    playerData.setChunksLimit(ChunkLoaderType.ONLINE_ONLY, amount);
                }
                sender.sendMessage("Set " + playerData.getPlayerName() + (premium ? "always-on" : "online-only") + " chunks to" + amount);
                break;
            default: //Wrong Argument
                helpLine.sendTo(sender);
                return;
        }

        return;
    }

    @FinalCMD.SubCMD(
            subcmd = {"near"},
            permission = PermissionNodes.COMMAND_NEAR
    )
    public void near(Player player) {
        List<CChunkLoader> allChunks = ChunkLoaderManager.getAllActivePremiumChunkLoaders();

        if (allChunks.isEmpty()){
            player.sendMessage("Não existem ChunkLoaders Premium no servidor;");
            return;
        }

        List<CChunkLoader> nearChunks = new ArrayList<>();
        Location playerLocation = player.getLocation();

        allChunks.forEach(cChunkLoader -> {
            try{
                if (!cChunkLoader.hasExpired() && !cChunkLoader.markDisabled){
                    if (playerLocation.distance(cChunkLoader.getLocation()) <= 600 ){
                        nearChunks.add(cChunkLoader);
                    }
                }
            }catch (Exception ignored){}
        });

        player.sendMessage("--------- Near Premium Chunks ---------");
        nearChunks.forEach(cChunkLoader ->
                FancyText.of("§a§lPremium Chunk [§6§l " + (cChunkLoader.markDisabled ? "§c§l" : "") + cChunkLoader.getOwnerName() + " §a§l]")
                        .setHoverText("Coords " + cChunkLoader.getLoc().getX() + " " + cChunkLoader.getLoc().getY() + " " + cChunkLoader.getLoc().getZ())
                        .setRunCommandAction("/tppos " + cChunkLoader.getLoc().getX() + " " + cChunkLoader.getLoc().getY() + " " + cChunkLoader.getLoc().getZ())
                        .send(player)
                );
    }


    @FCLocale(lang = LocaleType.PT_BR, text = "§4 ▶ §eTodos os seus §b%amount%§e chunkloaders foram removidos com sucesso!")
    @FCLocale(lang = LocaleType.EN_US, text = "§4 ▶ §eAll your §b%amount%§e chunkloaders were successfully removed!")
    public static LocaleMessage ALL_YOUR_CHUNKLOADERS_WERE_SUCCESSFULLY_REMOVED;

    @FinalCMD.SubCMD(
            subcmd = {"removerTudo"},
            locales = {
                    @FCLocale(lang = LocaleType.EN_US, text = "Remove all your chunkloaders"),
                    @FCLocale(lang = LocaleType.PT_BR, text = "Remove todos os seus chunkloaders")
            }
    )
    public void removerTudo(Player player, FCLPlayerData playerData) {
        List<CChunkLoader> chunkLoaders = playerData.getChunkLoaders()
                .stream()
                .collect(Collectors.toList());

        for (CChunkLoader chunkLoader : playerData.getChunkLoaders()) {
            ChunkLoaderManager.removeChunkLoader(chunkLoader.getLocation());
        }

        ALL_YOUR_CHUNKLOADERS_WERE_SUCCESSFULLY_REMOVED
                .addPlaceholder("%amount%", chunkLoaders.size())
                .send(player);
    }

    @FCLocale(lang = LocaleType.EN_US, text = "§4 ▶ §eAll [%amount%] ChunkLoaders from %player% have been removed.")
    @FCLocale(lang = LocaleType.PT_BR, text = "§4 ▶ §eTodos os [%amount%] ChunkLoaders do jogador %player% foram removidos.")
    public static LocaleMessage ALL_CHUNKLOADERS_FROM_PLAYER_HAVE_BEEN_REMOVED;

    @FinalCMD.SubCMD(
            subcmd = {"delete"},
            usage = "%name% <PlayerName>",
            permission = PermissionNodes.COMMAND_DELETE
    )
    public void delete(CommandSender sender, @Arg(name = "<Player>") FCLPlayerData target) {

        int amount = target.getChunkLoaders().size();

        for (CChunkLoader chunkLoader : new ArrayList<>(target.getChunkLoaders())) {
            ChunkLoaderManager.removeChunkLoader(chunkLoader.getLocation());
        }

        ALL_CHUNKLOADERS_FROM_PLAYER_HAVE_BEEN_REMOVED
                .addPlaceholder("%amount%", amount)
                .addPlaceholder("%player%", target.getPlayerName())
                .send(sender);

    }

    @FinalCMD.SubCMD(
            subcmd = {"purge"},
            permission = PermissionNodes.COMMAND_PURGE
    )
    public void purge(CommandSender sender) {

        List<CChunkLoader> chunkLoaders = ChunkLoaderManager.getAllChunkLoaders();

        for (CChunkLoader chunkLoader : chunkLoaders) {
            if (!chunkLoader.validateChunkLoader()) {
                ChunkLoaderManager.removeChunkLoader(chunkLoader.getLocation());
            }
        }

        sender.sendMessage(ChatColor.GOLD +"All invalid chunk loaders have been removed.");
    }

    @FinalCMD.SubCMD(
            subcmd = {"listPremium"},
            permission = PermissionNodes.COMMAND_LISTPREMIUM
    )
    public void listpremium(Player player, String label, MultiArgumentos argumentos, HelpLine helpLine) {
        player.sendMessage("--------- Premium Chunks ---------");
        ChunkLoaderManager.getAllActivePremiumChunkLoaders().forEach(cChunkLoader ->
                FancyText.of("§a§lPremium Chunk [§6§l " + (cChunkLoader.markDisabled ? "§c§l" : "") + cChunkLoader.getOwnerName() + " §a§l]")
                        .setHoverText("Coords " + cChunkLoader.getLoc().getX() + " " + cChunkLoader.getLoc().getY() + " " + cChunkLoader.getLoc().getZ())
                        .setRunCommandAction("/tppos " + cChunkLoader.getLoc().getX() + " " + cChunkLoader.getLoc().getY() + " " + cChunkLoader.getLoc().getZ() + " " + cChunkLoader.getWorldName())
                        .send(player)
                );
    }

    @FinalCMD.SubCMD(
            subcmd = {"disablePremiumChunksFromPlayer"},
            usage = "%name% <Player>",
            permission = PermissionNodes.COMMAND_ADMIN
    )
    public void disableFromPlayer(CommandSender sender, @Arg(name = "<Player>") FCLPlayerData playerData) {

        List<CChunkLoader> playersChunks = playerData.getChunkLoaders()
                .stream()
                .filter(CChunkLoader::isAlwaysOn)
                .collect(Collectors.toList());

        if (playersChunks.isEmpty()){
            sender.sendMessage("O jogador " + playerData.getPlayerName() + " não possui ChunkLoaders ");
            return;
        }

        sender.sendMessage("--------- Removed Chunks ---------");
        playersChunks.forEach(cChunkLoader ->
                FancyText.of("§a§lPremium Chunk [§6§l " + (cChunkLoader.markDisabled ? "§c§l" : "") + cChunkLoader.getOwnerName() + " §a§l]")
                        .setHoverText("Coords " + cChunkLoader.getLoc().getX() + " " + cChunkLoader.getLoc().getY() + " " + cChunkLoader.getLoc().getZ())
                        .setRunCommandAction("/tppos " + cChunkLoader.getLoc().getX() + " " + cChunkLoader.getLoc().getY() + " " + cChunkLoader.getLoc().getZ())
                        .send(sender)
                );
        sender.sendMessage("");

        for (CChunkLoader chunkLoader : playersChunks){
            chunkLoader.markDisabled = true;
            BCLForgeLib.instance().removeChunkLoader(chunkLoader);
        }
    }

    @FinalCMD.SubCMD(
            subcmd = {"reload"},
            permission = PermissionNodes.COMMAND_ADMIN
    )
    private void reload(CommandSender sender) {
        ECPluginManager.reloadPlugin(sender, FinalChunkLoader.instance);
    }
}
