package disca.dadm.valenbike.adapters;

import android.text.format.DateFormat;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.models.StationGUI;

public class StationsAdapter extends RecyclerView.Adapter<StationsAdapter.StationsViewHolder> {

    private static final String TAG = "StationsAdapter";
    private List<StationGUI> stationsList;

    public StationsAdapter(List<StationGUI> stationsList) {
        this.stationsList = stationsList;
    }

    @NonNull
    @Override
    public StationsAdapter.StationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_row, parent, false);
        return new StationsAdapter.StationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StationsAdapter.StationsViewHolder holder, final int position) {
        boolean isExpanded = stationsList.get(position).isExpanded();
        TransitionManager.beginDelayedTransition(holder.expandableLayout, new AutoTransition());
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        boolean isArrowDown = stationsList.get(position).isExpanded();
        holder.ivArrow.setImageResource(isArrowDown ? R.drawable.ic_keyboard_arrow_up_black_24dp : R.drawable.ic_keyboard_arrow_down_black_24dp);

        StationGUI stationGUI = stationsList.get(position);
        holder.numberStation.setText(String.valueOf(stationGUI.getNumber()));
        holder.numFreeBikes.setText(String.valueOf(stationGUI.getAvailableBikes()));
        holder.numFreeGaps.setText(String.valueOf(stationGUI.getAvailableBikeStands()));

        // TODO: Implementar calculo de distancia desde la posicion actual hasta la estacion.
        //holder.distance.setText(stationGUI.getTime());
        
        holder.address.setText(stationGUI.getAddress());

        long now = new Date().getTime();
        String dateString = DateFormat.format("HH:mm", new Date(now - stationGUI.getLastUpdate())).toString();
        holder.lastUpdate.setText(dateString);

        holder.banking.setSelected(stationGUI.getBanking());

    }

    @Override
    public int getItemCount() {
        if(stationsList != null) {
            return stationsList.size();
        }

        return 0;
    }

    class StationsViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "StationsViewHolder";

        private ConstraintLayout expandableLayout;
        private TextView numberStation, numFreeBikes, numFreeGaps, distance, address, lastUpdate;
        private ImageView ivArrow, banking;
        private CheckBox reminder, favourite;

        public StationsViewHolder(@NonNull final View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            numberStation = itemView.findViewById(R.id.sheetNumberStation);
            numFreeBikes = itemView.findViewById(R.id.tvNumFreeBikes);
            numFreeGaps = itemView.findViewById(R.id.tvNumFreeGaps);
            distance = itemView.findViewById(R.id.tvDistance);
            address = itemView.findViewById(R.id.tvAddress);
            lastUpdate = itemView.findViewById(R.id.tvLastUpdate);
            banking = itemView.findViewById(R.id.ivBanking);
            reminder = itemView.findViewById(R.id.sheetReminder);
            favourite = itemView.findViewById(R.id.sheetFavourite);
            ivArrow = itemView.findViewById(R.id.ivArrow);

            numberStation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeExpandibleLayout();
                }
            });

            ivArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeExpandibleLayout();
                }
            });

            favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        favourite.setBackgroundResource(R.drawable.ic_favorite_magenta_24dp);
                    } else {
                        favourite.setBackgroundResource(R.drawable.ic_favorite_border_magenta_24dp);
                    }
                }
            });

            reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        reminder.setBackgroundResource(R.drawable.ic_notifications_active_golden_24dp);
                    } else {
                        reminder.setBackgroundResource(R.drawable.ic_notifications_none_golden_24dp);
                    }
                }
            });

        }
        private void changeExpandibleLayout() {
            StationGUI station = stationsList.get(getAdapterPosition());
            station.setExpanded(!station.isExpanded());
            station.setArrowDown(!station.isArrowDown());
            notifyItemChanged(getAdapterPosition());
        }
    }
}
