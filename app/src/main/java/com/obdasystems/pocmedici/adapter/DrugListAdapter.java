package com.obdasystems.pocmedici.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.message.helper.CircleTransform;
import com.obdasystems.pocmedici.network.RestDrug;

import java.util.List;

public class DrugListAdapter extends RecyclerView.Adapter<DrugListAdapter.MyViewHolder> {
    private Context mContext;
    private List<RestDrug> drugs;
    private DrugListAdapter.DrugAdapterListener listener;




    public DrugListAdapter(Context mContext, List<RestDrug> drugs, DrugListAdapter.DrugAdapterListener listener) {
        this.mContext = mContext;
        this.drugs = drugs;
        this.listener = listener;
    }

    @Override
    public DrugListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drug_list_row, parent, false);

        return new DrugListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DrugListAdapter.MyViewHolder holder, final int position) {
        RestDrug drug = drugs.get(position);

        String drugName = drug.getName();
        holder.drugName.setText(drug.getName());

        // display profile image
        applyDrugPicture(holder);

        // apply click events
        applyClickEvents(holder, position);
    }



    private void applyClickEvents(DrugListAdapter.MyViewHolder holder, final int position) {
        holder.drugContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDrugRowClicked(position);
            }
        });
    }

    private void applyDrugPicture(DrugListAdapter.MyViewHolder holder) {
        //Glide.with(mContext).load("https://img.icons8.com/color/48/000000/pills.png")

        Glide.with(mContext).load("https://img.icons8.com/color/48/000000/walter-white.png")
                .thumbnail(0.5f)
                .crossFade()
                .transform(new CircleTransform(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgDrug);

        holder.imgDrug.setColorFilter(null);
        holder.drugIconText.setVisibility(View.GONE);
    }




    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }


    @Override
    public int getItemCount() {
        return drugs.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder  {
        public TextView drugName, drugPrimary, drugIconText;
        public ImageView imgDrug;
        public LinearLayout drugContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;

        public MyViewHolder(View view) {
            super(view);
            drugName = (TextView) view.findViewById(R.id.drugName);
            drugPrimary = (TextView) view.findViewById(R.id.drug_txt_primary);
            drugIconText = (TextView) view.findViewById(R.id.drug_icon_text);
            iconBack = (RelativeLayout) view.findViewById(R.id.drug_icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.drug_icon_front);
            imgDrug = (ImageView) view.findViewById(R.id.drug_icon_profile);
            drugContainer = (LinearLayout) view.findViewById(R.id.drug_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.drug_icon_container);
        }

    }

    public interface DrugAdapterListener {

        void onDrugRowClicked(int position);
    }
}
