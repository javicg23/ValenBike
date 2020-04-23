package disca.dadm.valenbike.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import disca.dadm.valenbike.R;
import disca.dadm.valenbike.adapters.HistoryAdapter;
import disca.dadm.valenbike.database.Journey;
import disca.dadm.valenbike.lib.RecyclerItemTouchHelper;

import disca.dadm.valenbike.tasks.HistoryAsyncTask;


public class HistoryFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private RecyclerView recycler;
    private List<Journey> historyList = new ArrayList<Journey>();
    private HistoryAdapter adapter = null;
    private Button delete;
    private TextView totalTime, totalMoney;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recycler = view.findViewById(R.id.recyclerHistory);
        totalTime = view.findViewById(R.id.totalTime);
        totalMoney = view.findViewById(R.id.totalMoney);

        delete = view.findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });

        initRecyclerView();

        HistoryAsyncTask task = new HistoryAsyncTask(this);
        task.execute();

        return view;
    }

    private void initRecyclerView() {
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler);
        adapter = new HistoryAdapter(historyList);
        recycler.setAdapter(adapter);
    }

    public void getList (List<Journey> list) {
        this.historyList.addAll(list);
        this.adapter.notifyDataSetChanged();
        initData();
    }

    private void initData(){
        if (!historyList.isEmpty()) delete.setVisibility(View.VISIBLE);
        int time=0, money=0;

        for(int i =0; i<historyList.size(); i++) {
            time+= historyList.get(i).getTotalTime();
            money+= historyList.get(i).getTotalCost();
        }

        totalTime.setText(time + " ");
        totalMoney.setText(money + " €");
    }
    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof HistoryAdapter.HistoryViewHolder) {
            Journey deleted = this.historyList.get(viewHolder.getAdapterPosition());
            int positionDeleted = viewHolder.getAdapterPosition();
            adapter.deleteItem(viewHolder.getAdapterPosition());
            showSnackBar(deleted, positionDeleted);

            if (this.historyList.isEmpty()) delete.setVisibility(View.INVISIBLE);
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("¡CUIDADO!");
        builder.setMessage("¿Desea eliminar completamente su historial?");
        builder.setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.clear();
                delete.setVisibility(View.INVISIBLE);

            }
        });
        builder.setNegativeButton("NO",null);
        builder.create().show();
    }

    private void showSnackBar (final Journey deleted, final int position) {
        Snackbar snackbar = Snackbar.make(this.getView(), "Historial eliminado", Snackbar.LENGTH_LONG);
        snackbar.setAction("Deshacer", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleted, position);
                    delete.setVisibility(View.VISIBLE);
                }
            });
            snackbar.setActionTextColor(Color.BLUE);
            snackbar.show();
    }

}

