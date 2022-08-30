package com.example.gosagentnewrelease;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.Cluster;
import com.yandex.mapkit.map.ClusterListener;
import com.yandex.mapkit.map.ClusterTapListener;
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends Activity implements ReadData.OnMarkersReadListener, ClusterListener, ClusterTapListener, MapObjectTapListener, com.yandex.mapkit.search.Session.SearchListener, CameraListener  {
    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private Point cameraLocation = new Point(54.848064,83.092304);
    private EditText searchEdit;
    private SearchManager searchManager;
    private com.yandex.mapkit.search.Session searchSession;
    private MapView mapView;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getPermissions();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MapActivity.this);

        SetApiKey.Activate();
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);

        setContentView(R.layout.activity_map);
        super.onCreate(savedInstanceState);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        mapView = findViewById(R.id.mapView);
        mapView.getMap().addCameraListener(this);
        searchEdit = findViewById(R.id.search_edit);
        searchEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
                submitQuery(searchEdit.getText().toString());

            return false;
        });

        submitQuery(searchEdit.getText().toString());

        setStartCameraPosition();
        moveCamera(Animation.Type.SMOOTH, 5);

        PluginModel.Reading.setOnMarkersReadListener(this);
        PluginModel.Reading.Markers.execute();
    }

    private void submitQuery(String query) {
        searchSession = searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()),
                new SearchOptions(),
                this);
    }

    private void setStartCameraPosition() {
        String nameKey = PluginModel.Data.getIDCameraPosition();

        if (!sharedPreferences.contains(nameKey)) {
            getCurrentUserPosition();
            return;
        }

        String position = sharedPreferences.getString(nameKey, "");
        String[] coordinates = position.split(",");

        cameraLocation = new Point(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
    }

    private void getCurrentUserPosition() {
        LocationManager locationManager = MapKitFactory.getInstance().createLocationManager();
        locationManager.requestSingleUpdate(new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
                cameraLocation = location.getPosition();
                moveCamera(Animation.Type.SMOOTH, 5);
            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) { }
        });
    }

    private void getPermissions() {
        ActivityCompat.requestPermissions(MapActivity.this,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void moveCamera(Animation.Type animationType, float duration) {
        mapView.getMap().move(
                new CameraPosition(cameraLocation, 14.0f, 0.0f, 0.0f),
                new Animation(animationType, duration),
                null);
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    public void finish() {
        saveData();
        PluginModel.clear();
        super.finish();
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Point lastLocation = mapView.getMap().getCameraPosition().getTarget();
        String lat = String.valueOf(lastLocation.getLatitude());
        String lang = String.valueOf(lastLocation.getLongitude());

        editor.putString(PluginModel.Data.getIDCameraPosition(),  lat + "," + lang);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onMarkersRead(Boolean isDone) {
        if (!isDone) {
            Toast.makeText(
                    this,
                    "Не удалось загрузить информацию по указанным лотам.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ImageProvider imageProvider = ImageProvider.fromBitmap(PluginModel.getIconBitMap(this));
        ClusterizedPlacemarkCollection clusteredCollection =
                mapView.getMap().getMapObjects().addClusterizedPlacemarkCollection(this);

        List<Point> coordinatesLot = new ArrayList<>();

        for (MarkerData lot : PluginModel.Data.Markers)
            coordinatesLot.add(new Point(lot.coordinatesX, lot.coordinatesY));

        clusteredCollection.addPlacemarks(coordinatesLot, imageProvider, new IconStyle());
        clusteredCollection.addTapListener(this);
        clusteredCollection.clusterPlacemarks(25, 13);
    }

    @Override
    public void onClusterAdded(Cluster cluster) {
        cluster.getAppearance().setIcon(new TextImageProvider(Integer.toString(cluster.getSize()), this));
        cluster.addClusterTapListener(this);
    }

    @Override
    public boolean onClusterTap(@NonNull Cluster cluster) {
        List<PlacemarkMapObject> mapObjects = cluster.getPlacemarks();
        double centralPointX = 0;
        double centralPointY = 0;

        for (PlacemarkMapObject mapObject : mapObjects) {
            centralPointX += mapObject.getGeometry().getLatitude();
            centralPointY += mapObject.getGeometry().getLongitude();
        }

        cameraLocation = new Point(centralPointX / mapObjects.size(), centralPointY / mapObjects.size());
        moveCamera(Animation.Type.LINEAR, 1);

        return true;
    }

    @Override
    public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
        if (!(mapObject instanceof PlacemarkMapObject))
            return false;

        PlacemarkMapObject placemarkMapObject = (PlacemarkMapObject) mapObject;
        PluginModel.Data.CoordinatesLot = new String[]{String.valueOf(placemarkMapObject.getGeometry().getLatitude()),
                                                       String.valueOf(placemarkMapObject.getGeometry().getLongitude())};

        Intent intent = new Intent(this, InformationActivity.class);
        startActivity(intent);

        return true;
    }

    @Override
    public void onSearchResponse(Response response) {
        Point x = response.getCollection().getChildren().get(0).getObj().getGeometry().get(0).getPoint();
        if (x == null)
            return;
        cameraLocation =x;

        moveCamera(Animation.Type.SMOOTH, 5);
    }

    @Override
    public void onSearchError(@NonNull Error error) {
        Toast.makeText(this, "Не удадалось найти...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraPositionChanged(Map map, CameraPosition cameraPosition,
            CameraUpdateReason cameraUpdateReason,
            boolean finished) {
        if (finished) {
            //submitQuery(searchEdit.getText().toString());
        }
    }

}

