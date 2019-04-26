package com.teco.parkingsystem.ui.history;

import com.teco.parkingsystem.Database.HistoryModel;

import java.util.List;

public interface HistoryView {

    void setAdapterData(List<HistoryModel> arrayList);
}
