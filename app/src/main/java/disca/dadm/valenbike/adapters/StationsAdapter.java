package disca.dadm.valenbike.adapters;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.text.format.DateFormat;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.database.ValenbikeSQLiteOpenHelper;
import disca.dadm.valenbike.fragments.StationsFragment;
import disca.dadm.valenbike.interfaces.DataPassListener;
import disca.dadm.valenbike.interfaces.OnGeocoderTaskCompleted;
import disca.dadm.valenbike.interfaces.OnStationTaskCompleted;
import disca.dadm.valenbike.models.ParametersGeocoderTask;
import disca.dadm.valenbike.models.StationGUI;
import disca.dadm.valenbike.tasks.StationsAsyncTask;

import static disca.dadm.valenbike.activities.MainActivity.CHANNEL_ID;
import static disca.dadm.valenbike.utils.Tools.cleanString;
import static disca.dadm.valenbike.utils.Tools.coordinatesToAddress;
import static disca.dadm.valenbike.utils.Tools.getDialogProgressBar;

public class StationsAdapter extends RecyclerView.Adapter<StationsAdapter.StationsViewHolder> implements OnGeocoderTaskCompleted, Filterable {

    private List<StationGUI> stationsList;
    private List<StationGUI> stationsListFull;
    private DataPassListener dataPassListener;
    private Context context;
    private StationGUI stationGUI;
    private NotificationManagerCompat notificationManagerCompat;
    private TextView tvNotFavouriteText;

    // loading progress
    private AlertDialog progressDialog;


    public StationsAdapter(List<StationGUI> stationsList, DataPassListener dataPassListener, Context context, TextView tvNotFavouriteText) {
        this.stationsList = stationsList;
        this.stationsListFull = new ArrayList<>(stationsList);
        this.dataPassListener = dataPassListener;
        this.context = context;
        this.tvNotFavouriteText = tvNotFavouriteText;
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

        boolean open = changeXML(stationGUI.getStatus().equals("OPEN"), holder, position);
        if (open) {
            boolean isExpanded = stationsList.get(position).isExpanded();
            TransitionManager.beginDelayedTransition(holder.expandableLayout, new AutoTransition());
            holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            boolean isArrowDown = stationsList.get(position).isExpanded();
            holder.ivArrow.setImageResource(isArrowDown ? R.drawable.ic_keyboard_arrow_up_black_24dp : R.drawable.ic_keyboard_arrow_down_black_24dp);

            progressDialog = getDialogProgressBar(context).create();


            holder.numFreeBikes.setText(String.valueOf(stationGUI.getAvailableBikes()));
            holder.numFreeGaps.setText(String.valueOf(stationGUI.getAvailableBikeStands()));

            long now = new Date().getTime();
            String dateString = DateFormat.format("HH:mm", new Date(now - stationGUI.getLastUpdate())).toString();
            holder.lastUpdate.setText(dateString);

            holder.banking.setVisibility(stationGUI.getBanking() ? View.VISIBLE : View.GONE);

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

            if (stationsList.get(position).getAvailableBikes() == 0) {
                boolean isReminderCheck = stationsList.get(position).isReminderCheck();
                holder.reminder.setBackgroundResource(isReminderCheck ? R.drawable.ic_notifications_active_golden_24dp : R.drawable.ic_notifications_none_golden_24dp);
            }

            boolean isFavouriteCheck = stationsList.get(position).isFavouriteCheck();
            holder.favourite.setBackgroundResource(isFavouriteCheck ? R.drawable.ic_favorite_magenta_24dp : R.drawable.ic_favorite_border_magenta_24dp);
        }
    }

    @Override
    public int getItemCount() {
        if (stationsList != null) {
            return stationsList.size();
        }

        return 0;
    }

