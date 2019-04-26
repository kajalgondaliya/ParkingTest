package com.teco.parkingsystem.ui.history;

import android.util.Log;

import com.teco.parkingsystem.Database.DatabaseHelper;
import com.teco.parkingsystem.Database.HistoryModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryPresenterImpl implements HistoryPresenter {

    private DatabaseHelper databaseHelper;
    private ArrayList<HistoryModel> historyList;
    HistoryView view;
    private static final String TAG = "HistoryPresenterImpl";

    public HistoryPresenterImpl(HistoryView view, DatabaseHelper databaseHelper) {
        this.view = view;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public void getHistoryData() {
        historyList = (ArrayList<HistoryModel>) databaseHelper.getAllHistory();
        Log.e(TAG, "getHistoryData: "+historyList.size() );
        view.setAdapterData(historyList);
    }
}
