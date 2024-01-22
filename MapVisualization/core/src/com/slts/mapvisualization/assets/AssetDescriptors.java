package com.slts.mapvisualization.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetDescriptors {

    public static final AssetDescriptor<TextureAtlas> WEATHER =
            new AssetDescriptor<TextureAtlas>(AssetPaths.WEATHER, TextureAtlas.class);


    private AssetDescriptors() {
    }
}
