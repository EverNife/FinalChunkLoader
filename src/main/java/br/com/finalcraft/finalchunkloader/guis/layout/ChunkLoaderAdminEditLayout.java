package br.com.finalcraft.finalchunkloader.guis.layout;

import br.com.finalcraft.evernifecore.gui.layout.DefaultIcons;
import br.com.finalcraft.evernifecore.gui.layout.LayoutBaseData;
import br.com.finalcraft.evernifecore.gui.layout.LayoutIcon;
import br.com.finalcraft.evernifecore.gui.layout.LayoutIconData;
import br.com.finalcraft.evernifecore.gui.util.EnumStainedGlassPane;
import br.com.finalcraft.evernifecore.itemstack.FCItemFactory;
import br.com.finalcraft.evernifecore.locale.FCLocale;
import br.com.finalcraft.evernifecore.locale.LocaleType;
import br.com.finalcraft.finalchunkloader.guis.LayoutManager;
import br.com.finalcraft.finalchunkloader.guis.layout.base.ChunkLoaderEditLayout;
import org.bukkit.Material;

@LayoutBaseData(
        title = "➲  §6§lChunkLoader Admin",
        rows = 5
)
public class ChunkLoaderAdminEditLayout extends ChunkLoaderEditLayout {

    @LayoutIconData(
            slot = {19}
    )
    public LayoutIcon REMOVE_SLOT = LayoutManager.EDIT_COMMON_CHUNK_LOADER_LAYOUT.REMOVE_SLOT;

    @LayoutIconData(
            slot = {21, 22, 23, 24, 25}
    )
    public LayoutIcon CHANGE_CHUNK = LayoutManager.EDIT_COMMON_CHUNK_LOADER_LAYOUT.CHANGE_CHUNK;

    @LayoutIconData(
            slot = {}
    )
    public LayoutIcon CHANGE_CHUNK_SELECTED = LayoutManager.EDIT_COMMON_CHUNK_LOADER_LAYOUT.CHANGE_CHUNK_SELECTED;

    @Override
    public LayoutIcon getRemoveSlot() {
        return REMOVE_SLOT;
    }

    @Override
    public LayoutIcon getChangeChunk() {
        return CHANGE_CHUNK;
    }

    @Override
    public LayoutIcon getChangeChunkSelected() {
        return CHANGE_CHUNK_SELECTED;
    }

    // -----------------------------------------------------------------------------------------------------------------
    //  BackGround
    // -----------------------------------------------------------------------------------------------------------------

    @LayoutIconData(
            slot = {4},
            background = true,
            locale = {
                    @FCLocale(lang = LocaleType.EN_US, text = "§a§lInfo", hover = "" +
                            "§2§m------------------------------------§r" +
                            "\n" +
                            "\n§7Choose the size of your chunkloder!" +
                            "\n§7It works while you are offline!" +
                            "\n" +
                            "\n§2§m------------------------------------§r"),
                    @FCLocale(lang = LocaleType.PT_BR, text = "§a§lInfo", hover = "" +
                            "§2§m------------------------------------§r" +
                            "\n" +
                            "\n§7Escolha o tamanho do seu chunkloder!" +
                            "\n§7Ele funciona enquanto você estiver offline!" +
                            "\n" +
                            "\n§2§m------------------------------------§r")
            }
    )
    public LayoutIcon INFO_SLOT = DefaultIcons.getInformationButton();

    @LayoutIconData(
            slot = {0, 1, 7, 8, 39, 40, 41},
            background = true
    )
    public LayoutIcon DIAMOND_BLOCK = FCItemFactory.from(Material.DIAMOND_BLOCK)
            .asLayout();

    @LayoutIconData(
            slot = {2, 3, 4, 5, 6, 36, 37, 38, 42, 43, 44},
            background = true
    )
    public LayoutIcon GOLD_BLOCK = FCItemFactory.from(Material.GOLD_BLOCK)
            .asLayout();

    @LayoutIconData(
            slot = {9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35},
            background = true
    )
    public LayoutIcon YELLOW_BACKGROUND = EnumStainedGlassPane.YELLOW
            .asLayout();

}
