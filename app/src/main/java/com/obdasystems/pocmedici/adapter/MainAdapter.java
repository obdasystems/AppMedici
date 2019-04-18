package com.obdasystems.pocmedici.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.listener.OnMainRecyclerViewItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private final LayoutInflater inflater;
    private OnMainRecyclerViewItemClickListener mClickListener;
    private List<String> imageNames;
    private Context ctx;

    public MainAdapter(Context context) {
        this.ctx = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MainAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View itemView = inflater.inflate(R.layout.form_reciclerview_item, parent, false);
        View itemView = inflater.inflate(R.layout.main_recycler_row, parent, false);
        return new MainAdapter.MainViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MainAdapter.MainViewHolder holder, int position) {
        if(imageNames!=null && imageNames.size()>0 ) {
            String imageName = imageNames.get(position);
            int id = ctx.getResources()
                    .getIdentifier(imageName, "drawable", ctx.getPackageName());
            Picasso.with(ctx)
                    .load(id)
                    .resize(1000, 250)
                    .into(holder.rowImageView);
        }
    }

    @Override
    public int getItemCount() {
        if(imageNames != null) {
            return imageNames.size();
        }
        return 0;
    }

    // CUSTOM METHODS
    public void setImages(List<String> imageNames) {
        this.imageNames = imageNames;
    }

    public void setOnItemClickListener(final OnMainRecyclerViewItemClickListener listener) {
        this.mClickListener = listener;
    }

    class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView rowImageView;

        private MainViewHolder(View itemView) {
            super(itemView);
            rowImageView = itemView.findViewById(R.id.main_recycler_row_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mClickListener!=null) {
                mClickListener.onItemClick(v, getLayoutPosition());
            }
        }
    }

}
