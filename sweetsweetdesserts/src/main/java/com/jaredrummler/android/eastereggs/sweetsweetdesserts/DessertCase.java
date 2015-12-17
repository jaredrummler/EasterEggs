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

package com.jaredrummler.android.eastereggs.sweetsweetdesserts;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;

public class DessertCase extends Activity {

  private DessertCaseView dessertCaseView;

  @Override public void onStart() {
    super.onStart();
    if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      PackageManager pm = getPackageManager();
      ComponentName cn = new ComponentName(this, DessertCaseDream.class);
      if (pm.getComponentEnabledSetting(cn) != COMPONENT_ENABLED_STATE_ENABLED) {
        pm.setComponentEnabledSetting(cn, COMPONENT_ENABLED_STATE_ENABLED, DONT_KILL_APP);
      }
    }
    dessertCaseView = new DessertCaseView(this);
    DessertCaseView.RescalingContainer container = new DessertCaseView.RescalingContainer(this);
    container.setView(dessertCaseView);
    setContentView(container);
  }

  @Override public void onResume() {
    super.onResume();
    dessertCaseView.postDelayed(new Runnable() {

      @Override public void run() {
        dessertCaseView.start();
      }
    }, 1000);
  }

  @Override public void onPause() {
    super.onPause();
    dessertCaseView.stop();
  }

}