package com.arbrr.onehack.ui.other;

/**
 * Created by jawad on 26/07/15.
 */
public interface BackpressListener {
  /**
   * Callback method for when a backpress of sorts occurs
   * (specifically created for ContactsFragment and SearchEditText interaction)
   * @return True if backpress successfully and completely handled, false otherwise
   */
  public boolean onBackpressCallback();
}
