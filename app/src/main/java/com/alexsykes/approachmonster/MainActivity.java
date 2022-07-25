package com.alexsykes.approachmonster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.alexsykes.approachmonster.data.ApproachDatabase;
import com.alexsykes.approachmonster.data.NavaidDao;
import com.alexsykes.approachmonster.data.NavaidViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final LatLng DEFAULT_LOCATION = new LatLng(53.355437, -2.277298);
    private final int DEFAULT_ZOOM = 10;
    private final String TAG = "Info";
    private NavaidDao navaidDao;
    private NavaidViewModel navaidViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        ApproachDatabase db = ApproachDatabase.getDatabase(this);
        navaidViewModel = new ViewModelProvider(this).get(NavaidViewModel.class);
        navaidDao = db.navaidDao();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap mMap) {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        mMap.setMinZoomPreference(6);
        mMap.setMaxZoomPreference(12);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);

        boolean success = mMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.map_style)));

        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }
//        map.addMarker(new MarkerOptions()
//                .position(DEFAULT_LOCATION)
//                .title("EGCC"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));

    }
}