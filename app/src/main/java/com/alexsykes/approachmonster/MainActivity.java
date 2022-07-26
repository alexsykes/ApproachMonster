package com.alexsykes.approachmonster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.alexsykes.approachmonster.data.ApproachDatabase;
import com.alexsykes.approachmonster.data.Navaid;
import com.alexsykes.approachmonster.data.NavaidDao;
import com.alexsykes.approachmonster.data.NavaidRepository;
import com.alexsykes.approachmonster.data.NavaidViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private final LatLng DEFAULT_LOCATION = new LatLng(53.355437, -2.277298);
    private final int DEFAULT_ZOOM = 9;
    private final String TAG = "Info";
    private NavaidDao navaidDao;
    private NavaidViewModel navaidViewModel;
    List<Navaid> airfieldList, vrpList, vorList, waypointList;

    TextView infoBoxTitleTextView, navaidNameTextView, navaidDetailTextView, navaidTypeTextView;

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

        airfieldList = navaidViewModel.getAllAirfields();
        vrpList = navaidViewModel.getAllVrps();
        vorList = navaidViewModel.getAllVors();
        waypointList = navaidViewModel.getAllWaypoints();

        setupUi();
    }

    private void setupUi() {
        infoBoxTitleTextView =  findViewById(R.id.infoBoxTitleTextView);
        navaidNameTextView =  findViewById(R.id.navaidNameTextView);
        navaidDetailTextView = findViewById(R.id.navaidDetailTextView);
        navaidTypeTextView = findViewById(R.id.navaidTypeTextView);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap mMap) {
        this.mMap = mMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMinZoomPreference(5);
        mMap.setMaxZoomPreference(12);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                markerClicked(marker);
                return false;
            }
        });

        boolean success = mMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.map_style)));
        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
        addNavaidsToMap(waypointList, 0);
        addNavaidsToMap(airfieldList, 1);
        addNavaidsToMap(vorList, 2);
    }

    private void markerClicked(Marker marker) {
        Log.i(TAG, "onMarkerClick: " + marker.getTag());
        if(marker.getTag()!=null) {
            int markerId = (int) marker.getTag();
            Navaid navaid = navaidViewModel.getNavaidById(markerId);

            infoBoxTitleTextView.setText(navaid.getCode());
            navaidNameTextView.setText(navaid.getName());
            navaidTypeTextView.setText(navaid.getType());
        }
//        navaidDetailTextView.setText(navaid.);

    }

    private void addNavaidsToMap(List<Navaid> navaids, int typeCode) {
        if (navaids.size() == 0) {
            return;
        }

        String marker_title, code, type;
        BitmapDescriptor square = BitmapFromVector(getApplicationContext(), R.drawable.ic_square_12);
        BitmapDescriptor star = BitmapFromVector(getApplicationContext(), R.drawable.ic_star_15);
        BitmapDescriptor target = BitmapFromVector(getApplicationContext(), R.drawable.target_12);
        LatLng latLng;

//        mMap.clear();
        for (Navaid navaid : navaids) {
            latLng = new LatLng(navaid.getLat(), navaid.getLng());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .visible(true);

            switch(typeCode)  {
                case 0 :
                    // VRP
                    markerOptions.icon(star);
                    break;
                case 1 :
                    // Airfield
                    markerOptions.icon(square);
                    break;
                case 2 :
                    // VOR
                    markerOptions.icon(target);
                    break;
                default:
                    break;
            }
            markerOptions.draggable(false);

            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(navaid.getNavaid_id());
            mMap.addMarker(markerOptions);
        }
        Log.i(TAG, "addNavaidsToMap: done");
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}