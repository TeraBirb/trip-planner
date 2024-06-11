package com.example.trip_planner.Activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip_planner.Entities.Vacation;
import com.example.trip_planner.R;

import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {

    private List<Vacation> vacations;
    private final Context context;
    private final LayoutInflater inflater;

    class VacationViewHolder extends RecyclerView.ViewHolder {
        private final TextView vacationItemView;

        private VacationViewHolder(View itemView) {
            super(itemView);
            vacationItemView = itemView.findViewById(R.id.textViewVacationListItem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Vacation current = vacations.get(position);

                    Intent intent = new Intent(context, VacationDetails.class);
                    intent.putExtra("id", current.getVacationID());
                    intent.putExtra("name", current.getVacationTitle());
                    intent.putExtra("staying at", current.getAccommodationName());
                    intent.putExtra("start date", current.getStartDate().toString());
                    intent.putExtra("end date", current.getEndDate().toString());

                    context.startActivity(intent);
                }
            });
        }
    }

    public VacationAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.vacation_list_item, parent, false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        if (vacations != null) {
            Vacation current = vacations.get(position);
            String name = current.getVacationTitle();
            holder.vacationItemView.setText(name);
        }
        else {
            holder.vacationItemView.setText("No vacation title");
        }
    }

    public void setVacations(List<Vacation> v) {
        vacations = v;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        if (vacations == null) {
            return 0;
        } else {
            return vacations.size();
        }
    }
}
