package br.com.finalcraft.finalchunkloader.guis;

import br.com.finalcraft.evernifecore.gui.layout.FCLayoutScanner;
import br.com.finalcraft.finalchunkloader.guis.layout.ChunkLoaderAdminEditLayout;
import br.com.finalcraft.finalchunkloader.guis.layout.ChunkLoaderCommonEditLayout;
import br.com.finalcraft.finalchunkloader.guis.layout.ChunkLoaderPremiumEditLayout;

public class LayoutManager {

    public static ChunkLoaderCommonEditLayout EDIT_COMMON_CHUNK_LOADER_LAYOUT;
    public static ChunkLoaderPremiumEditLayout EDIT_PREMIUM_CHUNK_LOADER_LAYOUT;
    public static ChunkLoaderAdminEditLayout EDIT_ADMIN_CHUNK_LOADER_LAYOUT;

    public static void initialize(){
        EDIT_COMMON_CHUNK_LOADER_LAYOUT = FCLayoutScanner.loadLayout(ChunkLoaderCommonEditLayout.class);
        EDIT_PREMIUM_CHUNK_LOADER_LAYOUT = FCLayoutScanner.loadLayout(ChunkLoaderPremiumEditLayout.class);
        EDIT_ADMIN_CHUNK_LOADER_LAYOUT = FCLayoutScanner.loadLayout(ChunkLoaderAdminEditLayout.class);
    }

}
