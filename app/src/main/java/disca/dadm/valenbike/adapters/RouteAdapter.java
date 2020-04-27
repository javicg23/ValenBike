package disca.dadm.valenbike.adapters;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.models.Indication;
import disca.dadm.valenbike.models.Route;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private List<Route> routes;

    public RouteAdapter(List<Route> routes) {
        this.routes = routes;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_row, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route route = routes.get(position);
        holder.tvDuration.setText(route.getDuration());
        holder.tvDistance.setText(route.getDistance());
        holder.tvAddress.setText(route.getAddress());
        holder.ivMode.setImageResource(route.getMode() == Route.MODE_BIKE ? R.drawable.ic_directions_bike_primary_24dp : R.drawable.ic_directions_walk_primary_24dp);

        initIndicationsDataRecycler(holder, route);

        boolean isExpanded = routes.get(position).isExpanded();
        TransitionManager.beginDelayedTransition(holder.expandableLayout, new AutoTransition());
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        boolean isArrowDown = routes.get(position).isExpanded();
        holder.ivArrow.setImageResource(isArrowDown ? R.drawable.ic_keyboard_arrow_up_black_24dp : R.drawable.ic_keyboard_arrow_down_black_24dp);

    }

    private void initIndicationsDataRecycler(RouteViewHolder holder, Route route) {
        JSONArray steps = route.getIndications();
        List<Indication> indications = new ArrayList<>();
        for (int i = 0; i < steps.length(); i++) {
            String address = null;
            try {
                address = steps.getJSONObject(i).getString("html_instructions");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Indication indication = new Indication(address);
            indications.add(indication);
        }
        IndicationAdapter indicationAdapter = new IndicationAdapter(indications);
        holder.recyclerView.setAdapter(indicationAdapter);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "RouteViewHolder";

        private ConstraintLayout expandableLayout;
        private TextView tvAddress, tvDuration, tvDistance;
        private ImageView ivArrow, ivMode;
        private RecyclerView recyclerView;

        public RouteViewHolder(@NonNull final View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.indicationsExpandableLayout);
            ivArrow = itemView.findViewById(R.id.indicationsCardArrow);
            ivMode = itemView.findViewById(R.id.indicationsCardMode);
            tvAddress = itemView.findViewById(R.id.indicationsCardAddress);
            tvDuration = itemView.findViewById(R.id.indicationsCardDuration);
            tvDistance = itemView.findViewById(R.id.indicationsCardDistance);
            recyclerView = itemView.findViewById(R.id.specifiedIndicationsRecyclerView);

            ivMode.setOnClickListener(new View.OnClickListener() {
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

            tvAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeExpandibleLayout();
                }
            });

        }

        private void changeExpandibleLayout() {
            Route route = routes.get(getAdapterPosition());
            route.setExpanded(!route.isExpanded());
            route.setArrowDown(!route.isArrowDown());
            notifyItemChanged(getAdapterPosition());
        }
    }
}
