package be.kuleuven.msec.iot.heartrate;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MessageService extends WearableListenerService {
    private String TAG="MessageService";
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message Received");

        final String message = new String(messageEvent.getData());
        final String path =  messageEvent.getPath();

        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("path", path);
        messageIntent.putExtra("message", message);

        //Broadcast the received Data Layer messages locally//
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);


        /*switch(messageEvent.getPath()) {
            case Constants.MONITOR_HEARTRATE:

                Intent messageIntent = new Intent();
                messageIntent.setAction(Intent.ACTION_SEND);
                messageIntent.putExtra("message", message);

                //Broadcast the received Data Layer messages locally//
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
                break;
            case Constants.REACHABLE:

        }

        //If the message’s path equals "/my_path"...//
        if (messageEvent.getPath().equals(Constants.MONITOR_HEARTRATE)) {

            //...retrieve the message//
            final String message = new String(messageEvent.getData());
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);

            //Broadcast the received Data Layer messages locally//
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }*/
    }
}
