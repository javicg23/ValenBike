package disca.dadm.valenbike.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.models.History;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private static final String TAG = "HistoryAdapter";
    private List<History> historyList;

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.date.setText(history.getDate());
        holder.time.setText(history.getTime());
        holder.origin.setText(history.getOrigin());
        holder.destination.setText((history.getDestination()));
        holder.distance.setText(history.getDistance());
        holder.money.setText(history.getMoney());
    }

    @Override
    public int getItemCount() {
        return this.historyList.size();
    }


    class HistoryViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "HistoryViewHolder";
        private TextView date, time, origin, destination, distance, money;

        public HistoryViewHolder(@NonNull final View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateHistory);
            time = itemView.findViewById(R.id.timeHistory);
            origin = itemView.findViewById(R.id.originStation);
            destination =itemView.findViewById(R.id.destinationStation);
            distance = itemView.findViewById(R.id.distanceHistory);
            money = itemView.findViewById(R.id.moneyHistory);
        }
    }
}
