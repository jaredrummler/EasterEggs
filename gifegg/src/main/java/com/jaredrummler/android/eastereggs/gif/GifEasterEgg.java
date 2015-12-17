/*
 * Copyright (C) 2015. Jared Rummler <jared.rummler@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.jaredrummler.android.eastereggs.gif;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import pl.droidsonroids.gif.GifDrawable;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class GifEasterEgg extends Activity {

  /**
   * The resource id (drawable or raw). Default is {@link R.drawable#easteregg}
   */
  public static final String EXTRA_GIF = "gif_id";

  private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;

  @SuppressLint("InlinedApi")
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    FrameLayout layout = new FrameLayout(this);
    layout.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    ImageView imageView = new ImageView(this);
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    params.gravity = Gravity.CENTER;
    layout.addView(imageView, params);
    setContentView(layout);

    layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    try {
      int drawableId = getIntent().getIntExtra(EXTRA_GIF, R.drawable.easteregg);
      GifDrawable gifDrawable = new GifDrawable(getResources(), drawableId);
      Bitmap bitmap = gifDrawable.seekToFrameAndGet(0);
      int color = bitmap.getPixel(0, 0);
      layout.setBackgroundColor(color);
      imageView.setImageDrawable(gifDrawable);
      gifDrawable.start();
    } catch (Exception e) {
      Toast.makeText(getApplicationContext(), "¯\\_| ✖ 〜 ✖ |_/¯", Toast.LENGTH_LONG).show();
      finish();
    }
  }

}
