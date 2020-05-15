package de.schuwue.insomnia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.MediaController;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements IVLCVout.Callback {

    private String mediaURL = null;

    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;

    private LibVLC mLibVLC = null;
    private VLCVideoLayout mVideoLayout = null;
    private MediaPlayer mMediaPlayer = null;
    private MediaController controller;

    // Handle Fire TV remote events
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        float rate;

        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
                controller.show(10000);

                handled = true;
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (mMediaPlayer.getPlayerState() == Media.State.Playing) {
                    mMediaPlayer.pause();
                    controller.show();
                } else {
                    mMediaPlayer.play();
                    controller.hide();
                }

                handled = true;
                break;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                mMediaPlayer.setTime(mMediaPlayer.getTime() + 15000);

                handled = true;
                break;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                mMediaPlayer.setTime(mMediaPlayer.getTime() - 15000);

                handled = true;
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                rate = mMediaPlayer.getRate();
                if (rate <= 1.0)
                    mMediaPlayer.setRate(rate * 2);

                handled = true;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                rate = mMediaPlayer.getRate();
                if (rate >= 0.5)
                    mMediaPlayer.setRate(rate / 2);

                handled = true;
                break;

        }
        return handled || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void initializePlayer() {
        Uri uri = Uri.parse(mediaURL);

        // Add mediacontrol callback
        mMediaPlayer.getVLCVout().addCallback(this);

        // Set media from URL to VLC lib
        final Media media = new Media(mLibVLC, uri);
        mMediaPlayer.setMedia(media);
        media.release();

        // Start playback
        mMediaPlayer.play();
    }

    private void releasePlayer() {
        mMediaPlayer.stop();
        mMediaPlayer.getVLCVout().detachViews();
        mMediaPlayer.getVLCVout().removeCallback(this);
    }

    private MediaController.MediaPlayerControl playerInterface = new MediaController.MediaPlayerControl() {
        public int getBufferPercentage() {
            return 0;
        }

        public int getCurrentPosition() {
            float pos = mMediaPlayer.getPosition();
            return (int)(pos * getDuration());
        }

        public int getDuration() {
            return (int)mMediaPlayer.getLength();
        }

        public boolean isPlaying() {
            return mMediaPlayer.isPlaying();
        }

        public void pause() {
            mMediaPlayer.pause();
        }

        public void seekTo(int pos) {
            mMediaPlayer.setPosition((float)pos / getDuration());
        }

        public void start() {
            mMediaPlayer.play();
        }

        public boolean canPause() {
            return true;
        }

        public boolean canSeekBackward() {
            return true;
        }

        public boolean canSeekForward() {
            return true;
        }

        @Override
        public int getAudioSessionId() {
            return 0;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Get the Intent that started this activity and extract the url
        Intent intent = getIntent();
        mediaURL = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(this, args);
        mMediaPlayer = new MediaPlayer(mLibVLC);
        mVideoLayout = findViewById(R.id.video_layout);
        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);
        controller = new MediaController(this);
        controller.setMediaPlayer(playerInterface);
        controller.setAnchorView(mVideoLayout);
        mVideoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.show(10000);
            }
        });
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }
}
