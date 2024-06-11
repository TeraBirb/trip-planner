package com.example.trip_planner.Activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trip_planner.R;
import com.example.trip_planner.Entities.Excursion;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    private List<Excursion> excursions;
    private final Context context;
    private final LayoutInflater inflater;

    public class ExcursionViewHolder extends RecyclerView.ViewHolder {

        private final TextView excursionItemView;

        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            excursionItemView = itemView.findViewById(R.id.textViewExcursionListItem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    final Excursion current = excursions.get(position);
                    Intent intent = new Intent(context, ExcursionDetails.class);
                    intent.putExtra("id", current.getExcursionID());
                    intent.putExtra("name", current.getExcursionTitle());
                    intent.putExtra("date", current.getExcursionDate());
                    intent.putExtra("vacID", current.getVacationID());
                    context.startActivity(intent);
                }
            });

        }
    }

    public ExcursionAdapter (Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }
    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if (excursions != null) {
            Excursion current = excursions.get(position);
            String name = current.getExcursionTitle();
//            int vacID = current.getVacationID();
            holder.excursionItemView.setText(name);
//            holder.excursionItemView2.setText(Integer.toString(vacID));

        }
        else {
            holder.excursionItemView.setText("No excursion name");
        }
    }

    public void setExcursions(List<Excursion> e) {
        excursions = e;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (excursions == null) {
            return 0;
        } else {
            return excursions.size();
        }
    }


}
