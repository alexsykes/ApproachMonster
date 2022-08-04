package com.alexsykes.approachmonster;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.approachmonster.data.ApproachDatabase;
import com.alexsykes.approachmonster.data.Flight;
import com.alexsykes.approachmonster.data.FlightDao;
import com.alexsykes.approachmonster.data.FlightViewModel;
import com.alexsykes.approachmonster.data.Navaid;
import com.alexsykes.approachmonster.data.NavaidViewModel;
import com.alexsykes.approachmonster.data.Runway;
import com.alexsykes.approachmonster.data.RunwayViewModel;
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
import java.util.Random;


// TODO implement layer visibility switches
// NOTE - 1 knot = 0.51444 metres / sec
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap mMap;
    private final LatLng DEFAULT_LOCATION = new LatLng(53.355437, -2.277298);
    private final int DEFAULT_ZOOM = 9;
    private final int UPDATE_PERIOD = 1000; // milli
    private long randomDelay;
    final Handler handler = new Handler();
    private final String TAG = "Info";
    private FlightDao flightDao;
    private NavaidViewModel navaidViewModel;
    private FlightViewModel flightViewModel;
    private RunwayViewModel runwayViewModel;
    private boolean airfieldsVisible, waypointsVisible, vorsVisible, clockwise;
    List<Navaid> airfieldList, vorList, waypointList;
    List<Flight> flightList;
    List<Runway> runwayList;
    ArrayList<Polyline> currentPolylines;
    MarkerManager markerManager;
    MarkerManager.Collection airfieldMarkerCollection, vorMarkerCollection, waypointMarkerCollection, currentFlightCollection;

    int currentAlt, currentVector, currentVelocity;
    int targetAlt, targetVector, targetVelocity;
    String flight_id;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    LinearLayout infoBoxLinearLayout, flightInfoBoxLayout;
    TextView infoBoxTitleTextView, navaidNameTextView, navaidDetailTextView, navaidTypeTextView;
    TextView  identTextView,  incAltTextView,  decAltTextView ;
    TextView    incVectorTextView,  decVectorTextView ;
    TextView   incSpeedTextView,  decSpeedTextView ;
    TextView speedEdit, vectorEdit, altEdit, altLabel, vectorLabel, speedLabel;
    SwitchMaterial airfieldSwitch, vorSwitch, waypointSwitch;
    RecyclerView flightListRecyclerView;

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
        runwayViewModel = new ViewModelProvider(this).get(RunwayViewModel.class);
        flightDao = db.flightDao();

