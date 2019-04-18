package com.obdasystems.pocmedici.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
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
import com.obdasystems.pocmedici.message.helper.FlipAnimator;
import com.obdasystems.pocmedici.persistence.entities.JoinFormWithMaxPageNumberData;

import java.util.ArrayList;
import java.util.List;

public class NewFormListAdapter extends RecyclerView.Adapter<NewFormListAdapter.MyViewHolder> {
    private Context mContext;
    //private List<Message> messages;
    private List<JoinFormWithMaxPageNumberData> forms;
    private NewFormListAdapter.FormAdapterListener listener;
    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public NewFormListAdapter(Context mContext, List<JoinFormWithMaxPageNumberData> forms, NewFormListAdapter.FormAdapterListener listener) {
        this.mContext = mContext;
        this.forms = forms;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    //CUSTOM METHODS
    public void setForms(List<JoinFormWithMaxPageNumberData> forms) {
        this.forms = forms;
    }

    public JoinFormWithMaxPageNumberData getFormAtPosition(int position) {
        if(this.forms!=null) {
            return this.forms.get(position);
        }
        return null;
    }

    @Override
    public NewFormListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.form_list_row, parent, false);

        return new NewFormListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NewFormListAdapter.MyViewHolder holder, final int position) {
        JoinFormWithMaxPageNumberData formData = forms.get(position);

        holder.title.setText(formData.getFormTitle());
        // FIXME: read form periodicity from rest response
        holder.primaryText.setText("Periodicity: Weekly");
        holder.secondaryText.setText("Number of pages: "+formData.getLastPageNumber());
        holder.timestamp.setText("12/03/2019");//TODO DISPLAY OF REAL TIMESTAMP (IF NEEDED, OTHERWISE CANCEL)
        holder.iconText.setText(""+formData.getFormClass());

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        // handle icon animation
        applyIconAnimation(holder, position);

        // display form image
        applyProfilePicture(holder);

        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(NewFormListAdapter.MyViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        /*holder.iconImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconImportantClicked(position);
            }
        });*/

        holder.formContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFormRowClicked(position);
            }
        });

        /*holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });*/
    }

    private void applyProfilePicture(NewFormListAdapter.MyViewHolder holder) {
        try {
            Glide.with(mContext).load("https://img.icons8.com/color/48/000000/treatment-plan.png")
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgProfile);
            holder.imgProfile.setColorFilter(null);
            holder.iconText.setVisibility(View.GONE);
        }
        catch (Exception e) {
            Log.i("appMedici", "Problems retrieving image on internet: "+e.getMessage());
            holder.imgProfile.setImageResource(R.drawable.bg_circle);
            holder.imgProfile.setColorFilter(Color.RED);
            holder.iconText.setVisibility(View.VISIBLE);
        }
    }

    private void applyIconAnimation(NewFormListAdapter.MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    @Override
    public long getItemId(int position) {
        return forms.get(position).getFormId();
    }

    /*private void applyImportant(NewFormListAdapter.DrugViewHolder holder, Message message) {
        if (message.isImportant()) {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_selected));
        } else {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_border_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_normal));
        }
    }*/

    /*private void applyReadStatus(NewFormListAdapter.DrugViewHolder holder, Message message) {
        if (message.isRead()) {
            holder.from.setTypeface(null, Typeface.NORMAL);
            holder.subject.setTypeface(null, Typeface.NORMAL);
            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.message));
        } else {
            holder.from.setTypeface(null, Typeface.BOLD);
            holder.subject.setTypeface(null, Typeface.BOLD);
            holder.from.setTextColor(ContextCompat.getColor(mContext, R.color.from));
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
        }
    }*/

    @Override
    public int getItemCount() {
        return forms.size();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        forms.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface FormAdapterListener {
        void onIconClicked(int position);

        //void onIconImportantClicked(int position);

        void onFormRowClicked(int position);

        //void onRowLongClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView iconImp, imgProfile;
        public TextView title, primaryText, secondaryText, iconText, timestamp;
        public LinearLayout formContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.formTitle);
            primaryText = (TextView) view.findViewById(R.id.form_txt_primary);
            secondaryText = (TextView) view.findViewById(R.id.form_txt_secondary);

            formContainer = (LinearLayout) view.findViewById(R.id.form_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.form_icon_container);

            iconBack = (RelativeLayout) view.findViewById(R.id.form_icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.form_icon_front);
            imgProfile = (ImageView) view.findViewById(R.id.form_icon_profile);

            iconText = (TextView) view.findViewById(R.id.form_icon_text);
            timestamp = (TextView) view.findViewById(R.id.form_timestamp);
        }
    }
}
