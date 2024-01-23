package com.slts.mapvisualization.utils;

import com.badlogic.gdx.Gdx;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final int NUM_TILES = 15;
    public static final int ZOOM = 10;
    public static final int MAP_WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static final int MAP_HEIGHT = MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static final float ICON_WIDTH = 125;
    public static final float ICON_HEIGHT = 125;
    public static final int HUD_WIDTH = Gdx.graphics.getWidth();
    public static final int HUD_HEIGHT = Gdx.graphics.getHeight();
    public static final String REQUEST_URL = "http://192.168.0.27:3001/cities";

}

