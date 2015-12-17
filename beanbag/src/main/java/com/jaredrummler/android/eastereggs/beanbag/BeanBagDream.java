/*
 * Copyright (C) 2015. Jared Rummler <jared.rummler@gmail.com>
 * Copyright (C) 2012 The Android Open Source Project
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

package com.jaredrummler.android.eastereggs.beanbag;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.dreams.DreamService;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class BeanBagDream extends DreamService {

  private Board board;

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();
    setInteractive(true);
    setFullscreen(true);
    board = new Board(this, null);
    setContentView(board);
  }

  @Override public void onDreamingStarted() {
    super.onDreamingStarted();
    board.startAnimation();
  }

  @Override public void onDreamingStopped() {
    board.stopAnimation();
    super.onDreamingStopped();
  }

}