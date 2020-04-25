package disca.dadm.valenbike.adapters;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.Image;
import android.text.format.DateFormat;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.fragments.MapFragment;
import disca.dadm.valenbike.interfaces.DataPassListener;
import disca.dadm.valenbike.interfaces.OnGeocoderTaskCompleted;
import disca.dadm.valenbike.models.ParametersGeocoderTask;
import disca.dadm.valenbike.models.StationGUI;

import static disca.dadm.valenbike.activities.MainActivity.CHANNEL_ID;
import static disca.dadm.valenbike.utils.Tools.coordinatesToAddress;
import static disca.dadm.valenbike.utils.Tools.getDialogProgressBar;

public class StationsAdapter extends RecyclerView.Adapter<StationsAdapter.StationsViewHolder> implements OnGeocoderTaskCompleted {

    private static final String TAG = "StationsAdapter";
    private List<StationGUI> stationsList;
    private DataPassListener dataPassListener;
    private Context context;
    private StationGUI stationGUI;
    private NotificationManagerCompat notificationManagerCompat;

    // loading progress
    private AlertDialog progressDialog;


    public StationsAdapter(List<StationGUI> stationsList, DataPassListener dataPassListener, Context context) {
        this.stationsList = stationsList;
        this.dataPassListener = dataPassListener;
        this.context = context;

    }

    @NonNull
    @Override
    public StationsAdapter.StationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_row, parent, false);
        return new StationsAdapter.StationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StationsAdapter.StationsViewHolder holder, final int position) {
        stationGUI = stationsList.get(position);
        holder.numberStation.setText(String.valueOf(stationGUI.getNumber()));
        holder.address.setText(stationGUI.getAddress());

        if (stationsList.get(position).getStatus().equals("OPEN")) {
            boolean isExpanded = stationsList.get(position).isExpanded();
            TransitionManager.beginDelayedTransition(holder.expandableLayout, new AutoTransition());
            holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            boolean isArrowDown = stationsList.get(position).isExpanded();
            holder.ivArrow.setImageResource(isArrowDown ? R.drawable.ic_keyboard_arrow_up_black_24dp : R.drawable.ic_keyboard_arrow_down_black_24dp);

            progressDialog = getDialogProgressBar(context).create();

            holder.numFreeBikes.setText(String.valueOf(stationGUI.getAvailableBikes()));
            holder.numFreeGaps.setText(String.valueOf(stationGUI.getAvailableBikeStands()));

            // TODO: Implementar calculo de distancia desde la posicion actual hasta la estacion.
            //holder.distance.setText(stationGUI.getTime());


            long now = new Date().getTime();
            String dateString = DateFormat.format("HH:mm", new Date(now - stationGUI.getLastUpdate())).toString();
            holder.lastUpdate.setText(dateString);

            holder.banking.setSelected(stationGUI.getBanking());

            holder.ibShowDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressDialog();
                    coordinatesToAddress(context, StationsAdapter.this, ParametersGeocoderTask.LOCATION_STATION, new LatLng(stationGUI.getPosition().getLat(), stationGUI.getPosition().getLng()));
                }
            });

            holder.ibShowMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataPassListener.passStationToMap(stationGUI.getNumber());
                }
            });

            holder.reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        holder.reminder.setBackgroundResource(R.drawable.ic_notifications_active_golden_24dp);
                        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle("¡Ya hay bicis disponibles!")
                                .setContentText("En " + stationsList.get(position).getAddress() + " ya hay bicis disponibles")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                                .build();
                        notificationManagerCompat = NotificationManagerCompat.from(context);
                        notificationManagerCompat.notify(1, notification);
                    } else {
                        holder.reminder.setBackgroundResource(R.drawable.ic_notifications_none_golden_24dp);
                    }
                }
            });
        } else {
            holder.expandableLayout.setVisibility(View.GONE);
            holder.ivArrow.setVisibility(View.GONE);
            holder.reminder.setVisibility(View.GONE);
            holder.numFreeBikes.setVisibility(View.GONE);
            holder.numFreeGaps.setVisibility(View.GONE);
            holder.ivBike.setVisibility(View.GONE);
            holder.ivParking.setVisibility(View.GONE);
            holder.favourite.setVisibility(View.GONE);
            holder.closedStation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if(stationsList != null) {
            return stationsList.size();
        }

        return 0;
    }

    @Override
    public void receivedAddressGPS(String address) {

    }

    @Override
    public void receivedAddressMarker(String address) {

    }

    @Override
    public void receivedAddressStation(String address) {
        dataPassListener.passLocationToDirection(null, "", new LatLng(stationGUI.getPosition().getLat(), stationGUI.getPosition().getLng()), address);
        hideProgressDialog();
    }

    private void showProgressDialog() {
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    class StationsViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "StationsViewHolder";

        private ConstraintLayout expandableLayout;
        private TextView numberStation, numFreeBikes, numFreeGaps, distance, address, lastUpdate;
        private ImageView ivArrow, banking, ivBike, ivParking;
        private CheckBox reminder, favourite;
        private ImageButton ibShowMap, ibShowDistance;
        private Button closedStation;

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
            ibShowDistance = itemView.findViewById(R.id.ibShowDistance);
            ibShowMap = itemView.findViewById(R.id.ibShowMap);
            closedStation = itemView.findViewById(R.id.btnClosedStation);
            ivBike = itemView.findViewById(R.id.ivBikeStation);
            ivParking = itemView.findViewById(R.id.ivParkingStation);

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

        }

        private void changeExpandibleLayout() {
            StationGUI station = stationsList.get(getAdapterPosition());
            station.setExpanded(!station.isExpanded());
            station.setArrowDown(!station.isArrowDown());
            notifyItemChanged(getAdapterPosition());
        }
    }
}
