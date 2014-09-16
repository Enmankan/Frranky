package ike.frranky;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class WearActivity extends Activity implements SensorEventListener {

    private static final String TAG = WearActivity.class.getSimpleName();
    private static final float THRESHOLD = 0.5f;

    private TextView mTextView;
    private SensorManager manager;
    private GoogleApiClient mGoogleApiClient;
    private float[] mMoving = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Wearable.API)
                .build();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // Listenerの登録
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_LINEAR_ACCELERATION);
        if (sensors.size() > 0) {
            Sensor s = sensors.get(0);
            manager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Listenerの登録解除
        manager.unregisterListener(this);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            for (int i = 0; i < 3; ++i) {
//                mMoving[i] = Math.abs(event.values[i]) > THRESHOLD ? (event.values[i] > 0.0f ? 1 : -1) : 0;
                mMoving[i] = Math.abs(event.values[i]) > THRESHOLD ? event.values[i] : 0;
            }

            sendStartMessage(mMoving.clone());
        }
    }

    private void sendStartMessage(final float[] values) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                sendMessageToStartActivity(values);
            }
        });
        t.start();
    }

    String START_ACTIVITY_PATH = "start";

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();   //…… 1
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    private void sendMessageToStartActivity(float[] values) {
        Collection<String> nodes = getNodes();
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);
        for (int i = 0; i < values.length; ++i)
            buffer.putFloat(values[i]);
        for (String node : nodes) {
            MessageApi.SendMessageResult result =
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node, START_ACTIVITY_PATH, buffer.array()).await();   //…… 2
            if (!result.getStatus().isSuccess()) {
                Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
            }
        }
    }
}