package com.example.mcqapp.views;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mcqapp.Model.QuestionModel;
import com.example.mcqapp.R;
import com.example.mcqapp.viewmodel.QuestionViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class QuizFragment extends Fragment implements View.OnClickListener {

    private QuestionViewModel viewModel;
    private NavController navController;
    private ProgressBar progressBar;
    private Button option1Btn, option2Btn, option3Btn, nextQueBtn;
    private TextView questionTV1, ansFeedBackTv, questionNumberTv, timerCountTv;

    WebView questionTv;
    private ImageView closeQuizBtn;
    private String quizId;
    private long totalQuestions;
    private int currentQueNo = 0;
    private boolean canAnswer = false;
    private long timer;
    private CountDownTimer countDownTimer;
    private int notAnswered = 0;
    private int correctAnswer = 0;
    private int wrongAnswer = 0;
    private String answer = "";

    private FirebaseFirestore firestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(QuestionViewModel.class);
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        closeQuizBtn = view.findViewById(R.id.imageView3);
        option1Btn = view.findViewById(R.id.option1Btn);
        option2Btn = view.findViewById(R.id.option2Btn);
        option3Btn = view.findViewById(R.id.option3Btn);
        nextQueBtn = view.findViewById(R.id.nextQueBtn);
        ansFeedBackTv = view.findViewById(R.id.ansFeedbackTv);
        questionTv = view.findViewById(R.id.quizQuestionTv);
        timerCountTv = view.findViewById(R.id.countTimeQuiz);
        questionNumberTv = view.findViewById(R.id.quizQuestionsCount);
        progressBar = view.findViewById(R.id.quizCoutProgressBar);

        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
        totalQuestions = QuizFragmentArgs.fromBundle(getArguments()).getTotalQueCount();
        viewModel.setQuizId(quizId);
        viewModel.getQuestions();

        option1Btn.setOnClickListener(this);
        option2Btn.setOnClickListener(this);
        option3Btn.setOnClickListener(this);
        nextQueBtn.setOnClickListener(this);

        WebSettings webSettings = questionTv.getSettings();
        webSettings.setJavaScriptEnabled(true);

        closeQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_quizragment_to_listFragment);
            }
        });

        loadData();
    }

    private void loadData() {
        enableOptions();
        loadQuestions(1);
    }

    private void enableOptions() {
        option1Btn.setVisibility(View.VISIBLE);
        option2Btn.setVisibility(View.VISIBLE);
        option3Btn.setVisibility(View.VISIBLE);

        option1Btn.setEnabled(true);
        option2Btn.setEnabled(true);
        option3Btn.setEnabled(true);

        ansFeedBackTv.setVisibility(View.INVISIBLE);
        nextQueBtn.setVisibility(View.INVISIBLE);
    }

    private void loadQuestions(int i) {
        currentQueNo = i;
        viewModel.getQuestionMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<QuestionModel>>() {
            @Override
            public void onChanged(List<QuestionModel> questionModels) {
                if (questionModels != null && questionModels.size() > 0) {
                    String latexEquation = questionModels.get(i - 1).getQuestion();

                  /*String htmlContent = "<html><head>" +
                            "<style type='text/css'>" +
                            "body { font-family: 'Times New Roman', Times, serif; font-size: 16px; }" +
                            "</style>" +
                            "<script type='text/javascript' async " +
                            "src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML'>" +
                            "</script></head><body>" +
                            "<p> \\( " + latexEquation + " \\)</p>" +
                            "</body></html>";*/


                    String htmlContent = "<html><head>" +
                            "<script type='text/javascript' async " +
                            "src='https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML'>" +
                            "</script></head><body>" +
                            "<p>\\(" + latexEquation + "\\)</p>" +
                            "</body></html>";


                    questionTv.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

                    option1Btn.setText(questionModels.get(i - 1).getOption_a());
                    option2Btn.setText(questionModels.get(i - 1).getOption_b());
                    option3Btn.setText(questionModels.get(i - 1).getOption_c());
                    timer = questionModels.get(i - 1).getTimer();

                    questionNumberTv.setText(String.valueOf(currentQueNo));
                    startTimer();
                    answer = questionModels.get(i - 1).getAnswer();
                }
            }
        });
        canAnswer = true;
    }

    private void startTimer() {
        timerCountTv.setText(String.valueOf(timer));
        progressBar.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(timer * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerCountTv.setText(String.valueOf(millisUntilFinished / 1000));
                progressBar.setProgress((int) ((millisUntilFinished / 1000) * 100) / (int) timer);
            }

            @Override
            public void onFinish() {
                canAnswer = false;
                ansFeedBackTv.setText("Times Up !! No answer selected");
                notAnswered++;
                showNextBtn();
            }
        }.start();
    }

    private void showNextBtn() {
        if (currentQueNo == totalQuestions) {
            nextQueBtn.setText("Submit");
            nextQueBtn.setEnabled(true);
            nextQueBtn.setVisibility(View.VISIBLE);
        } else {
            nextQueBtn.setVisibility(View.VISIBLE);
            nextQueBtn.setEnabled(true);
            ansFeedBackTv.setVisibility(View.VISIBLE);
        }
    }

    private void resetOptions() {
        ansFeedBackTv.setVisibility(View.INVISIBLE);
        nextQueBtn.setVisibility(View.INVISIBLE);
        nextQueBtn.setEnabled(false);
        option1Btn.setBackground(ContextCompat.getDrawable(requireContext(), R.color.light_sky));
        option2Btn.setBackground(ContextCompat.getDrawable(requireContext(), R.color.light_sky));
        option3Btn.setBackground(ContextCompat.getDrawable(requireContext(), R.color.light_sky));
    }

    private void submitResults() {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("correct", correctAnswer);
        resultMap.put("wrong", wrongAnswer);
        resultMap.put("notAnswered", notAnswered);

        viewModel.addResults(resultMap);

        QuizFragmentDirections.ActionQuizragmentToResultFragment action =
                QuizFragmentDirections.actionQuizragmentToResultFragment();
        action.setQuizId(quizId);
        navController.navigate(action);
    }

    private void verifyAnswer(Button button) {
        if (canAnswer) {
            if (answer.equals(button.getText())) {
                button.setBackground(ContextCompat.getDrawable(requireContext(), R.color.green));
                correctAnswer++;
                ansFeedBackTv.setText("Correct Answer");
            } else {
                button.setBackground(ContextCompat.getDrawable(requireContext(), R.color.red));
                wrongAnswer++;
                ansFeedBackTv.setText("Wrong Answer \nCorrect Answer :" + answer);
            }
        }
        canAnswer = false;
        countDownTimer.cancel();
        showNextBtn();
    }

    @Override
    public void onClick(View v) {
        option1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAnswer(option1Btn);
            }
        });

        option2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAnswer(option2Btn);
            }
        });

        option3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAnswer(option3Btn);
            }
        });

        nextQueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQueNo == totalQuestions) {
                    submitResults();
                } else {
                    currentQueNo++;
                    loadQuestions(currentQueNo);
                    resetOptions();
                }
            }
        });
        }

}
