package disca.dadm.valenbike.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.models.Indication;


public class IndicationAdapter extends RecyclerView.Adapter<IndicationAdapter.IndicationViewHolder> {

    private List<Indication> indications;

    public IndicationAdapter(List<Indication> indications) {
        this.indications = indications;
    }

    @NonNull
    @Override
    public IndicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.indications_row, parent, false);
        return new IndicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IndicationViewHolder holder, int position) {
        Indication indication = indications.get(position);
        String res = Html.fromHtml(indication.getText()).toString();
        holder.tvText.setText(res);
    }


    @Override
    public int getItemCount() {
        return indications.size();
    }

    class IndicationViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG = "IndicationViewHolder";

        private TextView tvText;

        public IndicationViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.indicationsText);


        }
    }
}
