package com.trivia.general_knowledge;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;
import com.trivia.general_knowledge.data.Repository;
import com.trivia.general_knowledge.databinding.ActivityMainBinding;
import com.trivia.general_knowledge.model.QuestionModel;
import com.trivia.general_knowledge.score.SaveGameState;
import com.trivia.general_knowledge.score.Score;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Using DataBinding.
    private ActivityMainBinding binding;
    List<QuestionModel> questions;
    private int current_question = 1;
    private int score = 0;
    private Score current_score;
    private int high_score = 0;
    private SaveGameState saveGameState;
    private int avoidDoubleAndSkip = 0;
    private AdView mAdView;
    private int maintainAds = 1;
    private AdRequest adRequest;
    private InterstitialAd mInterstitialAd;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initializing data binding.
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        current_score = new Score();
        saveGameState = new SaveGameState(this);

        //Retrieving and setting saved data...
       score = saveGameState.getCurrentScore();
       current_question = saveGameState.getCurrentQuestion();
       high_score = saveGameState.getHighScore();
       current_score.setScore(score);
       avoidDoubleAndSkip = saveGameState.getAvoidDoubleAndSkip();
       adRequest = new AdRequest.Builder().build();


       //Admob ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                //Calling Ads Method
                createPersonalizeAds();
            }
        });
        //Initializing Banner Ads
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

       //Calling Questions via Repository and QuestionProcessingPart Interface.
        questions = new Repository().getQuestion(questionModels -> {
            binding.questionText.setText(questions.get(current_question).getQuestion());
            binding.currentScore.setText("Current Score\n"+current_score.getScore());
            binding.highScore.setText("High Score : "+high_score);
            updateQuestion();

            });

        //Setting true button and providing user's answer.
        binding.trueButton.setOnClickListener(view -> {
            checkAnswer(true);

        });
        //Setting false button and providing user's answer.
        binding.falseButton.setOnClickListener(view -> {
            checkAnswer(false);
        });

        //Setting next button.
        binding.nextButton.setOnClickListener(view -> {
            if(maintainAds<10){
                maintainAds++;
            }else {
                maintainAds =1;
            }
            setAds();
            if(avoidDoubleAndSkip == 1){
                        avoidDoubleAndSkip--;
                        current_question = (current_question+1) % questions.size();
                        updateQuestion();


            }else {
                avoidDoubleAndSkip = 0;
                Snackbar.make(binding.cardView,"Please Select Your Answer",Snackbar.LENGTH_SHORT).show();
            }
            Log.d("TAG", "onCreate: "+maintainAds);

        });

        }


        //Checking user's answer.
    @SuppressLint("SetTextI18n")
    private void checkAnswer(boolean userChoice) {
        //This condition is to avoid choose answer multiple times.
        if(avoidDoubleAndSkip == 0){
            avoidDoubleAndSkip ++;
            boolean correctAnswer = questions.get(current_question).isAnswer();
            int snackMessage = 0;
            if(userChoice==correctAnswer){
                snackMessage = R.string.correct_answer;
                addScore();
                correctAnswerAnimation();
                blinkAnimation();
            }else {
                snackMessage = R.string.wrong_answer;
                deductScore();
                wrongAnswerAnimation();
                shakeAnimation();
            }
           saveGameState.saveHighScore(current_score.getScore());
            updateQuestion();
            getHighScore();
            binding.currentScore.setText("Current Score\n"+current_score.getScore());
            Snackbar.make(binding.cardView,snackMessage,Snackbar.LENGTH_SHORT).show();
        }else {
            avoidDoubleAndSkip =1;
            Snackbar.make(binding.cardView,"Please Select Next Question",Snackbar.LENGTH_SHORT).show();
        }

    }

    //Updating Question and Question number.
    @SuppressLint("SetTextI18n")
    private void updateQuestion(){
        binding.questionText.setText(questions.get(current_question).getQuestion());
        binding.questionOutOf.setText("Question : "+current_question+" / "+questions.size());
    }


    //This animation for cardView and Question text for wrong answer.
    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake_animation);
        binding.cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionText.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionText.setTextColor(Color.WHITE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //This animation for cardView and Question text for correct answer.
    private void blinkAnimation(){
        Animation blink = AnimationUtils.loadAnimation(this,R.anim.blink_animation);
        binding.cardView.setAnimation(blink);
        blink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionText.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionText.setTextColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    //This animation is for current score for wrong answer
    private void wrongAnswerAnimation() {
        Animation wrongAnimation = AnimationUtils.loadAnimation(this,R.anim.wrong_answer);
        binding.currentScore.setAnimation(wrongAnimation);
        wrongAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.currentScore.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.currentScore.setTextColor(getResources().getColor(R.color.score_color));

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    //This animation is for current score for correct answer
    private void correctAnswerAnimation() {
        Animation correctAnimation = AnimationUtils.loadAnimation(this,R.anim.wrong_answer);
        binding.currentScore.setAnimation(correctAnimation);
        correctAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.currentScore.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.currentScore.setTextColor(getResources().getColor(R.color.score_color));

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }



    //Setting plus score for the correct answer.
    private void addScore(){
        score +=100;
        current_score.setScore(score);
    }
    //Setting deduction for wrong answer.
    private void deductScore(){
        if(score>0){
            score -=50;
        }else{
            score = 0;
        }
        current_score.setScore(score);
    }

    //Setting the high score.
    private void getHighScore(){
        if(current_score.getScore()>high_score){
            high_score = current_score.getScore();
            binding.highScore.setText(String.format("High Score : %s", current_score));
        }else{
            binding.highScore.setText(String.format("High Score : %d", high_score));
        }
    }

    //saving game data..
    private void saveGame(){
        saveGameState.saveCurrentScore(current_score.getScore());
        saveGameState.saveCurrentQuestion(current_question);
        saveGameState.saveHighScore(current_score.getScore());
        saveGameState.saveAvoidDoubleAndSkip(avoidDoubleAndSkip);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //calling save game data in onPause to save game status when app is paused,
        saveGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //calling save game data in onPause to save game status when app is destroyed.,
        saveGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        createPersonalizeAds();
    }

    private void setAds(){
        if(maintainAds ==10){
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
            } else {
                Log.d("TAG", "Ads Not available: ");
            }
        }
    }

    private void createPersonalizeAds() {
        loadTheAds(adRequest);
    }

    private void loadTheAds(AdRequest adRequest) {
        InterstitialAd.load(this,(getString(R.string.interstitial_id_real)), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("AdMob...", "onAdLoaded");


                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("AdMob...", "The ad was dismissed.");

                                //Calling intent method after ads.
                                updateQuestion();



                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("AdMob...", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("AdMob...", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("AdMob...", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }
}