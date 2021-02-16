package com.ryan.luckywheel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.core.content.ContextCompat;

import com.ryan.luckywheel.confetti.ConfettiManager;
import com.ryan.luckywheel.confetti.ConfettiSource;
import com.ryan.luckywheel.confetti.ConfettoGenerator;
import com.ryan.luckywheel.confetti.confetto.BitmapConfetto;
import com.ryan.luckywheel.confetti.confetto.Confetto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class MainActivity extends Activity implements ConfettoGenerator {
    // 一回合 多少 秒
    private static final int SECOND_OF_ROUND = 20;
    // 幸運輪盤有幾格
    private static final int GRID_OF_LUCKY_WHEEL = 7;
    private final List<ConfettiManager> activeConfettiManagers = new ArrayList<>();
    List<LuckyItem> data = new ArrayList<>();
    float preAngle = 0;
    ImageView cursorView;
    ObjectAnimator animCursor;
    ValueAnimator animCursorSlowDown;
    private MediaPlayer mpAnswerSound;
    private int size;
    private int velocitySlow, velocityNormal;
    private Bitmap bitmap;
    private ViewGroup container;
    private SeekBar seekBarId;
    private boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setParticleEffect();
        cursorView = findViewById(R.id.cursorView);
        VideoView simpleVideoView = (VideoView) findViewById(R.id.videoView); // initiate a video view
        simpleVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.spinwheel));
        simpleVideoView.start();
        simpleVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        seekBarId = findViewById(R.id.seekBarId);
        seekBarId.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() < 100) {
                    seekBar.setProgress(0);
                } else {
                    seekBar.setVisibility(View.INVISIBLE);
                }

            }
        });
        final LuckyWheelView luckyWheelView = (LuckyWheelView) findViewById(R.id.luckyWheel);
        createGiftData();
        luckyWheelView.setData(data);
        luckyWheelView.setRound(SECOND_OF_ROUND);

        luckyWheelView.setPieBackgroundImage(R.mipmap.spin);
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = getRandomIndex();
                luckyWheelView.startLuckyWheelWithTargetIndex(index);

            }
        });

        luckyWheelView.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                activeConfettiManagers.add(getConfettiManager().setNumInitialCount(0)
                        .setEmissionDuration(2000)
                        .setEmissionRate(20)
                        .animate());
                Toast.makeText(getApplicationContext(), data.get(index).topText, Toast.LENGTH_SHORT).show();
                animateBack(luckyWheelView);

            }
        });
    }

    private void createGiftData() {
        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.topText = "Watch";
        luckyItem1.icon = R.mipmap.gift_watch;
        luckyItem1.color = ContextCompat.getColor(this, R.color.colorGreen);
        luckyItem1.secondaryColor = ContextCompat.getColor(this, R.color.colorGreenSecondary);
        data.add(luckyItem1);

        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.topText = "Discount 10%";
        luckyItem4.icon = R.mipmap.gift_discount_10;
        luckyItem4.color = ContextCompat.getColor(this, R.color.colorOrange);
        luckyItem4.secondaryColor = ContextCompat.getColor(this, R.color.colorOrangeSecondary);
        data.add(luckyItem4);


        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.topText = "Toy Bus";
        luckyItem3.icon = R.mipmap.gift_bus;
        luckyItem3.color = ContextCompat.getColor(this, R.color.colorSky);
        luckyItem3.secondaryColor = ContextCompat.getColor(this, R.color.colorSkySecondary);
        data.add(luckyItem3);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.topText = "Discount 20%";
        luckyItem2.icon = R.mipmap.gift_discount_20;
        luckyItem2.color = ContextCompat.getColor(this, R.color.colorAccent);
        luckyItem2.secondaryColor = ContextCompat.getColor(this, R.color.colorAccentSecondary);
        data.add(luckyItem2);


        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.topText = "Pencil Box";
        luckyItem5.icon = R.mipmap.gift_box;
        luckyItem5.color = ContextCompat.getColor(this, R.color.colorYellow);
        luckyItem5.secondaryColor = ContextCompat.getColor(this, R.color.colorYellowSecondary);
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.topText = "School Bag";
        luckyItem6.icon = R.mipmap.gift_bag;
        luckyItem6.secondaryColor = ContextCompat.getColor(this, R.color.colorYellow2Secondary);
        luckyItem6.color = ContextCompat.getColor(this, R.color.colorYellow2);
        data.add(luckyItem6);

        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.topText = "Better Luck Next Time";
        luckyItem7.icon = R.mipmap.better_luck;
        luckyItem7.color = ContextCompat.getColor(this, R.color.colorOrange);
        luckyItem7.secondaryColor = ContextCompat.getColor(this, R.color.colorOrangeSecondary);
        //data.add(luckyItem7);


    }

    private void setParticleEffect() {
        container = findViewById(R.id.activity_main);
        final Resources res = getResources();
        size = res.getDimensionPixelSize(R.dimen.big_confetti_size);
        velocitySlow = res.getDimensionPixelOffset(R.dimen.default_velocity_slow);
        velocityNormal = res.getDimensionPixelOffset(R.dimen.default_velocity_fast);

        bitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, R.drawable.snowflake),
                size,
                size,
                false
        );
    }

    private void animateBack(LuckyWheelView luckyWheelView) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(luckyWheelView, "rotationY", 90f, 0f);
        animation.setDuration(1000);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
    }

    private int getRandomIndex() {
        Random rand = new Random();
        return rand.nextInt(data.size() - 2) + 0;
    }

    private int getRandomRound() {
        Random rand = new Random();
        return rand.nextInt(10) + 15;
    }

    private void playAnswerSound() {
        mpAnswerSound = MediaPlayer.create(this, R.raw.airship);
        mpAnswerSound.start();

    }

    private ConfettiManager getConfettiManager() {
        final ConfettiSource source = new ConfettiSource(0, -size, container.getWidth(), -size);
        return new ConfettiManager(this, this, source, container)
                .setVelocityX(0, velocitySlow)
                .setVelocityY(velocityNormal, velocitySlow)
                .setRotationalVelocity(180, 90)
                .setTouchEnabled(true);
    }

    @Override
    public Confetto generateConfetto(Random random) {
        return new BitmapConfetto(bitmap);
    }

    private float getAngleOfIndexTarget(int index) {
        return (360f / GRID_OF_LUCKY_WHEEL) * index;
    }
}
