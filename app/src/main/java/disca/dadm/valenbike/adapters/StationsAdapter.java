//package disca.dadm.valenbike.adapters;
//
//import android.transition.AutoTransition;
//import android.transition.TransitionManager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//import disca.dadm.valenbike.R;
//import disca.dadm.valenbike.models.StationGUI;
//
//public class StationsAdapter extends RecyclerView.Adapter<StationsAdapter.StationsViewHolder> {
//
//    private static final String TAG = "StationsAdapter";
//    private List<StationGUI> stationsList;
//
//    public StationsAdapter(List<StationGUI> stationsList) {
//        this.stationsList = stationsList;
//    }
//
//    @NonNull
//    @Override
//    public StationsAdapter.StationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_row, parent, false);
//        return new StationsAdapter.StationsViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull StationsAdapter.StationsViewHolder holder, int position) {
//        StationGUI station = stationsList.get(position);
//
//        holder.tvStationTitle.setText(station.getTitle());
//        boolean isExpanded = stationsList.get(position).isExpanded();
//        TransitionManager.beginDelayedTransition(holder.expandableLayout, new AutoTransition());
//        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
//        boolean isArrowDown = stationsList.get(position).isExpanded();
//        holder.ivArrow.setImageResource(isArrowDown ? R.drawable.ic_keyboard_arrow_up_black_24dp : R.drawable.ic_keyboard_arrow_down_black_24dp);
//
//        TextView name = (TextView) holder.expandableLayout.getViewById(R.id.tvStationName);
//        name.setText(station.getName());
//
//        TextView address = (TextView) holder.expandableLayout.getViewById(R.id.tvAdress);
//        address.setText(station.getAddress());
//
//        TextView numBikes = (TextView) holder.expandableLayout.getViewById(R.id.tvNumBikes);
//        numBikes.setText(Integer.toString(station.getNumFreeBikes()));
//
//        TextView numGaps = (TextView) holder.expandableLayout.getViewById(R.id.tvNumFrees);
//        numGaps.setText(Integer.toString(station.getNumFreeGaps()));
//
//        ImageButton favourite = (ImageButton) holder.expandableLayout.getViewById(R.id.ibFavourite);
//        if (station.isFavourite()) {
//            // TODO
//            //favourite.setImageIcon(res/drawable/ic_big_star_on);
//        }
//
//        CheckBox notify = (CheckBox) holder.expandableLayout.getViewById(R.id.cbBikes);
//        notify.setSelected(station.getNotify());
//    }
//
//    @Override
//    public int getItemCount() {
//        if(stationsList != null) {
//            return stationsList.size();
//        }
//
//        return 0;
//    }
//
//    class StationsViewHolder extends RecyclerView.ViewHolder {
//        private static final String TAG = "StationsViewHolder";
//
//        private ConstraintLayout expandableLayout;
//        private TextView tvStationTitle;
//        private ImageView ivArrow;
//
//        public StationsViewHolder(@NonNull final View itemView) {
//            super(itemView);
//            expandableLayout = itemView.findViewById(R.id.expandableLayout);
//            tvStationTitle = itemView.findViewById(R.id.tvStationTitle);
//            ivArrow = itemView.findViewById(R.id.ivArrow);
//
//            tvStationTitle.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    changeExpandibleLayout();
//                }
//            });
//
//            ivArrow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    changeExpandibleLayout();
//                }
//            });
//        }
//        private void changeExpandibleLayout() {
//            StationGUI station = stationsList.get(getAdapterPosition());
//            station.setExpanded(!station.isExpanded());
//            station.setArrowDown(!station.isArrowDown());
//            notifyItemChanged(getAdapterPosition());
//        }
//    }
//}
