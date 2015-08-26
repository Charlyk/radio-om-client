package albu.eduard.moldova.radiomdv04;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.io.IOException;

public class PlayService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener {

    private static final String LOG_TAG = PlayService.class.getName();
    private MediaPlayer mediaPlayer =new MediaPlayer();
    private TelephonyManager tm;
    public static PowerManager.WakeLock wl;
    private String address = null;

    @Override
    public void onCreate() {

        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.reset();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        try {
            if (address == null) {
                if ((address = intent.getExtras().getString("sentChanel")) != null) {
                    mediaPlayer.reset();
                    if (!mediaPlayer.isPlaying()) {
                        try {
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(address);
                            mediaPlayer.prepare();
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException ignored) {
                        }
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

                                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                                wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                                        getString(R.string.app_name));
                                wl.acquire();
                                playMedia();
                            }
                        });
                    }
                } else {
                    stopMedia();
                    mediaPlayer.release();
                    stopSelf();
                }
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    public void stopMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private PhoneStateListener mPhoneListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        stopSelf();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        playMedia();
                        break;
                    default:
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
