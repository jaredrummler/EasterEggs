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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DessertCaseView extends FrameLayout {

  private static final String TAG = "DessertCaseView";

  private static final boolean DEBUG = false;

  public static final float SCALE = 0.25f; // natural display size will be SCALE*cellSize

  static final int START_DELAY = 5000;
  static final int DELAY = 2000;
  static final int DURATION = 500;

  private static final int TAG_POS = 0x2000001;
  private static final int TAG_SPAN = 0x2000002;

  private static final int[] PASTRIES = {
      R.drawable.dessert_android
  };

  private static final int[] RARE_PASTRIES = {
      R.drawable.dessert_cupcake, // 2009
      R.drawable.dessert_donut, // 2009
      R.drawable.dessert_eclair, // 2009
      R.drawable.dessert_froyo, // 2010
      R.drawable.dessert_gingerbread, // 2010
      R.drawable.dessert_honeycomb, // 2011
      R.drawable.dessert_ics, // 2011
      R.drawable.dessert_jellybean, // 2012
      R.drawable.dessert_kitkat, // 2013
      R.drawable.dessert_lollipop, // 2014
      R.drawable.dessert_marshmallow // 2015
  };

  private static final int NUM_PASTRIES = PASTRIES.length + RARE_PASTRIES.length;

  private static final float[] MASK = {
      0f, 0f, 0f, 0f, 255f, 0f, 0f, 0f, 0f, 255f, 0f, 0f, 0f, 0f, 255f, 1f, 0f, 0f, 0f, 0f
  };

  private static final float[] ALPHA_MASK = {
      0f, 0f, 0f, 0f, 255f, 0f, 0f, 0f, 0f, 255f, 0f, 0f, 0f, 0f, 255f, 0f, 0f, 0f, 1f, 0f
  };

  private static final Random RANDOM = new Random();

  private static final List<Integer> MATERIAL_COLORS = new ArrayList<>();

  private static final float PROB_2X = 0.33f;
  private static final float PROB_3X = 0.1f;
  private static final float PROB_4X = 0.01f;

  static {
    MATERIAL_COLORS.add(0xFFFFC107); // Amber-500
    MATERIAL_COLORS.add(0xFFFFB300); // Amber-600
    MATERIAL_COLORS.add(0xFFFFA000); // Amber-700
    MATERIAL_COLORS.add(0xFFFF8F00); // Amber-800
    MATERIAL_COLORS.add(0xFFFF6F00); // Amber-900
    MATERIAL_COLORS.add(0xFF5677FC); // Blue-500
    MATERIAL_COLORS.add(0xFF4E6CEF); // Blue-600
    MATERIAL_COLORS.add(0xFF455EDE); // Blue-700
    MATERIAL_COLORS.add(0xFF3B50CE); // Blue-800
    MATERIAL_COLORS.add(0xFF2A36B1); // Blue-900
    MATERIAL_COLORS.add(0xFF607D8B); // Blue-grey-500
    MATERIAL_COLORS.add(0xFF546E7A); // Blue-grey-600
    MATERIAL_COLORS.add(0xFF455A64); // Blue-grey-700
    MATERIAL_COLORS.add(0xFF37474F); // Blue-grey-800
    MATERIAL_COLORS.add(0xFF263238); // Blue-grey-900
    MATERIAL_COLORS.add(0xFF795548); // Brown-500
    MATERIAL_COLORS.add(0xFF6D4C41); // Brown-600
    MATERIAL_COLORS.add(0xFF5D4037); // Brown-700
    MATERIAL_COLORS.add(0xFF4E342E); // Brown-800
    MATERIAL_COLORS.add(0xFF3E2723); // Brown-900
    MATERIAL_COLORS.add(0xFF00BCD4); // Cyan-500
    MATERIAL_COLORS.add(0xFF00ACC1); // Cyan-600
    MATERIAL_COLORS.add(0xFF0097A7); // Cyan-700
    MATERIAL_COLORS.add(0xFF00838F); // Cyan-800
    MATERIAL_COLORS.add(0xFF006064); // Cyan-900
    MATERIAL_COLORS.add(0xFFFF5722); // Deep-orange-500
    MATERIAL_COLORS.add(0xFFF4511E); // Deep-orange-600
    MATERIAL_COLORS.add(0xFFE64A19); // Deep-orange-700
    MATERIAL_COLORS.add(0xFFD84315); // Deep-orange-800
    MATERIAL_COLORS.add(0xFFBF360C); // Deep-orange-900
    MATERIAL_COLORS.add(0xFF673AB7); // Deep-purple-500
    MATERIAL_COLORS.add(0xFF5E35B1); // Deep-purple-600
    MATERIAL_COLORS.add(0xFF512DA8); // Deep-purple-700
    MATERIAL_COLORS.add(0xFF4527A0); // Deep-purple-800
    MATERIAL_COLORS.add(0xFF311B92); // Deep-purple-900
    MATERIAL_COLORS.add(0xFF259B24); // Green-500
    MATERIAL_COLORS.add(0xFF0A8F08); // Green-600
    MATERIAL_COLORS.add(0xFF0A7E07); // Green-700
    MATERIAL_COLORS.add(0xFF056F00); // Green-800
    MATERIAL_COLORS.add(0xFF0D5302); // Green-900
    MATERIAL_COLORS.add(0xFF3F51B5); // Indigo-500
    MATERIAL_COLORS.add(0xFF3949AB); // Indigo-600
    MATERIAL_COLORS.add(0xFF303F9F); // Indigo-700
    MATERIAL_COLORS.add(0xFF283593); // Indigo-800
    MATERIAL_COLORS.add(0xFF1A237E); // Indigo-900
    MATERIAL_COLORS.add(0xFF03A9F4); // Light-blue-500
    MATERIAL_COLORS.add(0xFF039BE5); // Light-blue-600
    MATERIAL_COLORS.add(0xFF0288D1); // Light-blue-700
    MATERIAL_COLORS.add(0xFF0277BD); // Light-blue-800
    MATERIAL_COLORS.add(0xFF01579B); // Light-blue-900
    MATERIAL_COLORS.add(0xFF8BC34A); // Light-green-500
    MATERIAL_COLORS.add(0xFF7CB342); // Light-green-600
    MATERIAL_COLORS.add(0xFF689F38); // Light-green-700
    MATERIAL_COLORS.add(0xFF558B2F); // Light-green-800
    MATERIAL_COLORS.add(0xFF33691E); // Light-green-900
    MATERIAL_COLORS.add(0xFFCDDC39); // Lime-500
    MATERIAL_COLORS.add(0xFFC0CA33); // Lime-600
    MATERIAL_COLORS.add(0xFFAFB42B); // Lime-700
    MATERIAL_COLORS.add(0xFF9E9D24); // Lime-800
    MATERIAL_COLORS.add(0xFF827717); // Lime-900
    MATERIAL_COLORS.add(0xFFFF9800); // Orange-500
    MATERIAL_COLORS.add(0xFFFB8C00); // Orange-600
    MATERIAL_COLORS.add(0xFFF57C00); // Orange-700
    MATERIAL_COLORS.add(0xFFEF6C00); // Orange-800
    MATERIAL_COLORS.add(0xFFE65100); // Orange-900
    MATERIAL_COLORS.add(0xFFE91E63); // Pink-500
    MATERIAL_COLORS.add(0xFFD81B60); // Pink-600
    MATERIAL_COLORS.add(0xFFC2185B); // Pink-700
    MATERIAL_COLORS.add(0xFFAD1457); // Pink-800
    MATERIAL_COLORS.add(0xFF880E4F); // Pink-900
    MATERIAL_COLORS.add(0xFF9C27B0); // Purple-500
    MATERIAL_COLORS.add(0xFF8E24AA); // Purple-600
    MATERIAL_COLORS.add(0xFF7B1FA2); // Purple-700
    MATERIAL_COLORS.add(0xFF6A1B9A); // Purple-800
    MATERIAL_COLORS.add(0xFF4A148C); // Purple-900
    MATERIAL_COLORS.add(0xFFE51C23); // Red-500
    MATERIAL_COLORS.add(0xFFDD191D); // Red-600
    MATERIAL_COLORS.add(0xFFD01716); // Red-700
    MATERIAL_COLORS.add(0xFFC41411); // Red-800
    MATERIAL_COLORS.add(0xFFB0120A); // Red-900
    MATERIAL_COLORS.add(0xFF009688); // Teal-500
    MATERIAL_COLORS.add(0xFF00897B); // Teal-600
    MATERIAL_COLORS.add(0xFF00796B); // Teal-700
    MATERIAL_COLORS.add(0xFF00695C); // Teal-800
    MATERIAL_COLORS.add(0xFF004D40); // Teal-900
    MATERIAL_COLORS.add(0xFFFFEB3B); // Yellow-500
    MATERIAL_COLORS.add(0xFFFDD835); // Yellow-600
    MATERIAL_COLORS.add(0xFFFBC02D); // Yellow-700
    MATERIAL_COLORS.add(0xFFF9A825); // Yellow-800
    MATERIAL_COLORS.add(0xFFF57F17); // Yellow-900
  }

  private final Set<Point> freeList = new HashSet<>();
  private final Handler handler = new Handler();
  private final HashSet<View> tmpSet = new HashSet<>();
  private SparseArray<Drawable> drawables = new SparseArray<>(NUM_PASTRIES);
  private boolean started;
  private int cellSize;
  private int width, height;
  private int rows, columns;
  private View[] cells;

  private final Runnable juggle = new Runnable() {

    @Override public void run() {
      final int N = getChildCount();

      final int K = 1; // irand(1,3);
      for (int i = 0; i < K; i++) {
        final View child = getChildAt((int) (Math.random() * N));
        place(child, true);
      }

      fillFreeList();

      if (started) {
        handler.postDelayed(juggle, DELAY);
      }
    }
  };

  public DessertCaseView(Context context) {
    this(context, null);
  }

  public DessertCaseView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DessertCaseView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    final Resources res = getResources();

    started = false;

    cellSize = res.getDimensionPixelSize(R.dimen.dessert_case_cell_size);
    final BitmapFactory.Options opts = new BitmapFactory.Options();
    if (cellSize < 512) { // assuming 512x512 images
      opts.inSampleSize = 2;
    }
    opts.inMutable = true;
    Bitmap loaded = null;
    for (int[] list : new int[][]{PASTRIES, RARE_PASTRIES}) {
      for (int resid : list) {
        try {
          opts.inBitmap = loaded;
          // http://stackoverflow.com/questions/16034756/why-does-decoding-bitmap-with-inbitmap-always-get-java-lang-illegalargumentexcep
          opts.inSampleSize = 1;
          loaded = BitmapFactory.decodeResource(res, resid, opts);
          final BitmapDrawable d = new BitmapDrawable(res, convertToAlphaMask(loaded));
          d.setColorFilter(new ColorMatrixColorFilter(ALPHA_MASK));
          d.setBounds(0, 0, cellSize, cellSize);
          drawables.append(resid, d);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    if (DEBUG) setWillNotDraw(false);
  }

  private static Bitmap convertToAlphaMask(Bitmap b) {
    Bitmap a = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ALPHA_8);
    Canvas c = new Canvas(a);
    Paint pt = new Paint();
    pt.setColorFilter(new ColorMatrixColorFilter(MASK));
    c.drawBitmap(b, 0.0f, 0.0f, pt);
    return a;
  }

  static float frand() {
    return (float) (Math.random());
  }

  static float frand(float a, float b) {
    return (frand() * (b - a) + a);
  }

  static int irand(int a, int b) {
    return (int) (frand(a, b));
  }

  public void start() {
    if (!started) {
      started = true;
      fillFreeList(DURATION * 4);
    }
    handler.postDelayed(juggle, START_DELAY);
  }

  public void stop() {
    started = false;
    handler.removeCallbacks(juggle);
  }

  int pick(int[] a) {
    return a[(int) (Math.random() * a.length)];
  }

  @Override protected synchronized void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    if (width == w && height == h) return;

    final boolean wasStarted = started;
    if (wasStarted) {
      stop();
    }

    width = w;
    height = h;

    cells = null;
    removeAllViewsInLayout();
    freeList.clear();

    rows = height / cellSize;
    columns = width / cellSize;

    cells = new View[rows * columns];

    if (DEBUG) Log.v(TAG, String.format("New dimensions: %dx%d", columns, rows));

    setScaleX(SCALE);
    setScaleY(SCALE);
    setTranslationX(0.5f * (width - cellSize * columns) * SCALE);
    setTranslationY(0.5f * (height - cellSize * rows) * SCALE);

    for (int j = 0; j < rows; j++) {
      for (int i = 0; i < columns; i++) {
        freeList.add(new Point(i, j));
      }
    }

    if (wasStarted) {
      start();
    }
  }

  @Override public void onDraw(Canvas c) {
    super.onDraw(c);
    if (!DEBUG) return;

    Paint pt = new Paint();
    pt.setStyle(Paint.Style.STROKE);
    pt.setColor(0xFFCCCCCC);
    pt.setStrokeWidth(2.0f);

    final Rect check = new Rect();
    final int N = getChildCount();
    for (int i = 0; i < N; i++) {
      View stone = getChildAt(i);

      stone.getHitRect(check);

      c.drawRect(check, pt);
    }
  }

  public void fillFreeList() {
    fillFreeList(DURATION);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public synchronized void fillFreeList(int animationLen) {
    final Context ctx = getContext();
    final LayoutParams lp = new LayoutParams(cellSize, cellSize);

    while (!freeList.isEmpty()) {
      Point pt = freeList.iterator().next();
      freeList.remove(pt);
      final int i = pt.x;
      final int j = pt.y;

      if (cells[j * columns + i] != null) continue;
      final ImageView v = new ImageView(ctx);
      v.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View view) {
          place(v, true);
          postDelayed(new Runnable() {

            @Override
            public void run() {
              fillFreeList();
            }
          }, DURATION / 2);
        }
      });

      final int c = MATERIAL_COLORS.get(RANDOM.nextInt(MATERIAL_COLORS.size())); // random color
      v.setBackgroundColor(c);

      final float which = frand();
      final Drawable d;
      if (which < 0.5f) {
        d = drawables.get(pick(RARE_PASTRIES));
      } else if (which < 0.7f) {
        d = drawables.get(pick(PASTRIES));
      } else {
        d = null;
      }
      if (d != null) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
          v.getOverlay().add(d);
        } else {
          v.setImageDrawable(d);
          v.setColorFilter(Color.WHITE);
        }
      }

      lp.width = lp.height = cellSize;
      addView(v, lp);
      place(v, pt, false);
      if (animationLen > 0) {
        final float s = (Integer) v.getTag(TAG_SPAN);
        v.setScaleX(0.5f * s);
        v.setScaleY(0.5f * s);
        v.setAlpha(0f);
        v.animate().withLayer().scaleX(s).scaleY(s).alpha(1f).setDuration(animationLen);
      }
    }
  }

  public void place(View v, boolean animate) {
    place(v, new Point(irand(0, columns), irand(0, rows)), animate);
  }

  // we don't have .withLayer() on general Animators
  private final Animator.AnimatorListener makeHardwareLayerListener(final View v) {
    return new AnimatorListenerAdapter() {

      @Override
      public void onAnimationEnd(Animator animator) {
        v.setLayerType(View.LAYER_TYPE_NONE, null);
      }

      @Override
      public void onAnimationStart(Animator animator) {
        v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        v.buildLayer();
      }
    };
  }

  public synchronized void place(View v, Point pt, boolean animate) {
    final int i = pt.x;
    final int j = pt.y;
    final float rnd = frand();
    if (v.getTag(TAG_POS) != null) {
      for (final Point oc : getOccupied(v)) {
        freeList.add(oc);
        cells[oc.y * columns + oc.x] = null;
      }
    }
    int scale = 1;
    if (rnd < PROB_4X) {
      if (!(i >= columns - 3 || j >= rows - 3)) {
        scale = 4;
      }
    } else if (rnd < PROB_3X) {
      if (!(i >= columns - 2 || j >= rows - 2)) {
        scale = 3;
      }
    } else if (rnd < PROB_2X) {
      if (!(i == columns - 1 || j == rows - 1)) {
        scale = 2;
      }
    }

    v.setTag(TAG_POS, pt);
    v.setTag(TAG_SPAN, scale);

    tmpSet.clear();

    final Point[] occupied = getOccupied(v);
    for (final Point oc : occupied) {
      final View squatter = cells[oc.y * columns + oc.x];
      if (squatter != null) {
        tmpSet.add(squatter);
      }
    }

    for (final View squatter : tmpSet) {
      for (final Point sq : getOccupied(squatter)) {
        freeList.add(sq);
        cells[sq.y * columns + sq.x] = null;
      }
      if (squatter != v) {
        squatter.setTag(TAG_POS, null);
        if (animate) {
          ViewPropertyAnimator animator = squatter.animate();
          if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            animator.withLayer();
          }
          animator.scaleX(0.5f).scaleY(0.5f).alpha(0)
              .setDuration(DURATION).setInterpolator(new AccelerateInterpolator())
              .setListener(new Animator.AnimatorListener() {

                @Override public void onAnimationStart(Animator animator) {
                }

                @Override public void onAnimationEnd(Animator animator) {
                  removeView(squatter);
                }

                @Override public void onAnimationCancel(Animator animator) {
                }

                @Override public void onAnimationRepeat(Animator animator) {
                }
              }).start();
        } else {
          removeView(squatter);
        }
      }
    }

    for (final Point oc : occupied) {
      cells[oc.y * columns + oc.x] = v;
      freeList.remove(oc);
    }

    final float rot = irand(0, 4) * 90f;

    if (animate) {
      v.bringToFront();

      AnimatorSet set1 = new AnimatorSet();
      set1.playTogether(ObjectAnimator.ofFloat(v, View.SCALE_X, (float) scale),
          ObjectAnimator.ofFloat(v, View.SCALE_Y, (float) scale));
      set1.setInterpolator(new AnticipateOvershootInterpolator());
      set1.setDuration(DURATION);

      AnimatorSet set2 = new AnimatorSet();
      set2.playTogether(ObjectAnimator.ofFloat(v, View.ROTATION, rot),
          ObjectAnimator.ofFloat(v, View.X, i * cellSize + (scale - 1) * cellSize / 2),
          ObjectAnimator.ofFloat(v, View.Y, j * cellSize + (scale - 1) * cellSize / 2));
      set2.setInterpolator(new DecelerateInterpolator());
      set2.setDuration(DURATION);

      set1.addListener(makeHardwareLayerListener(v));

      set1.start();
      set2.start();
    } else {
      v.setX(i * cellSize + (scale - 1) * cellSize / 2);
      v.setY(j * cellSize + (scale - 1) * cellSize / 2);
      v.setScaleX(scale);
      v.setScaleY(scale);
      v.setRotation(rot);
    }
  }

  private Point[] getOccupied(View v) {
    final int scale = (Integer) v.getTag(TAG_SPAN);
    final Point pt = (Point) v.getTag(TAG_POS);
    if (pt == null || scale == 0) return new Point[0];

    final Point[] result = new Point[scale * scale];
    int p = 0;
    for (int i = 0; i < scale; i++) {
      for (int j = 0; j < scale; j++) {
        result[p++] = new Point(pt.x + i, pt.y + j);
      }
    }
    return result;
  }

  public static class RescalingContainer extends FrameLayout {

    private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;

    private DessertCaseView dessertCaseView;

    private float darkness;

    public RescalingContainer(Context context) {
      super(context);

      setSystemUiVisibility(0
          | View.SYSTEM_UI_FLAG_FULLSCREEN
          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void setView(DessertCaseView v) {
      addView(v);
      dessertCaseView = v;
    }

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
      final float w = right - left;
      final float h = bottom - top;
      final int w2 = (int) (w / DessertCaseView.SCALE / 2);
      final int h2 = (int) (h / DessertCaseView.SCALE / 2);
      final int cx = (int) (left + w * 0.5f);
      final int cy = (int) (top + h * 0.5f);
      dessertCaseView.layout(cx - w2, cy - h2, cx + w2, cy + h2);
    }

    public float getDarkness() {
      return darkness;
    }

    public void setDarkness(float p) {
      darkness = p;
      getDarkness();
      final int x = (int) (p * 0xff);
      setBackgroundColor(x << 24 & 0xFF000000);
    }
  }

}