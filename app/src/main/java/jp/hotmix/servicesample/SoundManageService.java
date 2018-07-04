package jp.hotmix.servicesample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

public class SoundManageService extends Service {
    private MediaPlayer _player;
    private static final String TAG = "SoundManageService";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        _player = new MediaPlayer();
        String id = "soundmanagerservice_notification_channel";
        String name = getString(R.string.notification_channel_name);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(id, name, importance);

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String mediaFileUriStr = "android.resource://" + getPackageName() + "/" + R.raw.music_sample;
        Uri mediaFileUri = Uri.parse(mediaFileUriStr);

        try {
            _player.setDataSource(SoundManageService.this, mediaFileUri);
            _player.setOnPreparedListener(new PlayerPreparedListener());
            _player.setOnCompletionListener(new PlayerCompletionListener());
            _player.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: called");
        if (_player.isPlaying()){
            _player.stop();
        }

        _player.release();
        _player = null;
    }

    private class PlayerPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.i(TAG, "onPrepared: called");
            mp.start();
        }
    }

    private class PlayerCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.i(TAG, "onCompletion: called");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    SoundManageService.this, "soundmanagerservice_notification_channel"
            );
            builder.setSmallIcon(android.R.drawable.ic_dialog_info);
            builder.setContentTitle(getString(R.string.msg_notification_title_finish));
            builder.setContentText(getString(R.string.msg_notification_text_finish));

            Notification notification = builder.build();

            NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, notification);
            Log.i(TAG, "onCompletion: notification notify()");
            stopSelf();
        }
    }


}
