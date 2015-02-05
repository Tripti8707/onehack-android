package com.arbrr.onehack.api;

import android.content.Context;
import android.net.Uri;

import com.arbrr.onehack.R;

/**
 * Created by damian on 2/4/15.
 */
public abstract class ApiUtils {

  private ApiUtils() {}

  public static Uri getEndpoint(Context context) {
    return Uri.parse(context.getString(R.string.api_endpoint)).buildUpon()
      .appendPath(context.getString(R.string.api_version)).build();
  }
}
