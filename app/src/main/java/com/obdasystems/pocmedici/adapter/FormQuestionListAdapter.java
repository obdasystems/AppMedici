package com.obdasystems.pocmedici.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.listener.OnFormQuestionRecyclerViewItemClickListener;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.JoinFormPageQuestionsWithPossibleAnswerData;

import java.util.List;

public class FormQuestionListAdapter extends RecyclerView.Adapter<FormQuestionListAdapter.FormQuestionViewHolder> {

    private final LayoutInflater inflater;
    private List<CtcaeFormQuestion> questions;
    private List<JoinFormPageQuestionsWithPossibleAnswerData> questionsWithAnswers;
    private Context ctx;

    public FormQuestionListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.ctx = context;
    }

    @NonNull
    @Override
    public FormQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = inflater.inflate(R.layout.formpage_recyclerview_row_layout, parent, false);
        return new FormQuestionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FormQuestionListAdapter.FormQuestionViewHolder holder, int position) {
        if(questions!=null) {
            for(int i=0;i<holder.possAnswRadioGroupView.getChildCount();i++){
                holder.possAnswRadioGroupView.removeViewAt(i);
            }
            holder.possAnswRadioGroupView.removeAllViews();

            CtcaeFormQuestion currQuestion = questions.get(position);
            int currQuestionId = currQuestion.getId();
            holder.formQuestionIdView.setText("QuestionId: "+currQuestionId);
            holder.formQuestionTextView.setText(currQuestion.getText());
            RadioGroup.LayoutParams rprms;
            int id = (int) System.currentTimeMillis(); //(position+1)*100;
            for(JoinFormPageQuestionsWithPossibleAnswerData join:questionsWithAnswers) {
                if(join.getQuestionId()==currQuestionId) {
                    RadioButton rb = new RadioButton(FormQuestionListAdapter.this.ctx);
                    rb.setId(join.getPossibleAnswerId() + join.getQuestionId() + join.getPageId() + join.getFormId());
                    rb.setText(join.getPossibleAnswerText());
                    holder.possAnswRadioGroupView.addView(rb);
                }
            }

            /*holder.formImageView.setImageResource(R.drawable.ic_description_black_24dp);
            holder.formTitleView.setText(current.getPageTitle() + "[nr.="+current.getPageNumber()+"]");
            holder.formDescriptionView.setText(current.getPageInstructions());*/

            //holder.formTitleItemView.setText(current.getId() + " " + current.getFormClass() + " " + current.getFormPeriodicity());
        }
        else {
            /*holder.formImageView.setImageResource(R.drawable.ic_add_alarm_black_24dp);
            holder.formTitleView.setText("NO QUESTIONS AVAILABLE!!");*/
            //holder.formTitleItemView.setText("No form found!!");
        }
    }

    @Override
    public int getItemCount() {
        if(questions != null) {
            return questions.size();
        }
        return 1;
    }


    //CUSTOM METHODS
    public void setQuestions(List<CtcaeFormQuestion> questions) {
        this.questions = questions;
    }

    public void setQuestionsWithAnswers(List<JoinFormPageQuestionsWithPossibleAnswerData> questionsWithAnswers) {
        this.questionsWithAnswers = questionsWithAnswers;
    }



    /*public JoinFormPageQuestionsWithPossibleAnswerData getFormPageAtPosition(int position) {
        if(this.questions!=null) {
            return this.questions.get(position);
        }
        return null;
    }*/

    /*public void setOnItemClickListener(final OnFormQuestionRecyclerViewItemClickListener listener) {
        this.mClickListener = listener;
    }*/

    //TODO MODIFICA
    class FormQuestionViewHolder extends RecyclerView.ViewHolder {
        //private final TextView formTitleItemView;
        private final CardView formCardView;
        private final TextView formQuestionIdView;
        private final TextView formQuestionTextView;
        private final RadioGroup possAnswRadioGroupView;

        private FormQuestionViewHolder(View itemView) {
            super(itemView);
            formCardView = itemView.findViewById(R.id.formCardView);
            formQuestionIdView = itemView.findViewById(R.id.formQuestionIdTextView);
            formQuestionTextView = itemView.findViewById(R.id.formQuestionTextView);
            possAnswRadioGroupView = itemView.findViewById(R.id.answersRadioGroup);
        }

    }
}

