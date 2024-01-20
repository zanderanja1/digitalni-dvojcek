package com.mygdx.game;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
public class AssetPacker {

    private static final boolean DRAW_DEBUG_OUTLINE = false;

    private static final String RAW_ASSETS_PATH = "desktop/assets-raw";
    private static final String ASSETS_PATH = "core/assets";

    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 8192;
        settings.maxHeight = 8192;
        settings.debug = DRAW_DEBUG_OUTLINE;

        TexturePacker.process(settings,
                RAW_ASSETS_PATH + "/weather",
                ASSETS_PATH + "/weather",
                "weather"
        );

    }
}
