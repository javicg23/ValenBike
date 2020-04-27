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
import disca.dadm.valenbike.models.Information;

public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.InformationViewHolder> {

    private List<Information> informationList;

    public InformationAdapter(List<Information> informationList) {
        this.informationList = informationList;
    }

    @NonNull
    @Override
    public InformationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_row, parent, false);
        return new InformationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InformationViewHolder holder, int position) {
        Information information = informationList.get(position);

        holder.tvTitle.setText(information.getTitle());
        holder.tvBody.setText(information.getBody());
        boolean isExpanded = informationList.get(position).isExpanded();
        TransitionManager.beginDelayedTransition(holder.expandableLayout, new AutoTransition());
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        boolean isArrowDown = informationList.get(position).isExpanded();
        holder.ivArrow.setImageResource(isArrowDown ? R.drawable.ic_keyboard_arrow_up_black_24dp : R.drawable.ic_keyboard_arrow_down_black_24dp);

    }

    @Override
    public int getItemCount() {
        return informationList.size();
    }

    class InformationViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "InformationViewHolder";

        private ConstraintLayout expandableLayout;
        private TextView tvTitle, tvBody;
        private ImageView ivArrow;

        public InformationViewHolder(@NonNull final View itemView) {
            super(itemView);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            tvTitle = itemView.findViewById(R.id.tvStationNum);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivArrow = itemView.findViewById(R.id.ivArrow);

            tvTitle.setOnClickListener(new View.OnClickListener() {
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
        }

        private void changeExpandibleLayout() {
            Information information = informationList.get(getAdapterPosition());
            information.setExpanded(!information.isExpanded());
            information.setArrowDown(!information.isArrowDown());
            notifyItemChanged(getAdapterPosition());
        }
    }
}
