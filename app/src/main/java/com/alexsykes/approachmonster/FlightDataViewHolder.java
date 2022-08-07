package com.alexsykes.approachmonster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.approachmonster.data.Flight;

public class FlightDataViewHolder extends RecyclerView.ViewHolder {
    final TextView flightDataTextView, flightIDTextView ;
    public static final String TAG = "Info";

    public FlightDataViewHolder(@NonNull View itemView) {
        super(itemView);
        flightIDTextView = itemView.findViewById(R.id.flightIDTextView);
        flightDataTextView = itemView.findViewById(R.id.flightDataTextView);
    }

    public void bind(Flight flight, AdapterView.OnItemClickListener listener) {
        int outgoing  = ContextCompat.getColor(itemView.getContext(), R.color.outgoing);
        int incoming  = ContextCompat.getColor(itemView.getContext(), R.color.incoming);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        flightDataTextView.setText(flight.getDestination());
        flightDataTextView.setVisibility(View.VISIBLE);
                Context context = v.getContext();
                ((MainActivity) context).onClickCalled(flight);
            }
        });

        if(flight.isIncoming()) {
            flightIDTextView.setText(flight.getFlight_id() + " ↘");
            itemView.setBackgroundColor(incoming);
        } else {
            flightIDTextView.setText(flight.getFlight_id() + " ↗");
            itemView.setBackgroundColor(outgoing);
        }
    }

    static FlightDataViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flight_slip_view, parent, false);
        return new FlightDataViewHolder(view);
    }
}
