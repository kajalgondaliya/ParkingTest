package com.teco.parkingsystem.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.teco.parkingsystem.Database.DatabaseHelper;
import com.teco.parkingsystem.Database.HistoryModel;
import com.teco.parkingsystem.R;
import com.teco.parkingsystem.ui.history.HistoryActivity;

import java.util.Calendar;
import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<HistoryModel> historyModelArrayList;
    private LayoutInflater layoutInflater;
    private DatabaseHelper databaseHelper;


    public HistoryAdapter(Context context, List<HistoryModel> historyModelArrayList) {
        this.context = context;
        this.historyModelArrayList = historyModelArrayList;
        layoutInflater = LayoutInflater.from(context);

        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.history_list_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, final int position) {
        holder.name.setText(historyModelArrayList.get(position).getName());
        holder.desc.setText(historyModelArrayList.get(position).getDescription());
        if (!historyModelArrayList.get(position).getStartTime().equals("")) {
            holder.start.setText(getFormattedDate(Long.parseLong(historyModelArrayList.get(position).getStartTime())));
        } else {
            holder.start.setText("-");
        }

        if (!historyModelArrayList.get(position).getEndTime().equals("")) {
            holder.end.setText(getFormattedDate(Long.parseLong(historyModelArrayList.get(position).getEndTime())));
        } else {
            holder.end.setText("-");
        }

        if (!historyModelArrayList.get(position).getTotalTime().equals("")) {
            holder.total.setText(String.valueOf(Long.parseLong(historyModelArrayList.get(position).getTotalTime()) / 60000) + " min.");
        } else {
            holder.total.setText("-");
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHelper.deleteHistory(databaseHelper.getHistory(String.valueOf(historyModelArrayList.get(position).getId())));
                historyModelArrayList.remove(position);

                if(historyModelArrayList.size()==0){
                    ((HistoryActivity)context).llNoRecord.setVisibility(View.VISIBLE);
                    ((HistoryActivity)context).recyclerView.setVisibility(View.GONE);
                }
                notifyDataSetChanged();
            }
        });
    }

    public static String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "hh:mm aa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "Today " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }

    @Override
    public int getItemCount() {
        return historyModelArrayList.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView name, desc, start, end, total;
        private ImageButton deleteButton;

        HistoryViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
            start = itemView.findViewById(R.id.start);
            end = itemView.findViewById(R.id.end);
            total = itemView.findViewById(R.id.total);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
