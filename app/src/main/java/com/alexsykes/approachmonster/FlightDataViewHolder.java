package com.alexsykes.approachmonster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.approachmonster.data.Flight;

public class FlightDataViewHolder extends RecyclerView.ViewHolder {
    final TextView flightDataTextView;

    public FlightDataViewHolder(@NonNull View itemView) {
        super(itemView);
        flightDataTextView = itemView.findViewById(R.id.flightDataTextView);
    }

    public void bind(Flight flight) {
        flightDataTextView.setText(flight.getFlight_id());
    }

    static FlightDataViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flight_slip_view, parent, false);
        return new FlightDataViewHolder(view);
    }
}
