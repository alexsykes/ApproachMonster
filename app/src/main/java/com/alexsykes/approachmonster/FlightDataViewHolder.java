package com.alexsykes.approachmonster;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.approachmonster.data.Flight;

public class FlightDataViewHolder extends RecyclerView.ViewHolder {
    final TextView flightDataTextView;
    public static final String TAG = "Info";

    public FlightDataViewHolder(@NonNull View itemView) {
        super(itemView);
        flightDataTextView = itemView.findViewById(R.id.flightDataTextView);
    }

    public void bind(Flight flight, AdapterView.OnItemClickListener listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: " + flight.getFlight_id());

                Context context = v.getContext();
                ((MainActivity) context).onClickCalled(flight);
            }
        });

        if(flight.isIncoming()) {
            flightDataTextView.setText(flight.getFlight_id() + " ↘");
        } else {
            flightDataTextView.setText(flight.getFlight_id() + " ↗");
        }
    }

    static FlightDataViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flight_slip_view, parent, false);
        return new FlightDataViewHolder(view);
    }
}
