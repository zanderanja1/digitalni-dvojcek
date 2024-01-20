package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final int NUM_TILES = 15;
    public static final int ZOOM = 10;
    public static final int MAP_WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static final int MAP_HEIGHT = MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static final int HUD_WIDTH = Gdx.graphics.getWidth();
    public static final int HUD_HEIGHT = Gdx.graphics.getHeight();
    public static Map<String, Geolocation> cityMap = new HashMap<>();

    static private void addCityToMap(String cityName, Geolocation geolocation) {
        cityMap.put(cityName, geolocation);
    }
    static public void initializeCityMap() {
        Geolocation MARIBOR_MARKER_GEOLOCATION = new Geolocation(46.559070, 15.638100);
        Geolocation LJUBLJANA_MARKER_GEOLOCATION = new Geolocation(46.0500268, 14.5069289);
        Geolocation PTUJ_MARKER_GEOLOCATION = new Geolocation(46.4198096, 15.8717378);
        Geolocation KOPER_MARKER_GEOLOCATION = new Geolocation(45.546540, 13.730730);
        Geolocation CELJE_MARKER_GEOLOCATION = new Geolocation(46.2293889, 15.2616828);
        Geolocation KRANJ_MARKER_GEOLOCATION = new Geolocation(46.2432913, 14.3549353);
        Geolocation KAMNIK_MARKER_GEOLOCATION = new Geolocation(46.2257358, 14.6118936);
        Geolocation AJDOVSCINA_MARKER_GEOLOCATION = new Geolocation(45.8885557, 13.9048561);
        Geolocation BLED_MARKER_GEOLOCATION = new Geolocation(46.3684202, 14.1100595);
        Geolocation CRNOMELJ_MARKER_GEOLOCATION = new Geolocation(45.5700965, 15.1923324);
        Geolocation IDRIJA_MARKER_GEOLOCATION = new Geolocation(46.0023983, 14.0273014);
        Geolocation IZOLA_MARKER_GEOLOCATION = new Geolocation(45.5398936, 13.65939);
        Geolocation VELENJE_MARKER_GEOLOCATION = new Geolocation(46.3592869, 15.1150301);
        Geolocation JESENICE_MARKER_GEOLOCATION = new Geolocation(46.4323791, 14.0623337);
        Geolocation KOCEVJE_MARKER_GEOLOCATION = new Geolocation(45.6430266, 14.8552701);
        Geolocation LENDAVA_MARKER_GEOLOCATION = new Geolocation(46.5639639, 16.4526468);
        Geolocation ORMOZ_MARKER_GEOLOCATION = new Geolocation(46.4072568, 16.1489979);
        Geolocation PIRAN_MARKER_GEOLOCATION = new Geolocation(45.527875, 13.569624);
        Geolocation PORTOROZ_MARKER_GEOLOCATION = new Geolocation(45.5146489, 13.5910112);
        Geolocation POSTOJNA_MARKER_GEOLOCATION = new Geolocation(45.7752847, 14.2139937);
        Geolocation SEVNICA_MARKER_GEOLOCATION = new Geolocation(46.0097431, 15.3037138);
        Geolocation TOLMIN_MARKER_GEOLOCATION = new Geolocation(46.1828005, 13.7328983);
        Geolocation TRBOVLJE_MARKER_GEOLOCATION = new Geolocation(46.1558964, 15.0540204);
        Geolocation VRHINKA_MARKER_GEOLOCATION = new Geolocation(45.9663607, 14.2980772);
        Geolocation ANKARAN_MARKER_GEOLOCATION = new Geolocation(45.5789629, 13.7364985);
        Geolocation BOHINJ_MARKER_GEOLOCATION = new Geolocation(46.30203365, 13.909015536346129);

        addCityToMap("Maribor", MARIBOR_MARKER_GEOLOCATION);
        addCityToMap("Ljubljana", LJUBLJANA_MARKER_GEOLOCATION);
        addCityToMap("Ptuj", PTUJ_MARKER_GEOLOCATION);
        addCityToMap("Koper", KOPER_MARKER_GEOLOCATION);
        addCityToMap("Celje", CELJE_MARKER_GEOLOCATION);
        addCityToMap("Kranj", KRANJ_MARKER_GEOLOCATION);
        addCityToMap("Kamnik", KAMNIK_MARKER_GEOLOCATION);
        addCityToMap("Ajdovscina", AJDOVSCINA_MARKER_GEOLOCATION);
        addCityToMap("Bled", BLED_MARKER_GEOLOCATION);
        addCityToMap("Crnomelj", CRNOMELJ_MARKER_GEOLOCATION);
        addCityToMap("Idrija", IDRIJA_MARKER_GEOLOCATION);
        addCityToMap("Izola", IZOLA_MARKER_GEOLOCATION);
        addCityToMap("Velenje", VELENJE_MARKER_GEOLOCATION);
        addCityToMap("Jesenice", JESENICE_MARKER_GEOLOCATION);
        addCityToMap("Kocevje", KOCEVJE_MARKER_GEOLOCATION);
        addCityToMap("Lendava", LENDAVA_MARKER_GEOLOCATION);
        addCityToMap("Ormoz", ORMOZ_MARKER_GEOLOCATION);
        addCityToMap("Piran", PIRAN_MARKER_GEOLOCATION);
        addCityToMap("Portoroz", PORTOROZ_MARKER_GEOLOCATION);
        addCityToMap("Postojna", POSTOJNA_MARKER_GEOLOCATION);
        addCityToMap("Sevnica", SEVNICA_MARKER_GEOLOCATION);
        addCityToMap("Tolmin", TOLMIN_MARKER_GEOLOCATION);
        addCityToMap("Trbovlje", TRBOVLJE_MARKER_GEOLOCATION);
        addCityToMap("Vrhinka", VRHINKA_MARKER_GEOLOCATION);
        addCityToMap("Ankaran", ANKARAN_MARKER_GEOLOCATION);
        addCityToMap("Bohinj", BOHINJ_MARKER_GEOLOCATION);
    }
}

