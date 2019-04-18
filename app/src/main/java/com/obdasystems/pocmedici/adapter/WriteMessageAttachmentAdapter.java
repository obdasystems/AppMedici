package com.obdasystems.pocmedici.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.listener.OnRecyclerViewPositionClickListener;

import java.lang.ref.WeakReference;
import java.util.List;

public class WriteMessageAttachmentAdapter
        extends RecyclerView.Adapter<WriteMessageAttachmentAdapter.AttachmentViewHolder>{
    private List<String> attachments;
    private final LayoutInflater inflater;
    private OnRecyclerViewPositionClickListener deleteListener;

    public WriteMessageAttachmentAdapter(Context context,
                                         OnRecyclerViewPositionClickListener listener) {
        inflater = LayoutInflater.from(context);
        deleteListener = listener;
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = inflater.inflate(R.layout.attachment_list_row, parent, false);
        return new WriteMessageAttachmentAdapter.AttachmentViewHolder(itemView,deleteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        if(attachments!=null && attachments.size()>0) {
            String currAttach = attachments.get(position);
            holder.attachmentUriTextView.setText(currAttach);
            /*holder.deleteAttachmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
        }
        else {
            // TODO Manage emptiness
        }
    }

    @Override
    public int getItemCount() {
        if(attachments != null) {
            return attachments.size();
        }
        return 0;
    }

    public void setAttachments(List<String> attachmentNames) {
        this.attachments = attachmentNames;
    }

    public class AttachmentViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView attachmentUriTextView;
        private final ImageButton deleteAttachmentButton;
        private final WeakReference<OnRecyclerViewPositionClickListener> delListenerRef;

        private AttachmentViewHolder(View itemView,
                                     OnRecyclerViewPositionClickListener listener) {
            super(itemView);
            delListenerRef = new WeakReference<OnRecyclerViewPositionClickListener>(listener);
            attachmentUriTextView = itemView.findViewById(R.id.attachmentUriTextView);
            deleteAttachmentButton = itemView.findViewById(R.id.deleteAttachmentButton);
            deleteAttachmentButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            delListenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

}
