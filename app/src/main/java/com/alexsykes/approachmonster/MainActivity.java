package com.alexsykes.approachmonster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
import com.google.maps.android.collections.MarkerManager;

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
    private boolean airfieldsVisible, waypointsVisible, vorsVisible;
    List<Navaid> airfieldList, vrpList, vorList, waypointList;
    List<Flight> flightList;
    ArrayList<Marker> currentMarkers;
    ArrayList<Polyline> currentPolylines;
    MarkerManager markerManager;
    MarkerManager.Collection airfieldMarkerCollection, vorMarkerCollection, waypointMarkerCollection, currentFlightCollection;

    int currentAlt, currentVector, currentVelocity;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    LinearLayout infoBoxLinearLayout, flightInfoBoxLayout;
    TextView infoBoxTitleTextView, navaidNameTextView, navaidDetailTextView, navaidTypeTextView;
    TextView  identTextView,  incAltTextView,  altTextView,  decAltTextView ;
    TextView  vectorTextView,  incVectorTextView,  decVectorTextView ;
    TextView  speedTextView, incSpeedTextView,  decSpeedTextView ;
    SwitchMaterial airfieldSwitch, vorSwitch, waypointSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get saved values
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        airfieldsVisible = prefs.getBoolean("airfieldsVisible", true);
        waypointsVisible = prefs.getBoolean("waypointsVisible", false);
        vorsVisible = prefs.getBoolean("vorsVisible", false);

        // Setup map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Setup data sources
        ApproachDatabase db = ApproachDatabase.getDatabase(this);
        navaidViewModel = new ViewModelProvider(this).get(NavaidViewModel.class);
        flightViewModel = new ViewModelProvider(this).get(FlightViewModel.class);
        flightDao = db.flightDao();

        // Get saved data
        airfieldList = navaidViewModel.getAllAirfields();
