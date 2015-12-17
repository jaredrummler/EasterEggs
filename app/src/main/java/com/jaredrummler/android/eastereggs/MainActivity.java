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

package com.jaredrummler.android.eastereggs;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaredrummler.android.eastereggs.beanbag.BeanBag;
import com.jaredrummler.android.eastereggs.sweetsweetdesserts.DessertCase;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

  private static final String GITHUB_URL = "https://github.com/jaredrummler/EasterEggs";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    List<EasterEgg> eggs = new ArrayList<>();
    Intent sweetsweetdesserts = new Intent(this, DessertCase.class);
    eggs.add(
        new EasterEgg("sweetsweetdesserts", R.drawable.sweetsweetdesserts, sweetsweetdesserts));

    Intent beanbag = new Intent(this, BeanBag.class);
    eggs.add(new EasterEgg("beanbag", R.drawable.beanbag, beanbag));

    Adapter adapter = new Adapter(eggs);
    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    //noinspection SimplifiableIfStatement
    if (id == R.id.action_github) {
      try {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL)));
      } catch (ActivityNotFoundException ignored) {
      }
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public static class Adapter extends RecyclerView.Adapter<ViewHolder> {

    private final List<EasterEgg> eggs;

    private final ViewHolder.OnClickListener onClickListener = new ViewHolder.OnClickListener() {

      @Override public void onClick(View v, int position) {
        v.getContext().startActivity(eggs.get(position).intent);
      }
    };

    public Adapter(List<EasterEgg> eggs) {
      this.eggs = eggs;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v =
          LayoutInflater.from(parent.getContext()).inflate(R.layout.item_easteregg, parent, false);
      return new ViewHolder(v).setOnClickListener(R.id.cardview, onClickListener);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      EasterEgg egg = eggs.get(position);
      GifImageView gif = holder.find(R.id.gif);
      TextView title = holder.find(R.id.title);
      gif.setImageResource(egg.gif);
      title.setText(egg.name);
    }

    @Override public int getItemCount() {
      return eggs.size();
    }

  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private final SparseArray<View> views = new SparseArray<>();

    public ViewHolder(View itemView) {
      super(itemView);
    }

    public ViewHolder setOnClickListener(@IdRes int id, final OnClickListener listener) {
      find(id).setOnClickListener(new View.OnClickListener() {

        @Override public void onClick(View v) {
          listener.onClick(v, getLayoutPosition());
        }
      });
      return this;
    }

    public <T extends View> T find(@IdRes int id) {
      View view = views.get(id);
      if (view == null) {
        view = itemView.findViewById(id);
        views.put(id, view);
      }
      //noinspection unchecked
      return (T) view;
    }

    public interface OnClickListener {

      void onClick(View v, int position);
    }

  }

  public static class EasterEgg {

    public final String name;
    public final int gif;
    public final Intent intent;

    public EasterEgg(String name, int gif, Intent intent) {
      this.name = name;
      this.gif = gif;
      this.intent = intent;
    }

  }

}
