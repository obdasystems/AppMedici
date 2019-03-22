package com.obdasystems.pocmedici.adapter;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.listener.OnFormQuestionRecyclerViewItemClickListener;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestionAnswered;
import com.obdasystems.pocmedici.persistence.entities.JoinFormPageQuestionsWithPossibleAnswerData;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFillingProcessAnsweredQuestionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormQuestionListAdapter extends RecyclerView.Adapter<FormQuestionListAdapter.FormQuestionViewHolder> {

    private final LayoutInflater inflater;
    private List<CtcaeFormQuestion> questions;
    private List<JoinFormPageQuestionsWithPossibleAnswerData> questionsWithAnswers;
    private List<CtcaeFormQuestionAnswered> answeredQuestions;
    private Context ctx;
    private Application application;
    private int fillingProcessId;
    private int formId;


    public FormQuestionListAdapter(Application app, Context context, int fillingProcessId, int formId) {
        inflater = LayoutInflater.from(context);
        this.ctx = context;
        this.fillingProcessId = fillingProcessId;
        this.application = app;
        this.formId = formId;
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
            holder.possAnswRadioGroupView.removeAllViews();

            CtcaeFormQuestion currQuestion = questions.get(position);
            int currQuestionId = currQuestion.getId();
            holder.formQuestionIdView.setText("QuestionId: "+currQuestionId);
            holder.formQuestionTextView.setText(currQuestion.getText());

            holder.pageId = currQuestion.getPageId();
            holder.questionId = currQuestionId;

            RadioGroup.LayoutParams rprms;
            int baseRbId = (int) System.currentTimeMillis(); //(position+1)*100;
            int counter = 0;
            int checkedRbId = -1;
            boolean foundChecked = false;
            Log.i("appMedici","["+this.getClass()+"]found "+answeredQuestions.size()+" " +
                    " answered questions");
            for(JoinFormPageQuestionsWithPossibleAnswerData join:questionsWithAnswers) {
                if(join.getQuestionId()==currQuestionId) {
                    RadioButton rb = new RadioButton(FormQuestionListAdapter.this.ctx);
                    int rbId = baseRbId + counter++;
                    rb.setId(rbId);
                    rb.setText(join.getPossibleAnswerText());
                    holder.possAnswRadioGroupView.addView(rb);
                    holder.buttonLabelToAnswerIdMap.put(join.getPossibleAnswerText(), join.getPossibleAnswerId());
                    for(CtcaeFormQuestionAnswered answered:answeredQuestions) {
                        if(answered.getQuestionId()==join.getQuestionId() && answered.getAnswerId()==join.getPossibleAnswerId()) {
                            checkedRbId = rbId;
                            foundChecked = true;
                            holder.previouslyCheckedRbId = rbId;
                            //rb.setChecked(true);
                            break;
                        }
                    }
                }
            }
            if(foundChecked) {
                holder.possAnswRadioGroupView.check(checkedRbId);
            }
        }
        else {
            //TODO manage emptiness
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


    public void setAlreadyAnsweredQuestions(List<CtcaeFormQuestionAnswered> answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }

    //TODO MODIFICA
    class FormQuestionViewHolder extends RecyclerView.ViewHolder {
        private final CardView formCardView;
        private final TextView formQuestionIdView;
        private final TextView formQuestionTextView;
        private final RadioGroup possAnswRadioGroupView;

        private Map<String, Integer> buttonLabelToAnswerIdMap;
        private int pageId;
        private int questionId;
        private int previouslyCheckedRbId = -1;

        private FormQuestionViewHolder(View itemView) {
            super(itemView);
            buttonLabelToAnswerIdMap = new HashMap<>();
            formCardView = itemView.findViewById(R.id.formCardView);
            formQuestionIdView = itemView.findViewById(R.id.formQuestionIdTextView);
            formQuestionTextView = itemView.findViewById(R.id.formQuestionTextView);
            possAnswRadioGroupView = itemView.findViewById(R.id.answersRadioGroup);
            possAnswRadioGroupView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId != previouslyCheckedRbId) {
                        RadioButton checked = itemView.findViewById(checkedId);
                        String checkedText = (String) checked.getText();
                        if (!buttonLabelToAnswerIdMap.isEmpty()) {
                            if (buttonLabelToAnswerIdMap.keySet().contains(checkedText)) {
                                int currAnswId = buttonLabelToAnswerIdMap.get(checkedText);
                                QueryAsyncTask task = new QueryAsyncTask(fillingProcessId, formId, pageId, questionId, currAnswId, ctx, application);
                                task.execute();
                                String msg = "You've just selected answer with id= " + currAnswId;
                                //Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ctx, "You've just selected " + checkedText, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ctx, "You've just selected " + checkedText, Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });
        }

    }

    private static class QueryAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progDial;
        private CtcaeFillingProcessAnsweredQuestionRepository repository;
        private int pageId;
        private int questionId;
        private int answerId;
        private Context ctx;
        private Application app;
        private int fillingProcessId;
        private int formId;

        QueryAsyncTask(int fillProcId, int formId, int pageId, int questionId, int answerId, Context context, Application app) {
            this.fillingProcessId = fillProcId;
            this.formId = formId;
            this.pageId = pageId;
            this.questionId = questionId;
            this.answerId = answerId;
            this.ctx = context;
            progDial = new ProgressDialog(ctx);
            this.app = app;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDial.setMessage("Saving answer...");
            progDial.setIndeterminate(false);
            progDial.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDial.setCancelable(false);
            progDial.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            repository = new CtcaeFillingProcessAnsweredQuestionRepository(app, fillingProcessId, formId);
            repository.insertAnsweredQuestion(pageId, questionId, answerId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progDial.dismiss();
        }
    }


}

