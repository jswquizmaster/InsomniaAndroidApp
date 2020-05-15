package de.schuwue.insomnia;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

public class WebAppInterface {
    private Context mContext;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /** Play a movie from the web page */
    @JavascriptInterface
    public void playMovie(String url) {
        Intent intent = new Intent(mContext, PlayerActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE, url);
        mContext.startActivity(intent);
    }

}
