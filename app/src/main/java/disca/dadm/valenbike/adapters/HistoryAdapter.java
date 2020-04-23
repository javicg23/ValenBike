package disca.dadm.valenbike.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.database.Journey;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private static final String TAG = "HistoryAdapter";
    private List<Journey> historyList;

    public HistoryAdapter(List<Journey> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, final int position) {
        Journey history = historyList.get(position);
        holder.origin.setText(history.getOrigin());
        holder.destination.setText((history.getDestination()));
        holder.date.setText(new SimpleDateFormat("dd/MM/YYYY").format(history.getDate()));
        holder.time.setText(history.getTotalTime() + "'");
        holder.distance.setText(history.getDistance() + " km");
        holder.money.setText(history.getTotalCost()+ " €");
    }

    @Override
    public int getItemCount() {
        return this.historyList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "HistoryViewHolder";
        private TextView date, time, origin, destination, distance, money;
        public LinearLayout layoutDelete;

        public HistoryViewHolder(@NonNull final View itemView) {
            super(itemView);
            layoutDelete = itemView.findViewById(R.id.layoutDelete);
            origin = itemView.findViewById(R.id.originStation);
            destination = itemView.findViewById(R.id.destinationStation);
            date = itemView.findViewById(R.id.dateHistory);
            time = itemView.findViewById(R.id.timeHistory);
            distance = itemView.findViewById(R.id.distanceHistory);
            money = itemView.findViewById(R.id.moneyHistory);
        }
    }

    public void deleteItem(int position) {
        this.historyList.remove(position);
        this.notifyItemRemoved(position);
    }

    public void clear() {
        this.historyList.clear();
        this.notifyDataSetChanged();
    }

    public void restoreItem (Journey deleted, int position) {
        this.historyList.add(position, deleted);
        this.notifyItemInserted(position);
    }
}


