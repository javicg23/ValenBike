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
import disca.dadm.valenbike.interfaces.OnJourneyTaskCompleted;
import disca.dadm.valenbike.models.Journey;
import disca.dadm.valenbike.lib.RecyclerItemTouchHelper;
import disca.dadm.valenbike.tasks.JourneyAsyncTask;


public class HistoryFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, OnJourneyTaskCompleted {

    private RecyclerView recycler;
    private List<Journey> historyList = new ArrayList<>();
    private HistoryAdapter adapter = null;
    private Button delete;
    private TextView totalTime, totalMoney, tvNotHistoryRoutes;
    private List<Integer> listIdDelete = new ArrayList<>();

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

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
        tvNotHistoryRoutes = view.findViewById(R.id.tvNotHistoryRoutes);

        delete = view.findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });

        JourneyAsyncTask journeyAsyncTask = new JourneyAsyncTask(getContext(), this);
        journeyAsyncTask.execute(JourneyAsyncTask.GET_ALL_JOURNEYS);

        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {
        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recycler);
        adapter = new HistoryAdapter(historyList);
        recycler.setAdapter(adapter);
    }

    private void initData() {
        int time = 0;
        double money = 0;

        if (!historyList.isEmpty()) {
            delete.setVisibility(View.VISIBLE);
            tvNotHistoryRoutes.setVisibility(View.GONE);
            for (int i = 0; i < historyList.size(); i++) {
                time += historyList.get(i).getDuration();
                money += historyList.get(i).getPrice();
            }
        } else {
            tvNotHistoryRoutes.setVisibility(View.VISIBLE);
            delete.setVisibility(View.INVISIBLE);
        }

        totalTime.setText(time + " '");
        totalMoney.setText(money + " €");
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof HistoryAdapter.HistoryViewHolder) {
            Journey deleted = this.historyList.get(viewHolder.getAdapterPosition());
            int positionDeleted = viewHolder.getAdapterPosition();
            adapter.deleteItem(viewHolder.getAdapterPosition());
            showSnackBar(deleted, positionDeleted);
            listIdDelete.add(deleted.getId());

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
                JourneyAsyncTask journeyAsyncTask = new JourneyAsyncTask(getContext(), HistoryFragment.this);
                journeyAsyncTask.execute(JourneyAsyncTask.REMOVE_ALL_JOURNEY);
                totalTime.setText("0 '");
                totalMoney.setText("0.0 €");
            }
        });
        builder.setNegativeButton("NO", null);
        builder.create().show();
    }

    private void showSnackBar(final Journey deleted, final int position) {
        Snackbar snackbar = Snackbar.make(this.getView(), "Ruta eliminada", Snackbar.LENGTH_LONG);
        snackbar.setAction("Deshacer", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.restoreItem(deleted, position);
                delete.setVisibility(View.VISIBLE);
                listIdDelete.remove(Integer.valueOf(deleted.getId()));
                initData();
            }
        });
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbarInside, int event) {
                if (event == Snackbar.Callback.DISMISS_EVENT_SWIPE || event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT || event == Snackbar.Callback.DISMISS_EVENT_CONSECUTIVE) {
                    listIdDelete.remove(Integer.valueOf(deleted.getId()));
                    JourneyAsyncTask journeyAsyncTask = new JourneyAsyncTask(getContext(), HistoryFragment.this);
                    journeyAsyncTask.execute(JourneyAsyncTask.REMOVE_JOURNEY, String.valueOf(deleted.getId()));
                }
            }
        });
        snackbar.setActionTextColor(Color.rgb(255, 128, 255));
        snackbar.show();

        initData();
    }

    @Override
    public void responseJourneyDatabase(List<Journey> list) {
        if (list != null) {
            this.historyList.addAll(list);
            this.adapter.notifyDataSetChanged();
            initData();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        for (int i = 0; i < listIdDelete.size(); i++) {
            JourneyAsyncTask journeyAsyncTask = new JourneyAsyncTask(getContext(), HistoryFragment.this);
            journeyAsyncTask.execute(JourneyAsyncTask.REMOVE_JOURNEY, String.valueOf(listIdDelete.get(i)));
        }
    }
}

