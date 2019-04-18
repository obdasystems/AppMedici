package com.obdasystems.pocmedici.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.network.RestDrug;

import java.util.List;

public class DrugListAdapter extends RecyclerView.Adapter<DrugListAdapter.DrugViewHolder> {
    private Context mContext;
    private List<RestDrug> drugs;
    private DrugAdapterListener listener;

    public DrugListAdapter(Context mContext,
                           List<RestDrug> drugs,
                           DrugAdapterListener listener) {
        this.mContext = mContext;
        this.drugs = drugs;
        this.listener = listener;
    }

    @Override
    @NonNull
    public DrugViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drug_list_row, parent, false);

        return new DrugViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DrugViewHolder holder,
                                 final int position) {
        RestDrug drug = drugs.get(position);
        holder.drugName.setText(drug.getName());

        // display profile image
        applyDrugPicture(holder);

        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(DrugViewHolder holder,
                                  final int position) {
        holder.drugContainer.setOnClickListener(
                view -> listener.onDrugRowClicked(position));
    }

    private void applyDrugPicture(DrugViewHolder holder) {
        //Glide.with(mContext).load("https://img.icons8.com/color/48/000000/pills.png")

        //Glide.with(mContext).load(R.drawable.ic_drugs)
        //        .thumbnail(0.5f)
        //        .crossFade()
        //        .transform(new CircleTransform(mContext))
        //        .diskCacheStrategy(DiskCacheStrategy.ALL)
        //        .into(holder.imgDrug);

        holder.imgDrug.setImageResource(R.drawable.ic_drugs);
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

    public class DrugViewHolder extends RecyclerView.ViewHolder  {
        public TextView drugName, drugPrimary, drugIconText;
        public ImageView imgDrug;
        public LinearLayout drugContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;

        public DrugViewHolder(View view) {
            super(view);
            drugName = view.findViewById(R.id.drugName);
            drugPrimary = view.findViewById(R.id.drug_txt_primary);
            drugIconText = view.findViewById(R.id.drug_icon_text);
            //iconBack = view.findViewById(R.id.drug_icon_back);
            iconFront = view.findViewById(R.id.drug_icon_front);
            imgDrug = view.findViewById(R.id.drug_icon_profile);
            drugContainer = view.findViewById(R.id.drug_container);
            iconContainer = view.findViewById(R.id.drug_icon_container);
        }

    }

    public interface DrugAdapterListener {
        void onDrugRowClicked(int position);
    }

}