    private boolean changeXML(boolean open, final StationsAdapter.StationsViewHolder holder, int position) {
        if (open) {
            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.changeExpandibleLayout();
                }
            });
            holder.expandableLayout.setVisibility(View.VISIBLE);
            holder.ivArrow.setVisibility(View.VISIBLE);
            if (stationsList.get(position).getAvailableBikes() == 0) {
                holder.reminder.setVisibility(View.VISIBLE);
            }
            holder.numFreeBikes.setVisibility(View.VISIBLE);
            holder.numFreeGaps.setVisibility(View.VISIBLE);
            holder.ivBike.setVisibility(View.VISIBLE);
            holder.ivParking.setVisibility(View.VISIBLE);
            holder.favourite.setVisibility(View.VISIBLE);
            holder.closedStation.setVisibility(View.GONE);
        } else {
            holder.constraintLayout.setOnClickListener(null);
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
        return open;
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

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<StationGUI> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(stationsListFull);
            } else {
                String filterPattern = cleanString(constraint.toString().toLowerCase().trim());
                for (StationGUI item : stationsListFull) {
                    if (cleanString(item.getAddress()).toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            stationsList.clear();
            stationsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class StationsViewHolder extends RecyclerView.ViewHolder implements OnStationTaskCompleted {
        private static final String TAG = "StationsViewHolder";

        private ConstraintLayout expandableLayout, constraintLayout;
        private TextView numberStation, numFreeBikes, numFreeGaps, address, lastUpdate;
        private ImageView ivArrow, banking, ivBike, ivParking;
        private CheckBox reminder, favourite;
        private ImageButton ibShowMap, ibShowDistance;
        private Button closedStation;

        public StationsViewHolder(@NonNull final View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            constraintLayout = itemView.findViewById(R.id.stationRowLayout);
            numberStation = itemView.findViewById(R.id.sheetNumberStation);
            numFreeBikes = itemView.findViewById(R.id.tvNumFreeBikes);
            numFreeGaps = itemView.findViewById(R.id.tvNumFreeGaps);
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


            constraintLayout.setOnClickListener(new View.OnClickListener() {
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
                    StationGUI station = stationsList.get(getAdapterPosition());
                    if (!station.isFavouriteCheck()) {
                        station.setFavouriteCheck(!station.isFavouriteCheck());
                        StationsAsyncTask stationsAsyncTask = new StationsAsyncTask(context, StationsViewHolder.this);
                        stationsAsyncTask.execute(StationsAsyncTask.INSERT_STATION, station.getNumber(), ValenbikeSQLiteOpenHelper.ENABLED_FAVREMIND,
                                station.isReminderCheck() ? ValenbikeSQLiteOpenHelper.ENABLED_FAVREMIND : ValenbikeSQLiteOpenHelper.DISABLED_FAVREMIND);
                        favourite.setBackgroundResource(R.drawable.ic_favorite_magenta_24dp);
                        StationsFragment.stationFavouriteList.add(station);
                    } else {
                        station.setFavouriteCheck(!station.isFavouriteCheck());
                        StationsAsyncTask stationsAsyncTask = new StationsAsyncTask(context, StationsViewHolder.this);
                        stationsAsyncTask.execute(StationsAsyncTask.UPDATE_STATION, station.getNumber(), ValenbikeSQLiteOpenHelper.DISABLED_FAVREMIND,
                                station.isReminderCheck() ? ValenbikeSQLiteOpenHelper.ENABLED_FAVREMIND : ValenbikeSQLiteOpenHelper.DISABLED_FAVREMIND);
                        favourite.setBackgroundResource(R.drawable.ic_favorite_border_magenta_24dp);
                        StationsFragment.stationFavouriteList.remove(station);
                        if (StationsFragment.favouriteChecked && StationsFragment.stationFavouriteList.size() == 0) {
                            tvNotFavouriteText.setVisibility(View.VISIBLE);
                        }
                    }
                    notifyItemChanged(getAdapterPosition());
                }
            });

            reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    StationGUI station = stationsList.get(getAdapterPosition());
                    if (station.getAvailableBikes() == 0) {
                        reminder.setVisibility(View.VISIBLE);
                        if (!station.isReminderCheck()) {
                            station.setReminderCheck(!station.isReminderCheck());
                            StationsAsyncTask stationsAsyncTask = new StationsAsyncTask(context, StationsViewHolder.this);
                            stationsAsyncTask.execute(StationsAsyncTask.INSERT_STATION, station.getNumber(),
                                    station.isFavouriteCheck() ? ValenbikeSQLiteOpenHelper.ENABLED_FAVREMIND : ValenbikeSQLiteOpenHelper.DISABLED_FAVREMIND, ValenbikeSQLiteOpenHelper.ENABLED_FAVREMIND);
                            reminder.setBackgroundResource(R.drawable.ic_notifications_active_golden_24dp);

                            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_notification)
                                    .setContentTitle("¡Ya hay bicis disponibles!")
                                    .setContentText("En " + station.getAddress() + " ya hay bicis disponibles")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                                    .build();
                            notificationManagerCompat = NotificationManagerCompat.from(context);
                            notificationManagerCompat.notify(1, notification);
                        } else {
                            station.setReminderCheck(!station.isReminderCheck());
                            StationsAsyncTask stationsAsyncTask = new StationsAsyncTask(context, StationsViewHolder.this);
                            stationsAsyncTask.execute(StationsAsyncTask.UPDATE_STATION, station.getNumber(),
                                    station.isFavouriteCheck() ? ValenbikeSQLiteOpenHelper.ENABLED_FAVREMIND : ValenbikeSQLiteOpenHelper.DISABLED_FAVREMIND, ValenbikeSQLiteOpenHelper.DISABLED_FAVREMIND);
                            reminder.setBackgroundResource(R.drawable.ic_notifications_none_golden_24dp);
                        }
                    }
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }

        private void changeExpandibleLayout() {
            StationGUI station = stationsList.get(getAdapterPosition());
            station.setExpanded(!station.isExpanded());
            station.setArrowDown(!station.isArrowDown());
            notifyItemChanged(getAdapterPosition());
        }

        @Override
        public void responseStationDatabase(List<StationGUI> list) {

        }
    }
}
