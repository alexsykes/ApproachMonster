package com.alexsykes.approachmonster;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.alexsykes.approachmonster.data.Flight;

import java.util.List;

public class FlightDataListAdapter extends RecyclerView.Adapter<FlightDataViewHolder> {
    List<Flight> flightList;

    AdapterView.OnItemClickListener listener;


    private final String TAG = "Info";

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public FlightDataListAdapter(List<Flight> flightList) {
        this.flightList = flightList;
    }

    @NonNull
    @Override
    public FlightDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flight_slip_view, parent, false);
        FlightDataViewHolder holder = new FlightDataViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FlightDataViewHolder holder, int position) {
        Flight current = flightList.get(position);
        holder.bind(current, listener);
    }

    @Override
    public int getItemCount() {
//        Log.i(TAG, "getItemCount: " + flightList.size());
        return flightList.size();
    }
}