//        nukeFlights(false);
        // Get saved data
        airfieldList = navaidViewModel.getAllAirfields();
        vorList = navaidViewModel.getAllVors();
        waypointList = navaidViewModel.getAllWaypoints();
        runwayList = runwayViewModel.getRunwayList();
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
        identTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flightInfoBoxLayout.setVisibility(View.GONE);
            }
        });
        incAltTextView  = findViewById(R.id.incAltTextView);
        altEdit   = findViewById(R.id.altEditText);
        decAltTextView  = findViewById(R.id.decAltTextView);

        flightListRecyclerView = findViewById(R.id.flightListRV);

        altLabel = findViewById(R.id.altLabel);
        speedLabel = findViewById(R.id.speedLabel);
        vectorLabel = findViewById(R.id.vectorLabel);
        vectorEdit  = findViewById(R.id.vectorEditText);
        incVectorTextView  = findViewById(R.id.incVectorTextView);
        decVectorTextView  = findViewById(R.id.decVectorTextView);
        speedEdit    = findViewById(R.id.speedEditText);
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
        addNavaidsToMap(vorList, 2);
        addAirfieldsToMap(airfieldList);
        addWaypointsToMap(waypointList);

        currentPolylines = new ArrayList<Polyline>();
        vorMarkerCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                markerClicked(marker);
                marker.showInfoWindow();
                return false;
            }
        });
        airfieldMarkerCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                markerClicked(marker);
                return false;
            }
        });
        waypointMarkerCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                markerClicked(marker);
                return false;
            }
        });
        currentFlightCollection.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                flightMarkerClicked(marker);
                return false;
            }
        });

        handler.post(flightProgressTimer);
        handler.post(mainLoop);
    }

    private void flightMarkerClicked(Marker marker) {
        flightInfoBoxLayout.setVisibility(View.VISIBLE);
        flight_id = marker.getTitle();
        identTextView.setText(flight_id);
        Flight flight = flightDao.getFlight(flight_id);

        currentAlt = flight.getAltitude();
        currentVector = flight.getVector();
        currentVelocity = flight.getVelocity();

        speedLabel.setText("Speed: " + currentVelocity + " kts");
        vectorLabel.setText("Direction:  " + currentVector + "°");
        altLabel.setText("Flight Level: " + currentAlt );

        targetAlt = flight.getTargetAltitude();
        targetVector = flight.getTargetVector();
        targetVelocity = flight.getTargetVelocity();

        vectorEdit.setText(String.valueOf(targetVector));
        speedEdit.setText(String.valueOf(targetVelocity));
        altEdit.setText(String.valueOf(targetAlt));

        vectorEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    targetVector = Integer.valueOf(vectorEdit.getText().toString());

                    updateFlight();
                }
            }
        });

        incAltTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetAlt < 450) {
                    targetAlt = targetAlt + 10;
                } else {
                    targetAlt = 450;
                }
                altEdit.setText(String.valueOf(targetAlt));
                updateFlight();
            }
        });

        decAltTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetAlt > 10) {
                    targetAlt = targetAlt - 10;
                } else {
                    targetAlt = 0;
                }
                altEdit.setText(String.valueOf(targetAlt));
                updateFlight();
            }
        });

        incVectorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(targetVector < 360) {
                    targetVector = targetVector + 10;
                } else {
                    targetVector = 10;
                }
                vectorEdit.setText(String.valueOf(targetVector));
                updateFlight();
            }
        });

        decVectorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetVector >= 20) {
                    targetVector = targetVector - 10;
                } else {
                    targetVector = 360;
                }
                vectorEdit.setText(String.valueOf(targetVector));
                updateFlight();
            }
        });

        incSpeedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(targetVelocity < 500) {
                    targetVelocity = targetVelocity + 10;
                } else {
                    targetVelocity = 500;
                }
                speedEdit.setText(String.valueOf(targetVelocity));
                updateFlight();
            }
        });

        decSpeedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetVelocity > 10) {
                    targetVelocity = targetVelocity - 10;
                }
                speedEdit.setText(String.valueOf(targetVelocity));
                updateFlight();
            }
        });
    }

    private void updateFlight() {
        flightDao.updateFlight(flight_id, targetAlt, targetVector, targetVelocity);
    }

    Runnable flightProgressTimer = new Runnable() {

        @Override
        public void run() {
            try{
//              Get current flight list
//                flightList = flightViewModel.getActiveFlightList();
                flightList = flightDao.getActiveFlightList();
//              Clear display
                currentFlightCollection.clear();
//              Delete current markers and lines from the map
                for (Polyline polyline : currentPolylines) {
                    polyline.remove();
                }

//              Define icon
                BitmapDescriptor square = BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_square_24);
                for (Flight flight: flightList) {
//                  Get current position, then update
                    LatLng currentPosition = new LatLng(flight.getLat(), flight.getLng());
                    boolean dataChanged = false;
                    int currentAlt, currentVector, currentVelocity, targetVelocity, targetAlt, targetVector, deltaVector, adjustedCurrent, adjustedTarget;

                    // Get current and target values from database
                    currentAlt = flight.getAltitude();
                    targetAlt = flight.getTargetAltitude();
                    currentVector = flight.getVector();
                    targetVector = flight.getTargetVector();
                    currentVelocity = flight.getVelocity();
                    targetVelocity = flight.getTargetVelocity();

                    // Populate unknowns
                    adjustedCurrent = currentVector;
                    adjustedTarget = targetVector;

                    // Display flight data for one flight
                    if (flight.getFlight_id().equals(flight_id)) {
                        speedLabel.setText("Speed: " + currentVelocity + " kts");
                        vectorLabel.setText("Direction:  " + currentVector + "°");
                        altLabel.setText("Flight Level: " + currentAlt);
                    }
//                  Vector calculation starts here
                    if(currentVector != targetVector) {
                        deltaVector = targetVector - currentVector;
                        if(deltaVector < 0 ) {
                            deltaVector = deltaVector + 360;
                        }
//                        Log.i(TAG, "delta: " + deltaVector);

                        if ( deltaVector < 180) {
                            clockwise = true;
                            currentVector++;
                            if (currentVector > 360) { currentVector = currentVector - 360; }
                        } else {
                            clockwise = false;
                            currentVector--;
                            if (currentVector < 1) { currentVector = currentVector + 360; }
                        }
                        dataChanged = true;
                    }
//                  Vector calculation ends here

                    if (currentAlt < targetAlt) {
                        currentAlt++;
                        dataChanged = true;
                    } else if (currentAlt > targetAlt) {
                        currentAlt--;
                        dataChanged = true;
                    }

                    if (currentVelocity < targetVelocity) {
                        currentVelocity++;
                        dataChanged = true;
                    } else if (currentVelocity > targetVelocity) {
                        currentVelocity--;
                        dataChanged = true;
                    }

                    if(dataChanged) {
                        flightDao.updateFlight(currentVector, currentVelocity, currentAlt, flight.getFlight_id());
                    }

                    String snippet;
                    //  Update database
                    flight.move(UPDATE_PERIOD);
                    flightDao.updatePosition(flight.getLat(), flight.getLng(), flight.getFlight_id());
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
    Runnable mainLoop = new Runnable() {
        @Override
        public void run() {
            try{
                randomDelay = (long) (Math.random() * 60000);
                if(flightList.size() < 10) {
                    int randomInt = (int) (Math.random() * 2);
                    if(randomInt == 1) {
                        addOutbound();
                    } else {
                        addIncoming();
                    }
                    Log.i(TAG, "Flight added: randomInt: " + randomInt);
                } else {
                    Log.i(TAG, "No flight added - quota exceeded ");
                }
                flightListRecyclerView = findViewById(R.id.flightListRV);
                final FlightDataListAdapter flightDataListAdapter = new FlightDataListAdapter(flightList);
                flightListRecyclerView.setAdapter(flightDataListAdapter);
            }
            catch (Exception e) {
                // TODO: handle exception
                Log.i(TAG, "Error: " + e.getLocalizedMessage());
            }
            finally{
                //also call the same runnable to call it at regular interval
                handler.postDelayed(this, randomDelay);
            }
        }
    };

    private void addOutbound() {
        Runway runway = getRandomRunway();
        Flight flight = new Flight("WA1893", runway.getLat(), runway.getLng(), runway.getElevation(), runway.getDirection(), 0, "EEFG", "B777");
        flightDao.insertFlight(flight);
    }

    private void addIncoming() {
        Navaid startPoint  = navaidViewModel.getRandomNavaid();
        Runway runway = runwayList.get(0);
        LatLng origin = new LatLng(startPoint.getLat(), startPoint.getLng());
        LatLng touchDown = new LatLng(runway.getLat(), runway.getLng());

        int vector = (int) SphericalUtil.computeHeading(origin, touchDown);
        vector = (vector + 360) % 360;

        String waypointName = startPoint.getName();
        String code = generateFlightCode();

        Random rnd = new Random();
        int startAlt = (int) 10 * (10 + (rnd.nextInt(35) ));
        int startVel  = (int) (250 + (rnd.nextInt(21)*10));

        Flight flight = new Flight(code,startPoint.getLat(), startPoint.getLng(), startAlt, vector, startVel, "EGCC", "B747");

        Log.i(TAG, "addIncoming: ");

        flightDao.insertFlight(flight);
    }

    private String generateFlightCode () {
        String caps = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        String numbers = "0123456789";

        StringBuilder sb = new StringBuilder(5);

        for (int i = 0; i < 2; i++) {
            Random rnd = new Random();
            sb.append(caps.charAt(rnd.nextInt(caps.length())));
        }

        for (int i = 0; i < 4; i++) {
            Random rnd = new Random();
            sb.append(numbers.charAt(rnd.nextInt(numbers.length())));
        }

        return sb.toString();
    }

    private void nukeFlights(boolean b) {
        flightViewModel.deleteAllActiveFlights(true);
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

    private Runway getRandomRunway() {
        int numRunways = runwayList.size();
        int randomInt = (int) (Math.random() * 4);
        Runway runway = runwayList.get(randomInt);
        return runway;
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void markerClicked(Marker marker) {
        Log.i(TAG, "markerClicked.onMarkerClick: " + marker.getId());
        if(marker.getTag()!=null) {
            int markerId = (int) marker.getTag();
            Navaid navaid = navaidViewModel.getNavaidById(markerId);
            infoBoxTitleTextView.setText(navaid.getCode());
            flight_id = navaid.getCode();
            navaidNameTextView.setText(navaid.getName());
            navaidTypeTextView.setText(navaid.getType());
        }
    }
}