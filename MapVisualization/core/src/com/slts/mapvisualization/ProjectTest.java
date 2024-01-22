package com.slts.mapvisualization;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.slts.mapvisualization.assets.AssetDescriptors;
import com.slts.mapvisualization.assets.RegionNames;
import com.slts.mapvisualization.utils.Constants;
import com.slts.mapvisualization.utils.ZoomXY;
import com.slts.mapvisualization.utils.Geolocation;
import com.slts.mapvisualization.utils.MapRasterTiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ProjectTest extends ApplicationAdapter implements GestureDetector.GestureListener {

    private ShapeRenderer shapeRenderer;
    private Vector3 touchPosition;
    private Image weatherIcon;

    private String weather = "Info";
    private Label weatherText;
    private BitmapFont font;
    private TiledMap tiledMap;
    private TextureAtlas weatherAtlas;
    private Map<String, Circle> textBounds = new HashMap<>();
    private SpriteBatch batch;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private Texture[] mapTiles;
    private ZoomXY beginTile;

    private SpriteBatch spriteBatch;

    // buttons
    private FitViewport hudViewport;
    private Stage hudStage;
    private Skin skin;

    // animation
    private Stage stage;
    private FitViewport viewport;
    private AssetManager assetManager;


    // boat animation

    // center geolocation
    //46.059802, 14.753392
    private final Geolocation CENTER_GEOLOCATION = new Geolocation(46.059802, 14.753392);
    //private final Geolocation CENTER_GEOLOCATION = new Geolocation(46.559070, 15.638100);

    // test marker


    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        assetManager = new AssetManager();
        assetManager.load(AssetDescriptors.WEATHER);
        assetManager.finishLoading();
        weatherAtlas = assetManager.get(AssetDescriptors.WEATHER);
        font = new BitmapFont(Gdx.files.internal("ui/font/default.fnt"));
        font.getData().setScale(5);

        Constants.initializeCityMap();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        camera.position.set(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, 0);
        camera.viewportWidth = Constants.MAP_WIDTH / 2f;
        camera.viewportHeight = Constants.MAP_HEIGHT / 2f;
        camera.zoom = 2f;
        camera.update();

        spriteBatch = new SpriteBatch();
        hudViewport = new FitViewport(Constants.HUD_WIDTH, Constants.HUD_HEIGHT);
        viewport = new FitViewport(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, camera);

        touchPosition = new Vector3();

        try {
            //in most cases, geolocation won't be in the center of the tile because tile borders are predetermined (geolocation can be at the corner of a tile)
            ZoomXY centerTile = MapRasterTiles.getTileNumber(CENTER_GEOLOCATION.lat, CENTER_GEOLOCATION.lng, Constants.ZOOM);
            mapTiles = MapRasterTiles.getRasterTileZone(centerTile, Constants.NUM_TILES);
            //you need the beginning tile (tile on the top left corner) to convert geolocation to a location in pixels.
            beginTile = new ZoomXY(Constants.ZOOM, centerTile.x - ((Constants.NUM_TILES - 1) / 2), centerTile.y - ((Constants.NUM_TILES - 1) / 2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tiledMap = new TiledMap();
        MapLayers layers = tiledMap.getLayers();

        TiledMapTileLayer layer = new TiledMapTileLayer(Constants.NUM_TILES, Constants.NUM_TILES, MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE);
        int index = 0;
        for (int j = Constants.NUM_TILES - 1; j >= 0; j--) {
            for (int i = 0; i < Constants.NUM_TILES; i++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(new TextureRegion(mapTiles[index], MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE)));
                layer.setCell(i, j, cell);
                index++;
            }
        }
        layers.add(layer);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // buttons
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        hudStage = new Stage(hudViewport, spriteBatch);
        hudStage.addActor(createButtons());

        for (Actor a : weatherInfo()) {
            hudStage.addActor(a);
        }

        Gdx.input.setInputProcessor(new InputMultiplexer(hudStage, new GestureDetector(this)));

        // boat

        stage = new Stage(viewport, spriteBatch);

    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        //handleInputMapResize();

        camera.update();

        handleInput();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        hudStage.act(Gdx.graphics.getDeltaTime());
        stage.act(Gdx.graphics.getDeltaTime());

        updateWeatherInfo();
        hudStage.draw();
        stage.draw();

        drawMarkers();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Circle bounds : textBounds.values()) {
            shapeRenderer.rect(bounds.x, bounds.y, bounds.radius, bounds.radius);
        }
        shapeRenderer.end();

    }

    private void drawMarkers() {
        for (Map.Entry<String, Geolocation> entry : Constants.cityMap.entrySet()) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            Geolocation geoLocation = entry.getValue();
            String cityName = entry.getKey();


            Vector2 marker = MapRasterTiles.getPixelPosition(geoLocation.lat, geoLocation.lng, beginTile.x, beginTile.y);

            // Draw rectangle for each city
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.point(marker.x, marker.y, 40);
            shapeRenderer.end();

            // Draw text for city name next to the rectangle
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            if(cityName == "Piran" || cityName == "Izola") {
                batch.end();
                continue;
            }
            else {
                font.draw(batch, cityName, marker.x + 25, marker.y + 20);
                addClickableText(cityName,marker.x +25 , marker.y + 20 , font);

            }
            batch.end();
        }

    }
    private void addClickableText(String text, float x, float y, BitmapFont font) {
        GlyphLayout layout = new GlyphLayout(font, text);

        Circle bounds = new Circle(x - 30 , y, 30);
        textBounds.put(text, bounds);
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        Vector3 touchPos = new Vector3(x, y, 0);
        camera.unproject(touchPos);

        float touchX = touchPos.x;
        float touchY = touchPos.y;

        for (Map.Entry<String, Circle> entry : textBounds.entrySet()) {
            if (entry.getValue().contains(touchX, touchY)) {

                Gdx.app.log("Click", "Clicked on text: " + entry.getKey());
                System.out.println(entry.getKey());

                weather = "vreme v " + entry.getKey() + ": soncno\n temperatura: 19ºC";



                break;
            }
        }

        return true;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        hudStage.dispose();
    }



    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        camera.translate(-deltaX, deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (initialDistance >= distance)
            camera.zoom += 0.02;
        else
            camera.zoom -= 0.02;
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -3, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 3, 0);
        }

        camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 2f);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, Constants.MAP_WIDTH - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, Constants.MAP_HEIGHT - effectiveViewportHeight / 2f);
    }
    private Array<Actor> weatherInfo(){
        Array<Actor> info = new Array<Actor>();
        weatherText = new Label(weather, skin);
        weatherText.setPosition(50f,100f);
        weatherText.setScaleX(5f);

        weatherIcon = new Image(weatherAtlas.findRegion(RegionNames.SUN));
        weatherIcon.setPosition(50f,130f);
        weatherIcon.setSize(30f,30f);

        info.add(weatherText);
        info.add(weatherIcon);
        return info;
    }
    private void updateWeatherInfo(){

        weatherText.setText(weather);

    }

    private Actor createButtons() {
        Table table = new Table();
        table.defaults().pad(20);

        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table buttonTable = new Table();
        buttonTable.defaults().padLeft(30).padRight(30);

        buttonTable.add(quitButton).fillX();


        table.add(buttonTable);
        table.left();
        table.top();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}
