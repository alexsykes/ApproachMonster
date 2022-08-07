package com.alexsykes.approachmonster;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alexsykes.approachmonster.data.ApproachDatabase;
import com.alexsykes.approachmonster.data.Flight;
import com.alexsykes.approachmonster.data.FlightDao;
import com.alexsykes.approachmonster.data.FlightViewModel;

public class ControlFragment extends Fragment {
    public static final String TAG = "Info";

    private TextView vectorView;
    private TextView altView;
    private TextView velocityView;
    private TextView rose_text_view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FlightViewModel flightViewModel;
    ApproachDatabase db;
    FlightDao flightDao;
    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private Flight flight;
    private int targetVector, targetVelocity, targetAltitude;
    private String flight_id;

    public ControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment ControlFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static ControlFragment newInstance(String param1, String param2) {
//        ControlFragment fragment = new ControlFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            flight = getArguments().getParcelable("flight");
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        flightViewModel = new ViewModelProvider(this).get(FlightViewModel.class);
        db = ApproachDatabase.getDatabase(getContext());
        flightDao = db.flightDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView =  inflater.inflate(R.layout.fragment_control, container, false);
        // Inflate the layout for this fragment
        TextView decVectorView = rootView.findViewById(R.id.decVectorView);
        TextView incVectorView = rootView.findViewById(R.id.incVectorView);
        TextView decAltView = rootView.findViewById(R.id.decAltView);
        TextView incAltView = rootView.findViewById(R.id.incAltView);
        TextView decVelocityView = rootView.findViewById(R.id.decVelocityView);
        TextView incVelocityView = rootView.findViewById(R.id.incVelocityView);

        vectorView = rootView.findViewById(R.id.vectorView);
        velocityView = rootView.findViewById(R.id.velocityView);
        altView = rootView.findViewById(R.id.altView);

        rose_text_view = rootView.findViewById(R.id.rose_text_view);
//        rose_text_view.setRotation(0);
//        vectorView.setText("360°");


        decVectorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementVector();
            }
        });

        incVectorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementVector();
            }
        });


        decVelocityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementVelocity();
            }
        });

        incVelocityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementVelocity();
            }
        });


        decAltView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementAlt();
            }
        });

        incAltView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementAlt();
            }
        });

        return rootView;
    }

    private void incrementAlt() {
        if( targetAltitude < 460 ) {
            targetAltitude = targetAltitude + 10;
            altView.setText("FL: " + String.valueOf(targetAltitude));
        }
        updateFlight();
    }

    private void decrementAlt() {
        if( targetAltitude > 10 ) {
            targetAltitude = targetAltitude - 10;
            altView.setText("FL: " + String.valueOf(targetAltitude));
        }
        updateFlight();
    }

    private void incrementVelocity() {
        if( targetVelocity < 460 ) {
            targetVelocity = targetVelocity + 5;
            velocityView.setText(String.valueOf(targetVelocity) + " kts");
        }
        updateFlight();
    }

    private void decrementVelocity() {
        if( targetVelocity >= 5 ) {
            targetVelocity = targetVelocity - 5;
            velocityView.setText(String.valueOf(targetVelocity) + " kts");
        }
        updateFlight();
    }

    private void incrementVector() {
        targetVector = (int) rose_text_view.getRotation();
        targetVector++;
        if (targetVector > 360) {
            targetVector = targetVector - 360;
        }
        rose_text_view.setRotation(targetVector);
        vectorView.setText(String.valueOf(targetVector) + "°");
        flight.setTargetVector(targetVector);
        updateFlight();
    }

    private void decrementVector() {
       targetVector = (int) rose_text_view.getRotation();
        targetVector--;
        if (targetVector < 1) {
            targetVector = 360 + targetVector;
        }
        rose_text_view.setRotation(targetVector);
        vectorView.setText(String.valueOf(targetVector) + "°");
        updateFlight();
    }

    // set flight details
    public void setFlight(Flight flight) {
        this.flight = flight;
        targetAltitude = flight.getTargetAltitude();
        targetVector = flight.getTargetVector();
        targetVelocity = flight.getTargetVelocity();
        flight_id = flight.getFlight_id();

        altView.setText("FL: " + String.valueOf(targetAltitude));
        velocityView.setText(String.valueOf(targetVelocity) + " kts");
        vectorView.setText(String.valueOf(targetVector) + "°");
        rose_text_view.setRotation(targetVector);
    }

    // void updateFlight(String flight_id, int targetAltitude, int targetVector, int targetVelocity);
     private void updateFlight() {
        ApproachDatabase db = ApproachDatabase.getDatabase(getContext());
        FlightDao flightDao = db.flightDao();

        Log.i(TAG, "updateFlight: " + flight_id);
        flightDao.updateFlight(flight_id, targetAltitude, targetVector, targetVelocity);
    }

    public String getFlightId() {
        return flight_id;
    }
}