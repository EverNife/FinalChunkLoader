package br.com.finalcraft.finalchunkloader.guis.layout;

import br.com.finalcraft.evernifecore.gui.layout.DefaultIcons;
import br.com.finalcraft.evernifecore.gui.layout.LayoutBaseData;
import br.com.finalcraft.evernifecore.gui.layout.LayoutIcon;
import br.com.finalcraft.evernifecore.gui.layout.LayoutIconData;
import br.com.finalcraft.evernifecore.gui.util.EnumStainedGlassPane;
import br.com.finalcraft.evernifecore.itemstack.FCItemFactory;
import br.com.finalcraft.evernifecore.locale.FCLocale;
import br.com.finalcraft.evernifecore.locale.LocaleType;
import br.com.finalcraft.finalchunkloader.guis.layout.base.ChunkLoaderEditLayout;
import org.bukkit.Material;

@LayoutBaseData(
        title = "➲  §0§lChunkLoader",
        rows = 5
)
public class ChunkLoaderCommonEditLayout extends ChunkLoaderEditLayout {

    @LayoutIconData(
            slot = {19},
            locale = {
                    @FCLocale(lang = LocaleType.EN_US, text = "§c§lRemove", hover = "" +
                            "§2§m------------------------------------§r" +
                            "\n" +
                            "\n§c ✎ Click here to REMOVE this chunkloader!" +
                            "\n" +
                            "\n§2§m------------------------------------§r"),
                    @FCLocale(lang = LocaleType.PT_BR, text = "§c§lRemover", hover = "" +
                            "§2§m------------------------------------§r" +
                            "\n" +
                            "\n§c ✎ Clique aqui para REMOVER esse chunkloader!" +
                            "\n" +
                            "\n§2§m------------------------------------§r")
            }
    )
    public LayoutIcon REMOVE_SLOT = DefaultIcons.getDenyButton();

    @LayoutIconData(
            slot = {21, 22, 23, 24, 25},
            locale = {
                    @FCLocale(lang = LocaleType.EN_US, text = "§a§lChance size to §b§l%range%§a", hover = "" +
                            "§2§m------------------------------------§r" +
                            "\n" +
                            "\n§6 ◆ §aClick to define size to: §b%range%" +
                            "\n§7      It will consume %total_chunks% chunks!" +
                            "\n" +
                            "\n§2§m------------------------------------§r"),
                    @FCLocale(lang = LocaleType.PT_BR, text = "§a§lAlterar Tamanho para §b§l%range%§a", hover = "" +
                            "§2§m------------------------------------§r" +
                            "\n" +
                            "\n§6 ◆ §aClique para definir o tamanho para %range%!" +
                            "\n§7      Vai consumir %total_chunks% chunks!" +
                            "\n" +
                            "\n§2§m------------------------------------§r")
            }

    )
    public LayoutIcon CHANGE_CHUNK = FCItemFactory.from(Material.MAP)
            .asLayout();

    @LayoutIconData(
            slot = {},
            locale = {
                    @FCLocale(lang = LocaleType.EN_US, text = "§a§lActual Size: §b§l%range%§a§l!", hover = "" +
                            "§2§m------------------------------------§r" +
                            "\n" +
                            "\n" +
                            "\n§7 Its consuming %total_chunks% chunks!" +
                            "\n" +
                            "\n§2§m------------------------------------§r"),
                    @FCLocale(lang = LocaleType.PT_BR, text = "§a§lTamanho Atual: §b§l%range%§a§l!", hover = "" +
                            "§2§m------------------------------------§r" +
                            "\n" +
                            "\n" +
                            "\n§7 Está consumindo %total_chunks% chunks!" +
                            "\n" +
                            "\n§2§m------------------------------------§r")
            }

    )
    public LayoutIcon CHANGE_CHUNK_SELECTED = FCItemFactory.from(Material.EMERALD_BLOCK)
            .asLayout();

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
                            "\n§7It only works while you are online!" +
                            "\n" +
                            "\n§2§m------------------------------------§r"),
                    @FCLocale(lang = LocaleType.PT_BR, text = "§a§lInfo", hover = "" +
                            "§2§m------------------------------------§r" +
                            "\n" +
                            "\n§7Escolha o tamanho do seu chunkloder!" +
                            "\n§7Ele só funciona enquanto você estiver online!" +
                            "\n" +
                            "\n§2§m------------------------------------§r")
            }
    )
    public LayoutIcon INFO_SLOT = DefaultIcons.getInformationButton();

    @LayoutIconData(
            slot = {0, 1, 2, 3, 5, 6, 7, 8, 36, 37, 38, 39, 40, 41, 42, 43, 44},
            background = true
    )
    public LayoutIcon IRON_BLOCK = FCItemFactory.from(Material.IRON_BLOCK)
            .displayName("§7§l❂")
            .asLayout();

    @LayoutIconData(
            slot = {9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35},
            background = true
    )
    public LayoutIcon WHITE_BACKGROUND = EnumStainedGlassPane.WHITE
            .asLayout();

}
