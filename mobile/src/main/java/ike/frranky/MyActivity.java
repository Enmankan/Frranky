package ike.frranky;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


public class MyActivity extends Activity implements SensorEventListener {

    public static final int MESSAGE_SENSOR_EVENT = 1;

    private boolean mOn = false;
    private Button mOnButton;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;

    private TextView mTextView5;
    private TextView mTextView6;
    private TextView mTextView7;

    private TextView mTextView8;
    private TextView mTextView9;
    private TextView mTextView10;

    private SoundPool mSoundPool;
    private int[] mSoundIds = new int[7];

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            SensorEvent event = (SensorEvent) msg.obj;

            if (Math.abs(event.values[0]) >= mSensitivity) {
                float volume = Math.min(Math.abs(event.values[0]) * mForceCoefficient, 1.0f);
                float playbackRate = 0.5f + Math.min(Math.abs(event.values[0]) * mForceCoefficient, 1.0f) * 1.5f;
                mSoundPool.play(event.values[0] > 0 ? mSoundIds[1] : mSoundIds[2], volume, volume, 1, 0, playbackRate);
            }
            if (Math.abs(event.values[1]) >= mSensitivity) {
                float volume = Math.min(Math.abs(event.values[1]) * mForceCoefficient, 1.0f);
                float playbackRate = 0.5f + Math.min(Math.abs(event.values[1]) * mForceCoefficient, 1.0f) * 1.5f;
                mSoundPool.play(event.values[1] > 0 ? mSoundIds[3] : mSoundIds[4], volume, volume, 1, 0, playbackRate);
            }
            if (Math.abs(event.values[2]) >= mSensitivity) {
                float volume = Math.min(Math.abs(event.values[2]) * mForceCoefficient, 1.0f);
                float playbackRate = 0.5f + Math.min(Math.abs(event.values[2]) * mForceCoefficient, 1.0f) * 1.5f;
                mSoundPool.play(event.values[2] > 0 ? mSoundIds[5] : mSoundIds[6], volume, volume, 1, 0, playbackRate);
            }

            mTextView5.setText(event.values[0] >= mSensitivity ? "RIGHT" : (event.values[0] <= -mSensitivity ? "LEFT" : ""));
            mTextView6.setText(event.values[1] >= mSensitivity ? "FRONT" : (event.values[1] <= -mSensitivity ? "BACK" : ""));
            mTextView7.setText(event.values[2] >= mSensitivity ? "UP" : (event.values[2] <= -mSensitivity ? "DOWN" : ""));

