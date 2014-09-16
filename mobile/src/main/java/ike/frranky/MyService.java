package ike.frranky;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;

/**
 * Created by ike on 13/09/14.
 */
public class MyService extends WearableListenerService {
    private static final String TAG = MyService.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private Messenger mMessageHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            mMessageHandler = (Messenger) intent.getExtras().get("MESSENGER");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (START_ACTIVITY_PATH.equals(messageEvent.getPath())) {
            ByteBuffer buffer = ByteBuffer.allocate(12);
            buffer.put(messageEvent.getData());
            buffer.rewind();

            MyActivity.MovingData movingData = new MyActivity.MovingData();
            movingData.x = buffer.getFloat();
            movingData.y = buffer.getFloat();
            movingData.z = buffer.getFloat();

            Message message = Message.obtain();
            message.obj = movingData;
            try {
                mMessageHandler.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
//            Log.v(TAG, "msg rcvd: " + Arrays.toString(moving));
//            Log.v(TAG, messageEvent.getPath());

        }
    }
    String START_ACTIVITY_PATH = "start";
}
