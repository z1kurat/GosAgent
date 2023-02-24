package com.example.gosagentnewrelease.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.gosagentnewrelease.custom.types.LotData;
import com.example.gosagentnewrelease.data.PluginModel;
import com.example.gosagentnewrelease.R;
import com.example.gosagentnewrelease.data.ReadData;
import com.example.gosagentnewrelease.adapter.TextImageProvider;
import com.example.gosagentnewrelease.data.UserData;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.GeoObject;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.Cluster;
import com.yandex.mapkit.map.ClusterListener;
import com.yandex.mapkit.map.ClusterTapListener;
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapActivity extends Activity implements ReadData.OnMarkersReadListener, ClusterListener, ClusterTapListener, MapObjectTapListener, Session.SearchListener, ImageButton.OnClickListener, TextView.OnEditorActionListener, View.OnTouchListener, RegionFragment.OnSomeRegionSelectedListener, TypeFragment.OnSomeTypeSelectedListener {
    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private Point cameraLocation = new Point(54.848064,83.092304);
    private EditText searchEdit;
    private EditText minPrice;
    private EditText maxPrice;
    private SearchManager searchManager;
    private MapView mapView;
    private TextView region;
    private TextView type;

    BottomSheetBehavior<View> BSBFragmentView;
    SparseBooleanArray SelectedRegionList;
    SparseBooleanArray SelectedTypeList;

    private RegionFragment Region;
    private TypeFragment Type;

    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPermissions();

        PluginModel.SetApiKey.Activate();
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);

        setContentView(R.layout.activity_map);

        SelectedRegionList = new SparseBooleanArray();
        SelectedTypeList = new SparseBooleanArray();

        View sheet = findViewById(R.id.sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(sheet);
        bottomSheetBehavior.setPeekHeight(250);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        View fragment_view = findViewById(R.id.fragment_region);
        BSBFragmentView = BottomSheetBehavior.from(fragment_view);
        BSBFragmentView.setPeekHeight(0);
        BSBFragmentView.setState(BottomSheetBehavior.STATE_COLLAPSED);

        minPrice = findViewById(R.id.min_price);
        maxPrice = findViewById(R.id.max_price);

        Region = new RegionFragment();
        Type = new TypeFragment();

        getFragmentManager().beginTransaction().add(R.id.fragment_region, Region).commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_region, Type).commit();

        region = findViewById(R.id.region);
        region.setOnClickListener(view -> {
            getFragmentManager().beginTransaction().replace(R.id.fragment_region, Region).commit();
            getFragmentManager().beginTransaction().show(Region);
            Region.SetSelectedItems(SelectedRegionList);
            BSBFragmentView.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        type = findViewById(R.id.type);
        type.setOnClickListener(view -> {
            getFragmentManager().beginTransaction().replace(R.id.fragment_region, Type).commit();
            getFragmentManager().beginTransaction().show(Type);
            Type.SetSelectedItems(SelectedTypeList);
            BSBFragmentView.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        mapView = findViewById(R.id.mapView);
        searchEdit = findViewById(R.id.search_edit);
        searchEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchRequest = searchEdit.getText().toString();
                if (!searchRequest.equals(""))
                    submitQuery(searchRequest);
            }
            return false;
        });

        userData = new UserData(this);

        setStartCameraPosition();
        moveCamera(Animation.Type.SMOOTH, 5);

        PluginModel.Reading.setOnMarkersReadListener(this);
        PluginModel.Reading.Markers.execute();
    }

    private void submitQuery(String query) {
        searchManager.submit(
                query,
                VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()),
                new SearchOptions(),
                this);
    }

    private void setStartCameraPosition() {
        SQLiteDatabase sqLiteDatabase = userData.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(UserData.TABLE_NAME_SETTINGS, null, null, null, null, null,null);

        double lat = 0;
        double lang = 0;
        boolean isCorrect = false;

        if (cursor.moveToFirst()) {
            int idNumber = cursor.getColumnIndex(UserData.KEY_NAME_SETTINGS);
            int idLat = cursor.getColumnIndex(UserData.KEY_SETTINGS_LAT);
            int idLang = cursor.getColumnIndex(UserData.KEY_SETTINGS_LANG);
            do {
                if (cursor.getString(idNumber).equals(PluginModel.Data.getIDCameraPosition())) {
                    isCorrect = true;
                    lat = cursor.getDouble(idLat);
                    lang = cursor.getDouble(idLang);
                    break;
                }
            } while (cursor.moveToNext());
        }

        if (!isCorrect){
            getCurrentUserPosition();
            return;
        }

        cameraLocation = new Point(lat, lang);
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

        try {
            PluginModel.Reading.Lot.executor.shutdown();
            PluginModel.Reading.Markers.executor.shutdown();
        } catch (Exception e) {
            Log.d("GosAgent", "bam!");
        }

        super.onStop();
    }

    @Override
    public void finish() {
        saveData();
        PluginModel.clear();
        super.finish();
    }

    private void saveData() {
        SQLiteDatabase sqLiteDatabase = userData.getReadableDatabase();
        Point lastLocation = mapView.getMap().getCameraPosition().getTarget();

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserData.KEY_NAME_SETTINGS, PluginModel.Data.getIDCameraPosition());
        contentValues.put(UserData.KEY_SETTINGS_LAT, lastLocation.getLatitude());
        contentValues.put(UserData.KEY_SETTINGS_LANG, lastLocation.getLongitude());

        int success = sqLiteDatabase.update(UserData.TABLE_NAME_SETTINGS, contentValues, UserData.KEY_NAME_SETTINGS + "= ?", new String[]{PluginModel.Data.getIDCameraPosition()});
        if (success == 0)
            sqLiteDatabase.insert(UserData.TABLE_NAME_SETTINGS, null, contentValues);

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
                    "Не удалось загрузить информацию по указанным лотам...",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ImageProvider imageProvider = ImageProvider.fromBitmap(PluginModel.getIconBitMap(this));
        ClusterizedPlacemarkCollection clusteredCollection =
                mapView.getMap().getMapObjects().addClusterizedPlacemarkCollection(this);

        List<Point> coordinatesLot = new ArrayList<>();


        for (LotData lot : PluginModel.Data.LotData)
            coordinatesLot.add(new Point(lot.coordinatesX, lot.coordinatesY));

        clusteredCollection.addPlacemarks(coordinatesLot, imageProvider, new IconStyle());
        clusteredCollection.addTapListener(this);
        clusteredCollection.clusterPlacemarks(25, 13);

        minPrice.setOnEditorActionListener(this);
        maxPrice.setOnEditorActionListener(this);
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
        PluginModel.Data.CoordinatesLotLat = placemarkMapObject.getGeometry().getLatitude();
        PluginModel.Data.CoordinatesLotLang = placemarkMapObject.getGeometry().getLongitude();

        Intent intent = new Intent(this, InformationActivity.class);
        startActivity(intent);

        return true;
    }

    @Override
    public void onSearchResponse(Response response) {
        try {
            GeoObject findPosition = Objects.requireNonNull(response.getCollection().getChildren().get(0).getObj());
            if (findPosition.getGeometry().isEmpty())
                return;

            cameraLocation = findPosition.getGeometry().get(0).getPoint();
            moveCamera(Animation.Type.SMOOTH, 3);
        } catch (NullPointerException exception) {
            Log.d("GosAgent", "onSearchResponse " + exception);
        }
    }

    @Override
    public void onSearchError(@NonNull Error error) {
        Toast.makeText(this, "Не удадалось найти...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        //!toDO:...
    }

    public void SetFilterItems() {
        mapView.getMap().getMapObjects().clear();

        ImageProvider imageProvider = ImageProvider.fromBitmap(PluginModel.getIconBitMap(this));
        ClusterizedPlacemarkCollection clusteredCollection =
                mapView.getMap().getMapObjects().addClusterizedPlacemarkCollection(this);

        List<String> keys = new ArrayList<>(PluginModel.Data.Country.keySet());
        List<Integer> filteredRegion = new ArrayList<>();
        List<String> filteredType = new ArrayList<>();

        for (int i = 0; i < keys.size(); i++)
            if (SelectedRegionList.get(i))
                filteredRegion.addAll(PluginModel.Data.Country.get(keys.get(i)));

        for (int i = 0; i < PluginModel.Data.TypeLot.size(); ++i)
            if (SelectedTypeList.get(i))
                filteredType.add(PluginModel.Data.TypeLot.get(i));

        double maxValue = -1;
        double minValue = -1;

        if (!maxPrice.getText().toString().isEmpty())
            maxValue = Integer.parseInt(maxPrice.getText().toString());

        if (!minPrice.getText().toString().isEmpty())
            minValue = Integer.parseInt(minPrice.getText().toString());

        List<Point> coordinatesLot = new ArrayList<>();

        for (LotData lot : PluginModel.Data.LotData) {
            if ((filteredRegion.contains(lot.subjectRFCode) || filteredRegion.size() == 0) &&
                    (filteredType.contains(lot.type) || filteredType.size() == 0) &&
                    (maxValue >= lot.initialPrice || maxValue == -1) &&
                    (minValue <= lot.initialPrice || minValue == -1))
                coordinatesLot.add(new Point(lot.coordinatesX, lot.coordinatesY));
        }

        if (coordinatesLot.size() == 0)
            return;
        clusteredCollection.addPlacemarks(coordinatesLot, imageProvider, new IconStyle());
        clusteredCollection.addTapListener(this);
        clusteredCollection.clusterPlacemarks(25, 13);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
        SetFilterItems();

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focused = this.getCurrentFocus();

        if(focused != null)
            imm.hideSoftInputFromWindow(focused.getWindowToken(), InputMethodManager.RESULT_HIDDEN);

        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: BSBFragmentView.setDraggable(false); break;
            case MotionEvent.ACTION_UP: BSBFragmentView.setDraggable(true); break;
        }
        return false;
    }

    @Override
    public void onSomeRegionSelected(SparseBooleanArray selected) {
        SelectedRegionList = selected;
        SetFilterItems();

        List<String> keys = new ArrayList<>(PluginModel.Data.Country.keySet());
        StringBuilder resultView = new StringBuilder();
        for (int i = 0; i < keys.size(); ++i)
            if (SelectedRegionList.get(i)) {
                resultView.append(keys.get(i));
                resultView.append(", ");
            }

        resultView.trimToSize();
        if (resultView.length() > 0)
            resultView = new StringBuilder(resultView.substring(0, resultView.length() - 2));

        region.setText(resultView.toString());
    }

    @Override
    public void onSomeTypeSelected(SparseBooleanArray selected) {
        SelectedTypeList = selected;
        SetFilterItems();

        StringBuilder resultView = new StringBuilder();
        for (int i = 0; i < PluginModel.Data.TypeLot.size(); ++i)
            if (SelectedTypeList.get(i)) {
                resultView.append(PluginModel.Data.TypeLot.get(i));
                resultView.append(", ");
            }

        resultView.trimToSize();
        if (resultView.length() > 0)
            resultView = new StringBuilder(resultView.substring(0, resultView.length() - 2));

        type.setText(resultView.toString());
    }
}