            mTextView8.setText(String.valueOf(event.values[0]));
            mTextView9.setText(String.valueOf(event.values[1]));
            mTextView10.setText(String.valueOf(event.values[2]));

//            float x = Math.min(Math.abs(event.values[0]) * mForceCoefficient, 1.0f) * 360.0f;
//            float y = Math.min(Math.abs(event.values[1]) * mForceCoefficient, 1.0f) * 360.0f;
//            float z = Math.min(Math.abs(event.values[2]) * mForceCoefficient, 1.0f) * 360.0f;
//            float max = Math.max(Math.abs(event.values[0]), 0.0f);
//            max = Math.max(Math.abs(event.values[1]), max);
//            max = Math.max(Math.abs(event.values[2]), max);
//            max = Math.min(max / 16.0f, 1.0f);
//            mRelativeLayout.setBackgroundColor(Color.HSVToColor(new float[] { 0.0f, max, 1.0f }));
        }
    };

    private Handler mServiceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MovingData moving = (MovingData) msg.obj;

            if ((int)moving.x != 0) {
                float volume = Math.min(Math.abs(moving.x) * mForceCoefficient, 1.0f);
                float playbackRate = 0.5f + Math.min(Math.abs(moving.x) * mForceCoefficient, 1.0f) * 1.5f;
                mSoundPool.play(moving.x > 0 ? mSoundIds[1] : mSoundIds[2], volume, volume, 1, 0, playbackRate);
            }
            if ((int)moving.y != 0) {
                float volume = Math.min(Math.abs(moving.y) * mForceCoefficient, 1.0f);
                float playbackRate = 0.5f + Math.min(Math.abs(moving.y) * mForceCoefficient, 1.0f) * 1.5f;
                mSoundPool.play(moving.y > 0 ? mSoundIds[3] : mSoundIds[4], volume, volume, 1, 0, playbackRate);
            }
            if ((int)moving.z != 0) {
                float volume = Math.min(Math.abs(moving.z) * mForceCoefficient, 1.0f);
                float playbackRate = 0.5f + Math.min(Math.abs(moving.z) * mForceCoefficient, 1.0f) * 1.5f;
                mSoundPool.play(moving.z > 0 ? mSoundIds[5] : mSoundIds[6], volume, volume, 1, 0, playbackRate);
            }

            mTextView5.setText((int)moving.x >= 1 ? "RIGHT" : ((int)moving.x <= -1 ? "LEFT" : ""));
            mTextView6.setText((int)moving.y >= 1 ? "FRONT" : ((int)moving.y <= -1 ? "BACK" : ""));
            mTextView7.setText((int)moving.z >= 1 ? "UP" : ((int)moving.z <= -1 ? "DOWN" : ""));

            mTextView8.setText(String.valueOf(moving.x));
            mTextView9.setText(String.valueOf(moving.y));
            mTextView10.setText(String.valueOf(moving.z));
        }
    };
    private TextView mTextViewSensitivity;
    private TextView mTextViewForceCoefficient;
    private float mSensitivity = 0.6f;
    private float mForceCoefficient = 0.25f;
    private SeekBar mSeekBarSensitivity;
    private RelativeLayout mRelativeLayout;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SeekBar mSeekBarForceCoefficient;

    public static class MovingData {
        public float x;
        public float y;
        public float z;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mSoundIds[0] = mSoundPool.load(this, R.raw.sfx01, 1);
        mSoundIds[1] = mSoundPool.load(this, R.raw.sfx02, 1);
        mSoundIds[2] = mSoundPool.load(this, R.raw.sfx03, 1);
        mSoundIds[3] = mSoundPool.load(this, R.raw.sfx04, 1);
        mSoundIds[4] = mSoundPool.load(this, R.raw.sfx05, 1);
        mSoundIds[5] = mSoundPool.load(this, R.raw.sfx06, 1);
        mSoundIds[6] = mSoundPool.load(this, R.raw.sfx07, 1);

        Intent startServiceIntent = new Intent(this, MyService.class);
        startServiceIntent.putExtra("MESSENGER", new Messenger(mServiceHandler));
        startService(startServiceIntent);

        mTextView2 = (TextView) findViewById(R.id.textView2);
        mTextView3 = (TextView) findViewById(R.id.textView3);
        mTextView4 = (TextView) findViewById(R.id.textView4);

        mTextView5 = (TextView) findViewById(R.id.textView5);
        mTextView6 = (TextView) findViewById(R.id.textView6);
        mTextView7 = (TextView) findViewById(R.id.textView7);

        mTextView8 = (TextView) findViewById(R.id.textView8);
        mTextView9 = (TextView) findViewById(R.id.textView9);
        mTextView10 = (TextView) findViewById(R.id.textView10);

        mTextViewSensitivity = (TextView) findViewById(R.id.textViewSensitivity);
        mTextViewForceCoefficient = (TextView) findViewById(R.id.textViewForceCoefficient);

        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        mSeekBarSensitivity = (SeekBar) findViewById(R.id.seekBar);
        mSeekBarSensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSensitivity = 0.2f + 0.1f * progress;
                mTextViewSensitivity.setText("Sensitivity: " + mSensitivity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarForceCoefficient = (SeekBar) findViewById(R.id.seekBarForceCoefficient);
        mSeekBarForceCoefficient.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mForceCoefficient = 2.0f / (progress + 2.0f);
                float roundedCoeff = Math.round(mForceCoefficient * 10000.0f) / 10000.0f;
                mTextViewForceCoefficient.setText("Force Coefficient: " + roundedCoeff);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mOnButton = (Button) findViewById(R.id.button);

        mOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                if (mOn) {
                    mOn = false;
                    button.setText("Kakatte koi!");

                    mSensorManager.unregisterListener(MyActivity.this);
                } else {
                    mOn = true;
                    button.setText("Urusai!");

                    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                    if (mSensor != null) {
                        Log.d("TAG", "Sensor available");

                        mSensorManager.registerListener(MyActivity.this, mSensor, SensorManager.SENSOR_DELAY_GAME);
                    }
                    else {
                        // Failure! No magnetometer.
                        Log.d("TAG", "Sensor not available!");
                    }
                }
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        mHandler.sendMessage(mHandler.obtainMessage(MyActivity.MESSAGE_SENSOR_EVENT, event));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mOn && mSensorManager != null) {
            mSensorManager.registerListener(MyActivity.this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
