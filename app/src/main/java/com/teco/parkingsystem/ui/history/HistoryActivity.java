package com.teco.parkingsystem.ui.history;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.teco.parkingsystem.Database.DatabaseHelper;
import com.teco.parkingsystem.Database.HistoryModel;
import com.teco.parkingsystem.R;
import com.teco.parkingsystem.adapter.HistoryAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryActivity extends AppCompatActivity implements HistoryView {

    @BindView(R.id.back_arrow)
    ImageView backArrow;
    @BindView(R.id.recycler_view)
    public RecyclerView recyclerView;
    @BindView(R.id.ll_no_record)
    public LinearLayout llNoRecord;

    private LinearLayoutManager linearLayoutManager;
    private HistoryAdapter historyAdapter;
    private HistoryPresenter presenter;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        databaseHelper = new DatabaseHelper(this);
        presenter = new HistoryPresenterImpl(this, new DatabaseHelper(this));

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        //get history data
        presenter.getHistoryData();

    }

    @Override
    public void setAdapterData(List<HistoryModel> arrayList) {
        historyAdapter = new HistoryAdapter(this, arrayList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(historyAdapter);
        if(arrayList.size()==0){
            recyclerView.setVisibility(View.GONE);
            llNoRecord.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.back_arrow)
    public void onClick() {
        onBackPressed();
    }
}
