package com.arbrr.onehack.ui.other;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by jawad on 26/07/15.
 *
 * Currently just handles when the keyboard is open and back button is pressed
 */
public class SearchEditText extends EditText {
  private static String LOGTAG = "MD/SearchEditText";

  Context context;

  BackpressListener mBackpressListener;

  public SearchEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
  }

  @Override
  public boolean onKeyPreIme(int keyCode, KeyEvent event) {
    Log.d(LOGTAG, "Key pressed");

    if (keyCode == KeyEvent.KEYCODE_BACK) {
      Log.d(LOGTAG, "Hiding keyboard! Back event!");

      // User has pressed Back key. So hide the keyboard
      InputMethodManager mgr = (InputMethodManager)
          context.getSystemService(Context.INPUT_METHOD_SERVICE);

      mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);

      // If there's a backpress listener, let it now act
      if(mBackpressListener != null) {
        // Let the backpress listener state whether this event has been fully handled
        return mBackpressListener.onBackpressCallback();
      }
    }

    // Tell the rest of the system that this event wasn't fully handled
    return false;
  }

  /**
   * Register a BackpressListener to be called when this EditText handles a back event
   * @param newBackListener Listener to be called
   */
  public void registerBackpressCallback(BackpressListener newBackListener) {
    this.mBackpressListener = newBackListener;
  }
}
