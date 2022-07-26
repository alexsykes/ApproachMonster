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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private final LatLng DEFAULT_LOCATION = new LatLng(53.355437, -2.277298);
    private final int DEFAULT_ZOOM = 9;
    private final String TAG = "Info";
    private NavaidDao navaidDao;
    private NavaidViewModel navaidViewModel;
    private NavaidRepository navaidRepository;
    List<Navaid> airfieldList, vrpList, vorList;

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
//        navaidRepository =
        navaidDao = db.navaidDao();

        airfieldList = navaidViewModel.getAllAirfields();
        vrpList = navaidViewModel.getAllVrps();
        vorList = navaidViewModel.getAllVors();
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

        boolean success = mMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.map_style)));
        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }
//        mMap.addMarker(new MarkerOptions()
//                .position(DEFAULT_LOCATION)
//                .title("EGCC"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
        addNavaidsToMap(airfieldList, 0);
        addNavaidsToMap(vorList, 1);
        addNavaidsToMap(vrpList, 2);
    }

    private void addNavaidsToMap(List<Navaid> navaids, int typeCode) {
        if (navaids.size() == 0) {
            return;
        }

        String marker_title, code, type;
        LatLng latLng;

//        mMap.clear();
        for (Navaid navaid : navaids) {
            latLng = new LatLng(navaid.getLat(), navaid.getLng());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(navaid.getCode())
                    .visible(true);

            switch(typeCode)  {
                case 0 :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.circle_6));
                    break;
                case 1 :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.target_12));
                    break;
                case 2 :
                    markerOptions.icon(BitmapFromVector(getApplicationContext(), R.drawable.triangle_48));
                    break;
                default:
                    break;
            }
            markerOptions.draggable(false);
            mMap.addMarker(markerOptions);
//            Marker marker1 = mMap.addMarker(markerOptions);
//            marker1.setTag(marker.getMarker_id());
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