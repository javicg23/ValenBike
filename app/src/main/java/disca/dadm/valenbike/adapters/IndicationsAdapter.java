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

import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.models.Indications;
import disca.dadm.valenbike.models.Information;

public class IndicationsAdapter extends RecyclerView.Adapter<IndicationsAdapter.IndicationsViewHolder> {
    private static final String TAG = "IndicationsAdapter";
    private List<Indications> indications;

    public IndicationsAdapter(List<Indications> indications) {
        this.indications = indications;
    }

    @NonNull
    @Override
    public IndicationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.indications_card, parent, false);
        return new IndicationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IndicationsViewHolder holder, int position) {
        Indications indication = indications.get(position);
        holder.tvDuration.setText(indication.getDuration());
        holder.tvDistance.setText(indication.getDistance());
        holder.tvAddress.setText(indication.getAddress());
        holder.tvBody.setText(indication.getIndications());
        if (indication.getMode() == Indications.MODE_BIKE) {
            holder.ivMode.setBackground(holder.ivMode.getContext().getResources().getDrawable(R.drawable.ic_directions_bike_primary_24dp, null));
        }

        boolean isExpanded = indications.get(position).isExpanded();
        TransitionManager.beginDelayedTransition(holder.expandableLayout, new AutoTransition());
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        boolean isArrowDown = indications.get(position).isExpanded();
        holder.ivArrow.setImageResource(isArrowDown ? R.drawable.ic_keyboard_arrow_up_black_24dp : R.drawable.ic_keyboard_arrow_down_black_24dp);

    }

    @Override
    public int getItemCount() {
        return indications.size();
    }

    class IndicationsViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "IndicationsViewHolder";

        private ConstraintLayout expandableLayout;
        private TextView tvAddress, tvDuration, tvDistance, tvBody;
        private ImageView ivArrow, ivMode;

        public IndicationsViewHolder(@NonNull final View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.indicationsExpandableLayout);
            ivArrow = itemView.findViewById(R.id.indicationsCardArrow);
            ivMode = itemView.findViewById(R.id.indicationsCardMode);
            tvAddress = itemView.findViewById(R.id.indicationsCardAddress);
            tvDuration = itemView.findViewById(R.id.indicationsCardDuration);
            tvDistance = itemView.findViewById(R.id.indicationsCardDistance);
            tvBody = itemView.findViewById(R.id.indicationsBody);

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


            tvDistance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeExpandibleLayout();
                }
            });

            tvDuration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeExpandibleLayout();
                }
            });
        }
        private void changeExpandibleLayout() {
            Indications indication = indications.get(getAdapterPosition());
            indication.setExpanded(!indication.isExpanded());
            indication.setArrowDown(!indication.isArrowDown());
            notifyItemChanged(getAdapterPosition());
        }
    }
}
