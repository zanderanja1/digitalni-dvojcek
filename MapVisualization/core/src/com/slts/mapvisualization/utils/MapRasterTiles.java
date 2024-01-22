package com.slts.mapvisualization.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MapRasterTiles {
    //Mapbox
    //https://docs.mapbox.com/api/maps/raster-tiles/
    static String mapServiceUrl = "https://api.mapbox.com/v4/";
    static String token = "?access_token=" + Keys.MAPBOX;
    static String tilesetId = "mapbox.satellite";
    //static String marker = "Maribor-city.svg+194D33(46.559070,15.638100)";
    static String format = "@2x.jpg90";
    final static public int TILE_SIZE = 512;


    public static Texture getRasterTile(int zoom, int x, int y) throws IOException {
        URL url = new URL(mapServiceUrl + tilesetId + "/" + zoom + "/" + x + "/" + y + format + token);
        ByteArrayOutputStream bis = fetchTile(url);
        return getTexture(bis.toByteArray());
    }


    public static Texture getRasterTile(String zoomXY) throws IOException {
        URL url = new URL(mapServiceUrl + tilesetId + "/" + zoomXY + format + token);
        ByteArrayOutputStream bis = fetchTile(url);
        return getTexture(bis.toByteArray());
    }


    private static void renderTile(Texture tile, float x, float y) {
        // Render the tile using SpriteBatch or any rendering mechanism of your choice
        SpriteBatch spriteBatch = new SpriteBatch();
        spriteBatch.begin();
        spriteBatch.draw(tile, x, y);
        spriteBatch.end();
    }
    private static float getLatitude(float y, int zoom) {
        double latitude = tile2lat((int) (y / TILE_SIZE), zoom);
        return (float) latitude;
    }

    private static float getLongitude(float x, int zoom) {
        double longitude = tile2long((int) (x / TILE_SIZE), zoom);
        return (float) longitude;
    }

    public static Texture getRasterTile(ZoomXY zoomXY) throws IOException {
        URL url = new URL(mapServiceUrl + tilesetId + "/" + zoomXY.toString() + format + token);
        ByteArrayOutputStream bis = fetchTile(url);
        return getTexture(bis.toByteArray());
    }

    public static Texture[] getRasterTileZone(ZoomXY zoomXY, int size) throws IOException {
        Texture[] array = new Texture[size * size];
        int[] factorY = new int[size * size]; //if size is 3 {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        int[] factorX = new int[size * size]; //if size is 3 {-1, 0, 1, -1, 0, 1, -1, 0, 1};

        int value = (size - 1) / -2;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                factorY[i * size + j] = value;
                factorX[i + j * size] = value;
            }
            value++;
        }

        for (int i = 0; i < size * size; i++) {
            array[i] = getRasterTile(zoomXY.zoom, zoomXY.x + factorX[i], zoomXY.y + factorY[i]);
            System.out.println(zoomXY.zoom + "/" + (zoomXY.x + factorX[i]) + "/" + (zoomXY.y + factorY[i]));
        }
        return array;
    }

    public static ByteArrayOutputStream fetchTile(URL url) throws IOException {
        ByteArrayOutputStream bis = new ByteArrayOutputStream();
        InputStream is = url.openStream();
        byte[] bytebuff = new byte[4096];
        int n;

        while ((n = is.read(bytebuff)) > 0) {
            bis.write(bytebuff, 0, n);
        }
        return bis;
    }

    public static Texture getTexture(byte[] array) {
        return new Texture(new Pixmap(array, 0, array.length));
    }



    public static ZoomXY getTileNumber(final double lat, final double lon, final int zoom) {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return new ZoomXY(zoom, xtile, ytile);
    }

    public static double tile2long(int tileNumberX, int zoom) {
        return (tileNumberX / Math.pow(2, zoom) * 360 - 180);
    }

    public static double tile2lat(int tileNumberY, int zoom) {

        double n = Math.PI - 2 * Math.PI * tileNumberY / Math.pow(2, zoom);
        return (180 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n))));
    }

    public static double[] project(double lat, double lng, int tileSize) {
        double siny = Math.sin((lat * Math.PI) / 180);

        // Truncating to 0.9999 effectively limits latitude to 89.189. This is
        // about a third of a tile past the edge of the world tile.
        siny = Math.min(Math.max(siny, -0.9999), 0.9999);

        return new double[]{
                tileSize * (0.5 + lng / 360),
                tileSize * (0.5 - Math.log((1 + siny) / (1 - siny)) / (4 * Math.PI))
        };
    }


    public static Vector2 getPixelPosition(double lat, double lng, int tileSize, int zoom, int beginTileX, int beginTileY, int height) {
        double[] worldCoordinate = project(lat, lng, tileSize);
        // Scale to fit our image
        double scale = Math.pow(2, zoom);

        // Apply scale to world coordinates to get image coordinates
        return new Vector2(
                (int) (Math.floor(worldCoordinate[0] * scale) - (beginTileX * tileSize)),
                height - (int) (Math.floor(worldCoordinate[1] * scale) - (beginTileY * tileSize) - 1)
        );
    }

    public static Vector2 getPixelPosition(double lat, double lng, int beginTileX, int beginTileY) {
        double[] worldCoordinate = project(lat, lng, MapRasterTiles.TILE_SIZE);
        // Scale to fit our image
        double scale = Math.pow(2, Constants.ZOOM);

        // Apply scale to world coordinates to get image coordinates
        return new Vector2(
                (int) (Math.floor(worldCoordinate[0] * scale) - (beginTileX * MapRasterTiles.TILE_SIZE)),
                Constants.MAP_HEIGHT - (int) (Math.floor(worldCoordinate[1] * scale) - (beginTileY * MapRasterTiles.TILE_SIZE) - 1)
        );
    }
}
