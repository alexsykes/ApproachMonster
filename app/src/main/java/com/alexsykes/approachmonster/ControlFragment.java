package com.alexsykes.approachmonster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alexsykes.approachmonster.data.FlightViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ControlFragment extends Fragment {

    private TextView decSpeedView, incSpeedView, decVectorView, incVectorView, vectorView, rose_text_view;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FlightViewModel flightViewModel;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ControlFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ControlFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ControlFragment newInstance(String param1, String param2) {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        flightViewModel = new ViewModelProvider(this).get(FlightViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView =  inflater.inflate(R.layout.fragment_control, container, false);
        // Inflate the layout for this fragment
        decVectorView = rootView.findViewById(R.id.decVectorView);
        incVectorView = rootView.findViewById(R.id.incVectorView);
        vectorView = rootView.findViewById(R.id.vectorView);
        rose_text_view = rootView.findViewById(R.id.rose_text_view);
        rose_text_view.setRotation(0);
        vectorView.setText("360°");


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

        return rootView;
    }

    private void incrementVector() {
        int vector = (int) rose_text_view.getRotation();
        vector++;
        if (vector > 360) {
            vector = vector - 360;
        }
        rose_text_view.setRotation(vector);
        vectorView.setText(String.valueOf(vector) + "°");
    }

    private void decrementVector() {
        int vector = (int) rose_text_view.getRotation();
        vector--;
        if (vector < 1) {
            vector = 360 + vector;
        }
        rose_text_view.setRotation(vector);
        vectorView.setText(String.valueOf(vector) + "°");
    }
}