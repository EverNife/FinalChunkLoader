package br.com.finalcraft.finalchunkloader.guis.gui;

import br.com.finalcraft.evernifecore.cooldown.Cooldown;
import br.com.finalcraft.evernifecore.gui.PlayerGui;
import br.com.finalcraft.evernifecore.gui.layout.IHasLayout;
import br.com.finalcraft.evernifecore.placeholder.replacer.CompoundReplacer;
import br.com.finalcraft.evernifecore.placeholder.replacer.RegexReplacer;
import br.com.finalcraft.finalchunkloader.FinalChunkLoader;
import br.com.finalcraft.finalchunkloader.chunkloader.CChunkLoader;
import br.com.finalcraft.finalchunkloader.config.data.FCLPlayerData;
import br.com.finalcraft.finalchunkloader.config.datastore.ChunkLoaderManager;
import br.com.finalcraft.finalchunkloader.config.settings.ChunkLoaderType;
import br.com.finalcraft.finalchunkloader.guis.LayoutManager;
import br.com.finalcraft.finalchunkloader.guis.layout.base.ChunkLoaderEditLayout;
import br.com.finalcraft.finalchunkloader.messages.FCLMessages;
import br.com.finalcraft.libs.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;

public class ChunkLoaderEditGui extends PlayerGui<FCLPlayerData, Gui> implements IHasLayout<ChunkLoaderEditLayout> {

    private final FCLPlayerData viewer;
    private final CChunkLoader chunkLoader;

    public ChunkLoaderEditGui(FCLPlayerData viewer, CChunkLoader chunkLoader) {
        super(chunkLoader.getOwner());
        this.viewer = viewer;
        this.chunkLoader = chunkLoader;

        setupLayout(this);
    }

    @Override
    public ChunkLoaderEditLayout layout() {
        return chunkLoader.isAdminChunkLoader()
                ? LayoutManager.EDIT_ADMIN_CHUNK_LOADER_LAYOUT
                : chunkLoader.getChunkLoaderType() == ChunkLoaderType.ONLINE_ONLY
                    ? LayoutManager.EDIT_COMMON_CHUNK_LOADER_LAYOUT
                    : LayoutManager.EDIT_PREMIUM_CHUNK_LOADER_LAYOUT;
    }

    @Override
    public Player getPlayer() {
        return viewer.getPlayer();
    }

    @Override
    public void open(){

        getPlayerData().recalculateGroupLimitsIfNecessary();

        if (chunkLoader.getRange() >= 0){
            layout().getRemoveSlot().applyTo(this)
                    .setAction(inventoryClickEvent -> {
                        ChunkLoaderManager.removeChunkLoader(chunkLoader.getLocation());
                        FCLMessages.CHUNK_LOADER_SUCCESSFULLY_REMOVED
                                .send(viewer.getPlayer());
                        this.close();
                    });
        }

        boolean isSomeoneElse = getPlayerData() != viewer;

        for (int index = 0; index < layout().getChangeChunk().getSlot().length; index++) {
            int slot = layout().getChangeChunk().getSlot()[index];

            int range = index;
            int newTotalChunks = ( 1 + (2 * range)) * ( 1 + (2 * range)); // [1,3,5,7,9] ^ 2
            boolean isSelectedRange = chunkLoader.getRange() == index;

            CompoundReplacer replacer = new RegexReplacer<>()
                    .addParser("range",o -> (range + 1))
                    .addParser("total_chunks",o -> newTotalChunks)
                    .compound(null);

            if (isSelectedRange){
                layout().getChangeChunkSelected()
                        .asLayoutBuilder()
                        .setSlot(slot)
                        .build()
                        .parse(replacer)
                        .applyTo(this);
            }else {
                layout().getChangeChunk()
                        .asLayoutBuilder()
                        .setSlot(slot)
                        .build()
                        .parse(replacer)
                        .applyTo(this)
                        .setAction(inventoryClickEvent -> {

                            if (!chunkLoader.isAdminChunkLoader()) {
                                int needed = newTotalChunks;
                                int remaining = getPlayerData().getChunksLimitRemaining(chunkLoader.getChunkLoaderType());
                                int available = remaining + chunkLoader.totalChunks();

                                if (available < needed){
                                    if (isSomeoneElse){
                                        FCLMessages.THE_PLAYER_DOES_NOT_HAVE_ENOUGH_FREE_CHUNKS
                                                .addPlaceholder("%need%", needed)
                                                .addPlaceholder("%available%", available)
                                                .addPlaceholder("%player%", getPlayerData().getPlayerName())
                                                .send(getPlayer());
                                    }else {
                                        FCLMessages.NOT_ENOUGH_FREE_CHUNKS
                                                .addPlaceholder("%need%", needed)
                                                .addPlaceholder("%available%", available)
                                                .send(getPlayer());
                                    }
                                    return;
                                }
                            }

                            Cooldown cooldown = viewer.getCooldown("FCL_EDIT_CHUNK_SIZE");
                            if (cooldown.isInCooldown()){
                                cooldown.warnPlayer(viewer.getPlayer());
                                return;
                            }
                            cooldown.startWith(2);

                            if (!chunkLoader.validateChunkLoader()){
                                FCLMessages.CHUNK_LOADER_IS_NOT_VALID.send(getPlayer());
                                this.close();
                                return;
                            }

                            FinalChunkLoader.getLog().info("[%s] edited [%s]'s chunk loader at [%s] range from [%s] to [%s]",
                                    getPlayer().getName(),
                                    chunkLoader.getOwnerName(),
                                    chunkLoader.getLocationString(),
                                    chunkLoader.getRange(),
                                    newTotalChunks
                            );

                            ChunkLoaderManager.removeChunkLoader(chunkLoader.getLocation());

                            chunkLoader.setRange(range);
                            chunkLoader.getOwner().forceSavePlayerData();

                            ChunkLoaderManager.setChunkLoader(chunkLoader.getLocation(), chunkLoader);

                            FCLMessages.CHUNK_LOADER_SUCCESSFULLY_UPDATED
                                    .addPlaceholder("%size%", chunkLoader.sizeInString())
                                    .send(getPlayer());

                            this.open();
                        });
            }

        }


        super.open();
    }

}
