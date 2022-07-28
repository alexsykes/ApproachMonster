package com.alexsykes.approachmonster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.alexsykes.approachmonster.data.ApproachDatabase;
import com.alexsykes.approachmonster.data.Flight;
import com.alexsykes.approachmonster.data.FlightDao;
import com.alexsykes.approachmonster.data.FlightViewModel;
import com.alexsykes.approachmonster.data.Navaid;
import com.alexsykes.approachmonster.data.NavaidDao;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;


// TODO implement layer visibility switches
// NOTE - 1 knot = 0.51444 metres / sec
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap mMap;
    private final LatLng DEFAULT_LOCATION = new LatLng(53.355437, -2.277298);
    private final int DEFAULT_ZOOM = 9;
    private final int UPDATE_PERIOD = 1000; // millis
    final Handler handler = new Handler();
    private final String TAG = "Info";
    private NavaidDao navaidDao;
    private FlightDao flightDao;
    private NavaidViewModel navaidViewModel;
    private FlightViewModel flightViewModel;
    List<Navaid> airfieldList, vrpList, vorList, waypointList;
    List<Flight> flightList;
    ArrayList<Marker> currentMarkers;
    ArrayList<Polyline> currentPolylines;

    TextView infoBoxTitleTextView, navaidNameTextView, navaidDetailTextView, navaidTypeTextView;
    SwitchMaterial airportSwitch, vorSwitch, waypointSwitch;

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
        flightViewModel = new ViewModelProvider(this).get(FlightViewModel.class);
        flightDao = db.flightDao();

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

        airportSwitch = findViewById(R.id.airportSwitch);
        vorSwitch = findViewById(R.id.vorSwitch);
        waypointSwitch = findViewById(R.id.waypointSwitch);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap mMap) {
//runnable must be execute once
        Log.i(TAG, "onMapReady: ");
        this.mMap = mMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMinZoomPreference(5);
        mMap.setMaxZoomPreference(18);
        mMap.setOnMapLoadedCallback(this);
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
//        addNavaidsToMap(waypointList, 0);
    }

    @Override
    public void onMapLoaded() {
        Log.i(TAG, "onMapLoaded: ");
        addNavaidsToMap(airfieldList, 1);
        addNavaidsToMap(vorList, 2);
        addFlightsToMap();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "waited:  UPDATE_PERIOD");
                handler.post(runnable);
            }
        }, UPDATE_PERIOD);
    }

    private void addFlightsToMap() {
        currentMarkers = new ArrayList<Marker>();
        currentPolylines = new ArrayList<Polyline>();
        flightList = flightViewModel.getActiveFlightList();
        LatLng currentPosition, lineEnd;
        BitmapDescriptor square = BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_square_24);

        // Using existing flightList
        for(Flight flight: flightList) {
//            Calculate current position and vector
            currentPosition = new LatLng(flight.getLat(), flight.getLng());

            // Vector shows distance per minute on current track
            double distance = flight.getVelocity() * 60 * 0.51444;
            lineEnd = SphericalUtil.computeOffset(currentPosition, distance, flight.getVector());
            MarkerOptions markerOptionsLine = new MarkerOptions()
                    .position(currentPosition)
                    .visible(true);
            MarkerOptions markerOptionsSquare = new MarkerOptions()
                    .position(currentPosition)
                    .visible(true);

            markerOptionsSquare.icon(square);
            markerOptionsSquare.anchor(0.5f, 0.5f);
            Marker currentMarker = mMap.addMarker(markerOptionsSquare);

           Polyline polyline = mMap.addPolyline((new PolylineOptions()).add(currentPosition, lineEnd).
                            width(3)
                    .color(Color.WHITE)
                    .geodesic(true));

           currentPolylines.add(polyline);
            currentMarkers.add(currentMarker);
//            Log.i(TAG, "addFlightsToMap: " + latLng);
        }
        Log.i(TAG, "flights added: ");
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

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            try{
                //do your code here

                flightList = flightViewModel.getActiveFlightList();
                for(Marker marker: currentMarkers) {
                    marker.remove();
                }
                for(Polyline polyline : currentPolylines) {
                    polyline.remove();
                }

                BitmapDescriptor square = BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_square_24);
                for (Flight flight: flightList) {
                    LatLng currentPosition = new LatLng(flight.getLat(), flight.getLng());
                     flight.move(UPDATE_PERIOD);
                     currentPosition = new LatLng(flight.getLat(), flight.getLng());

                    // Vector shows distance per minute on current track
                    double distance = flight.getVelocity() * 60 * 0.51444;
                    LatLng lineEnd = SphericalUtil.computeOffset(currentPosition, distance, flight.getVector());
                    MarkerOptions markerOptionsLine = new MarkerOptions()
                            .position(currentPosition)
                            .visible(true);
                    MarkerOptions markerOptionsSquare = new MarkerOptions()
                            .position(currentPosition)
                            .visible(true);

                    markerOptionsSquare.icon(square);
                    markerOptionsSquare.anchor(0.5f, 0.5f);
                    Marker currentMarker = mMap.addMarker(markerOptionsSquare);

                    Polyline polyline = mMap.addPolyline((new PolylineOptions()).add(currentPosition, lineEnd).
                            width(3)
                            .color(Color.WHITE)
                            .geodesic(true));

                    currentPolylines.add(polyline);
                    currentMarkers.add(currentMarker);
                }
            }
            catch (Exception e) {
                // TODO: handle exception
            }
            finally{
                //also call the same runnable to call it at regular interval
                handler.postDelayed(this, UPDATE_PERIOD);
            }
        }
    };
}