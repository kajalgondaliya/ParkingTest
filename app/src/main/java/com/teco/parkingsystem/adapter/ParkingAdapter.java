package com.teco.parkingsystem.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.teco.parkingsystem.Models.Result;
import com.teco.parkingsystem.R;

import java.util.List;


public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.MyViewHolder> {

    Context context;
    List<Result> results;
    ItemClick listener;

    public ParkingAdapter(Context context, List<Result> results, ItemClick listener) {

        this.context = context;
        this.results = results;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        Result result = results.get(i);
        myViewHolder.title.setText(result.getName());
        myViewHolder.desc.setText(result.getVicinity());

        if (result.getPhotos() != null && result.getPhotos().size() > 0) {
            String path = context.getString(R.string.img_path, result.getPhotos().get(0).getPhotoReference(), context.getString(R.string.server_key));

            Glide.with(context).load(path).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).apply(new RequestOptions().placeholder(R.drawable.ic_parking)).into(myViewHolder.imageView);

        }else {
            Glide.with(context).load(result.getIcon()).apply(new RequestOptions().placeholder(R.drawable.ic_parking)).into(myViewHolder.imageView);

        }

        myViewHolder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc;
        ImageView imageView;
        LinearLayout llMain;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivImg);
            title = itemView.findViewById(R.id.tvTitle);
            desc = itemView.findViewById(R.id.tvDesc);
            llMain = itemView.findViewById(R.id.llMain);
        }
    }

    public interface ItemClick {
        public void onItemClick(int pos);
    }
}