//        vrpList = navaidViewModel.getAllVrps();
        vorList = navaidViewModel.getAllVors();
        waypointList = navaidViewModel.getAllWaypoints();
        setupUi();
    }

    private void setupUi() {
        infoBoxLinearLayout = findViewById(R.id.infoBoxLinearLayout);
        infoBoxTitleTextView =  findViewById(R.id.infoBoxTitleTextView);
        navaidNameTextView =  findViewById(R.id.navaidNameTextView);
        navaidDetailTextView = findViewById(R.id.navaidDetailTextView);
        navaidTypeTextView = findViewById(R.id.navaidTypeTextView);
        infoBoxLinearLayout.setVisibility(View.GONE);

        flightInfoBoxLayout = findViewById(R.id.flightInfoBox);
        flightInfoBoxLayout.setVisibility(View.GONE);

        identTextView  = findViewById(R.id.identTextView);
        incAltTextView  = findViewById(R.id.incAltTextView);
        altTextView   = findViewById(R.id.altTextView);
        decAltTextView  = findViewById(R.id.decAltTextView);


        vectorTextView  = findViewById(R.id.vectorTextView);
        incVectorTextView  = findViewById(R.id.incVectorTextView);
        decVectorTextView  = findViewById(R.id.decVectorTextView);
        speedTextView    = findViewById(R.id.speedTextView);
        incSpeedTextView = findViewById(R.id.incSpeedTextView);
        decSpeedTextView   = findViewById(R.id.decSpeedTextView);

        airfieldSwitch = findViewById(R.id.airportSwitch);
        vorSwitch = findViewById(R.id.vorSwitch);
        waypointSwitch = findViewById(R.id.waypointSwitch);
        airfieldSwitch.setChecked(airfieldsVisible);
        waypointSwitch.setChecked(waypointsVisible);
        vorSwitch.setChecked(vorsVisible);

        vorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked) {
                    vorMarkerCollection.showAll();
                    editor.putBoolean("vorsVisible", true);
                } else {
                    vorMarkerCollection.hideAll();
                    editor.putBoolean("vorsVisible", false);
                }
                editor.apply();
            }
        });
        waypointSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked) {
                    waypointMarkerCollection.showAll();
                    editor.putBoolean("waypointsVisible", true);
                } else {
                    waypointMarkerCollection.hideAll();
                    editor.putBoolean("waypointsVisible", false);
                }
                editor.apply();
            }
        });
        airfieldSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked) {
                    airfieldMarkerCollection.showAll();
                    editor.putBoolean("airfieldsVisible", true);
                } else {
                    airfieldMarkerCollection.hideAll();
                    editor.putBoolean("airfieldsVisible", false);
                }
                editor.apply();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap mMap) {
//runnable must be execute once
        Log.i(TAG, "onMapReady: ");
        this.mMap = mMap;
        markerManager = new MarkerManager(mMap);
        airfieldMarkerCollection = markerManager.newCollection();
        waypointMarkerCollection = markerManager.newCollection();
        vorMarkerCollection = markerManager.newCollection();
        currentFlightCollection = markerManager.newCollection();

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMinZoomPreference(5);
        mMap.setMaxZoomPreference(18);
        mMap.setOnMapLoadedCallback(this);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(@NonNull Marker marker) {
//                markerClicked(marker);
//                return false;
//            }
//        });

        boolean success = mMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.map_style)));
        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
    }

    @Override
    public void onMapLoaded() {
        Log.i(TAG, "onMapLoaded: ");
//        addNavaidsToMap(airfieldList, 1);
        addNavaidsToMap(vorList, 2);
        addAirfieldsToMap(airfieldList);
        addWaypointsToMap(waypointList);
//        addFlightsToMap();

        currentPolylines = new ArrayList<Polyline>();
        vorMarkerCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Log.i(TAG, "vorMarkers.onMarkerClick: ");
                markerClicked(marker);
                marker.showInfoWindow();
                return false;
            }
        });
        airfieldMarkerCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Log.i(TAG, "airfieldMarkers.onMarkerClick: ");
                markerClicked(marker);
                return false;
            }
        });
        waypointMarkerCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Log.i(TAG, "waypointMarkers.onMarkerClick: ");
                markerClicked(marker);
                return false;
            }
        });
        currentFlightCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Log.i(TAG, "currentFlightCollection.onMarkerClick: ");
                flightMarkerClicked(marker);
                return false;
            }
        });
        // Start updates following initial delay
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.i(TAG, "waited:  UPDATE_PERIOD");
//                handler.post(flightProgressTimer);
//            }
//        }, UPDATE_PERIOD);
        handler.post(flightProgressTimer);
    }

    private void flightMarkerClicked(Marker marker) {
        flightInfoBoxLayout.setVisibility(View.VISIBLE);
        String flight_id = marker.getTitle();
        identTextView.setText(flight_id);
        Flight flight = flightDao.getFlight(flight_id);

        currentAlt = flight.getAltitude();
        currentVector = flight.getVector();
        currentVelocity = flight.getVelocity();

        vectorTextView.setText(String.valueOf(flight.getVector()+ "°"));
        speedTextView.setText(String.valueOf(flight.getVelocity() + "Kts"));
        altTextView.setText("FL: "+ currentAlt);

        incAltTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentAlt++;
                altTextView.setText("FL: "+String.valueOf(currentAlt));
                updateFlight();
            }
        });

        decAltTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentAlt > 0) {
                    currentAlt--;
                }
                altTextView.setText("FL: "+String.valueOf(currentAlt));
                updateFlight();
            }
        });

        incVectorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentVector < 360) {
                    currentVector++;
                } else {
                    currentVector = 1;
                }
                vectorTextView.setText(String.valueOf(currentVector) + "°");
                updateFlight();
            }
        });

        decVectorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentVector > 1) {
                    currentVector--;
                } else {
                    currentVector = 360;
                }
                vectorTextView.setText(String.valueOf(currentVector) + "°");
                updateFlight();
            }
        });

        incSpeedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentVelocity < 360) {
                    currentVelocity = currentVelocity + 5;
                }
                speedTextView.setText(currentVelocity + "Kts");
                updateFlight();
            }
        });

        decSpeedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentVelocity > 5) {
                    currentVelocity = currentVelocity - 5;
                }
                speedTextView.setText(currentVelocity + "Kts");
                updateFlight();
            }
        });


    }

    private void updateFlight() {
    }

    private void markerClicked(Marker marker) {
        Log.i(TAG, "markerClicked.onMarkerClick: " + marker.getId());
        if(marker.getTag()!=null) {
            int markerId = (int) marker.getTag();
            Navaid navaid = navaidViewModel.getNavaidById(markerId);
            infoBoxTitleTextView.setText(navaid.getCode());
            navaidNameTextView.setText(navaid.getName());
            navaidTypeTextView.setText(navaid.getType());
        }
    }

    private void addFlightsToMap() {
        currentMarkers = new ArrayList<Marker>();
//        currentPolylines = new ArrayList<Polyline>();
//        currentFlightCollection.clear();

        // Get current flight list
        flightList = flightViewModel.getActiveFlightList();

        // Set up current position and icon
        LatLng currentPosition, lineEnd;
        BitmapDescriptor square = BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_square_24);

        // Using existing flightList
        for(Flight flight: flightList) {
//            Calculate current position and vector
            currentPosition = new LatLng(flight.getLat(), flight.getLng());

            // Vector shows distance per minute on current track
            double distance = flight.getVelocity() * 60 * 0.51444;
            lineEnd = SphericalUtil.computeOffset(currentPosition, distance, flight.getVector());
            MarkerOptions markerOptionsSquare = new MarkerOptions()
                    .position(currentPosition)
                    .title(flight.getFlight_id())
                    .visible(true);

            markerOptionsSquare.icon(square);
            markerOptionsSquare.anchor(0.5f, 0.5f);
//            Marker currentMarker = currentFlightCollection.addMarker(markerOptionsSquare);
//            currentMarker.setTag(flight.getFlight_id());

            Polyline polyline = mMap.addPolyline((new PolylineOptions()).add(currentPosition, lineEnd).
                    width(3)
                    .color(Color.WHITE)
                    .geodesic(true));
            currentFlightCollection.addMarker(markerOptionsSquare);
//            currentPolylines.add(polyline);
//            currentMarkers.add(currentMarker);
//            Log.i(TAG, "addFlightsToMap: " + latLng);
        }
        Log.i(TAG, "flights added: ");
    }

    private void addNavaidsToMap(List<Navaid> navaids, int typeCode) {
        if (navaids.size() == 0) {
            return;
        }

        String marker_title, code, type;
        BitmapDescriptor target = BitmapFromVector(getApplicationContext(), R.drawable.target_12);
        LatLng latLng;

//        mMap.clear();
        for (Navaid navaid : navaids) {
            latLng = new LatLng(navaid.getLat(), navaid.getLng());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(target)
                    .visible(true)
                    .title(navaid.getCode())
                    .draggable(false);
//            Marker marker = mMap.addMarker(markerOptions);
//            marker.setTag(navaid.getNavaid_id());
            Marker marker = vorMarkerCollection.addMarker(markerOptions);
            marker.setTag(navaid.getNavaid_id());
        }
        Log.i(TAG, "addNavaidsToMap: done");

        if(vorsVisible) {
            vorMarkerCollection.showAll();
        } else {
            vorMarkerCollection.hideAll();
        }
    }

    private void addAirfieldsToMap(List<Navaid> navaids) {
        if (navaids.size() == 0) {
            return;
        }

        String marker_title, code, type;
        BitmapDescriptor square = BitmapFromVector(getApplicationContext(), R.drawable.ic_square_12);
        LatLng latLng;

//        mMap.clear();
        for (Navaid navaid : navaids) {
            latLng = new LatLng(navaid.getLat(), navaid.getLng());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(square)
                    .visible(true)
                    .title(navaid.getCode())
                    .draggable(false);

//            Marker marker = mMap.addMarker(markerOptions);
//            marker.setTag(navaid.getNavaid_id());
            Marker marker = airfieldMarkerCollection.addMarker(markerOptions);
            marker.setTag(navaid.getNavaid_id());
        }

        if(airfieldsVisible) {
            airfieldMarkerCollection.showAll();
        } else {
            airfieldMarkerCollection.hideAll();
        }
        Log.i(TAG, "addAirfieldsToMap: done");
    }

    private void addWaypointsToMap(List<Navaid> navaids) {
        if (navaids.size() == 0) {
            return;
        }

        String marker_title, code, type;
        BitmapDescriptor square = BitmapFromVector(getApplicationContext(), R.drawable.ic_star_15);
        LatLng latLng;

//        mMap.clear();
        for (Navaid navaid : navaids) {
            latLng = new LatLng(navaid.getLat(), navaid.getLng());

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(square)
                    .visible(true)
                    .title(navaid.getCode())
                    .draggable(false);

//            Marker marker = mMap.addMarker(markerOptions);
//            marker.setTag(navaid.getNavaid_id());
            Marker marker = waypointMarkerCollection.addMarker(markerOptions);
            marker.setTag(navaid.getNavaid_id());
        }
        Log.i(TAG, "addWaypointsToMap: done");

        if(waypointsVisible) {
            waypointMarkerCollection.showAll();
        } else {
            waypointMarkerCollection.hideAll();
        }
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    Runnable flightProgressTimer = new Runnable() {

        @Override
        public void run() {
            try{
//              Get current flight list
                flightList = flightViewModel.getActiveFlightList();
//              Clear display
                currentFlightCollection.clear();
//              Delete current markers and lines from the map
                for (Polyline polyline : currentPolylines) {
                    polyline.remove();
                }
//                }

//              Define icon
                BitmapDescriptor square = BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_square_24);
                for (Flight flight: flightList) {
//                  Get current position, then update
                    LatLng currentPosition = new LatLng(flight.getLat(), flight.getLng());

                    //  Update database
                    flight.move(UPDATE_PERIOD);
                    flightDao.updatePosition(currentPosition.latitude, currentPosition.longitude, flight.getFlight_id());
                    // Vector shows distance per minute on current track
                    // Calculate projected minute dustance then add
                    double distance = flight.getVelocity() * 60 * 0.51444;
                    LatLng lineEnd = SphericalUtil.computeOffset(currentPosition, distance, flight.getVector());

                    MarkerOptions markerOptionsSquare = new MarkerOptions()
                            .position(currentPosition)
                            .title(flight.getFlight_id())
                            .visible(true);

                    markerOptionsSquare.icon(square);
                    markerOptionsSquare.anchor(0.5f, 0.5f);
                    Marker currentMarker = currentFlightCollection.addMarker(markerOptionsSquare);

                    Polyline polyline = mMap.addPolyline((new PolylineOptions()).add(currentPosition, lineEnd)
                            .width(3)
                            .color(Color.WHITE)
                            .geodesic(true));

                    // Add to ListArrays
                    currentPolylines.add(polyline);
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