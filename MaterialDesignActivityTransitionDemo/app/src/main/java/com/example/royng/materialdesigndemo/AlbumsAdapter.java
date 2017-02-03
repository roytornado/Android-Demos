package com.example.royng.materialdesigndemo;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder> {

  private Context mContext;
  private List<Album> albumList = demoAlbumList();

  class AlbumViewHolder extends RecyclerView.ViewHolder {
    View main;
    TextView title;
    ImageView thumbnail, overflow;

    AlbumViewHolder(View view) {
      super(view);
      main = view;
      title = (TextView) view.findViewById(R.id.title);
      thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
      overflow = (ImageView) view.findViewById(R.id.overflow);
    }
  }

  AlbumsAdapter(Context mContext) {
    this.mContext = mContext;
  }

  @Override
  public int getItemCount() {
    return albumList.size();
  }

  @Override
  public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
    return new AlbumViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(final AlbumViewHolder holder, int position) {
    final Album album = albumList.get(position);
    holder.title.setText(album.name);
    Glide.with(mContext).load(album.thumbnail).into(holder.thumbnail);
    holder.overflow.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        showPopupMenu(holder.overflow);
      }
    });
    holder.main.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ActivityOptions options = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
          options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, holder.thumbnail, mContext.getString(R.string.picture_transition_name));
          Intent intent = DetailActivity.makeIntent(mContext, album);
          mContext.startActivity(intent, options.toBundle());
        } else {
          Intent intent = DetailActivity.makeIntent(mContext, album);
          mContext.startActivity(intent);
        }
      }
    });
  }

  private void showPopupMenu(View view) {
    PopupMenu popup = new PopupMenu(mContext, view);
    MenuInflater inflater = popup.getMenuInflater();
    inflater.inflate(R.menu.menu_album, popup.getMenu());
    popup.show();
  }


  public ArrayList<Album> demoAlbumList() {
    ArrayList<Album> list = new ArrayList<Album>();
    Album album;
    album = new Album();
    album.name = "Dawes, 'We're All Gonna Die'";
    album.thumbnail = "http://img.wennermedia.com/660-width/unnamed-19-9d57ff2d-41f7-478e-ba19-7b9f445625ef.jpg";
    list.add(album);
    album = new Album();
    album.name = "Esperanza Spalding, 'Emily's D+Evolution'";
    album.thumbnail = "http://img.wennermedia.com/660-width/esperanza-spalding-emilys-d-evolution-album-stream-2eb15bbd-a1a9-4310-8c99-32f212ec02aa.jpg";
    list.add(album);
    album = new Album();
    album.name = "Kyle Dixon & Michael Stein, 'Stranger Things, Volume One and Two'";
    album.thumbnail = "http://img.wennermedia.com/660-width/survive-stranger-things-vol-1-7908b092-368d-4459-b23f-2835f638c093.jpg";
    list.add(album);
    album = new Album();
    album.name = "Norah Jones, 'Day Breaks'";
    album.thumbnail = "http://img.wennermedia.com/660-width/rs-norah-02-4fb7f8bb-f41b-4ff6-a58c-3df0f101d369.jpg";
    list.add(album);
    album = new Album();
    album.name = "Iggy Pop, 'Post Pop Depression'";
    album.thumbnail = "http://img.wennermedia.com/660-width/e6ea053b-989f6910-c601-45b4-b3b6-8d828bfdde6f.jpg";
    list.add(album);
    album = new Album();
    album.name = "The Monkees, 'Good Times!'";
    album.thumbnail = "http://img.wennermedia.com/660-width/5d383d24-5766db7c-4dbc-4cbd-b5ce-3c6ba04d1d74.jpg";
    list.add(album);
    album = new Album();
    album.name = "Future, 'Evol'";
    album.thumbnail = "http://img.wennermedia.com/660-width/7ca1bf65-092410c2-fad6-4e7a-b73b-8846d164224c.jpg";
    list.add(album);
    album = new Album();
    album.name = "Drake, 'Views'";
    album.thumbnail = "http://img.wennermedia.com/660-width/3b385c122fde43a8f39b41ba312803771000x1000x1-d1dd133d-f285-4051-b24d-9ccaac1f81d0.jpg";
    list.add(album);
    album = new Album();
    album.name = "Wilco, 'Schmilco'";
    album.thumbnail = "http://img.wennermedia.com/660-width/schmilco_wilco-1fa97413-1cf5-473d-b98c-b8544d09d89b.jpg";
    list.add(album);
    album = new Album();
    album.name = "Maxwell, 'blackSUMMERS'night'";
    album.thumbnail = "http://img.wennermedia.com/660-width/rs-maxwell-blacksummersnight-e27d87a6-8b50-43e7-9036-8763339728c4.jpg";
    list.add(album);
    album = new Album();
    album.name = "Elton John, 'Wonderful Crazy Night'";
    album.thumbnail = "http://img.wennermedia.com/660-width/wonderful_crazy_night-a3949316-7acb-4e58-a651-2584228697fa.jpg";
    list.add(album);
    album = new Album();
    album.name = "Tove Lo, 'Lady Wood'";
    album.thumbnail = "http://img.wennermedia.com/660-width/tove-lo-lady-wood-2016-2480x2480-8b8530ea-5828-4c5a-afdd-e66bdd26db98.jpg";
    list.add(album);
    album = new Album();
    album.name = "Bonnie Raitt, 'Dig In Deep'";
    album.thumbnail = "http://img.wennermedia.com/660-width/bonnieraittdig-2e906252-7312-483d-8e20-9afad1fa0920.jpg";
    list.add(album);
    album = new Album();
    album.name = "Metallica, 'Hardwired… to Self-Destruct'";
    album.thumbnail = "http://img.wennermedia.com/660-width/metallica-e00df7a2-08c9-43a3-9863-076e8158c566.jpg";
    list.add(album);
    album = new Album();
    album.name = "Anohni, 'Hopelessness'";
    album.thumbnail = "http://img.wennermedia.com/660-width/a1895762218_10-38143362-6beb-418d-9056-b1381c8e98e8.jpg";
    list.add(album);
    album.name = "Dawes, 'We're All Gonna Die'";
    album.thumbnail = "http://img.wennermedia.com/660-width/unnamed-19-9d57ff2d-41f7-478e-ba19-7b9f445625ef.jpg";
    list.add(album);
    album = new Album();
    album.name = "Esperanza Spalding, 'Emily's D+Evolution'";
    album.thumbnail = "http://img.wennermedia.com/660-width/esperanza-spalding-emilys-d-evolution-album-stream-2eb15bbd-a1a9-4310-8c99-32f212ec02aa.jpg";
    list.add(album);
    album = new Album();
    album.name = "Kyle Dixon & Michael Stein, 'Stranger Things, Volume One and Two'";
    album.thumbnail = "http://img.wennermedia.com/660-width/survive-stranger-things-vol-1-7908b092-368d-4459-b23f-2835f638c093.jpg";
    list.add(album);
    album = new Album();
    album.name = "Norah Jones, 'Day Breaks'";
    album.thumbnail = "http://img.wennermedia.com/660-width/rs-norah-02-4fb7f8bb-f41b-4ff6-a58c-3df0f101d369.jpg";
    list.add(album);
    album = new Album();
    album.name = "Iggy Pop, 'Post Pop Depression'";
    album.thumbnail = "http://img.wennermedia.com/660-width/e6ea053b-989f6910-c601-45b4-b3b6-8d828bfdde6f.jpg";
    list.add(album);
    album = new Album();
    album.name = "The Monkees, 'Good Times!'";
    album.thumbnail = "http://img.wennermedia.com/660-width/5d383d24-5766db7c-4dbc-4cbd-b5ce-3c6ba04d1d74.jpg";
    list.add(album);
    album = new Album();
    album.name = "Future, 'Evol'";
    album.thumbnail = "http://img.wennermedia.com/660-width/7ca1bf65-092410c2-fad6-4e7a-b73b-8846d164224c.jpg";
    list.add(album);
    album = new Album();
    album.name = "Drake, 'Views'";
    album.thumbnail = "http://img.wennermedia.com/660-width/3b385c122fde43a8f39b41ba312803771000x1000x1-d1dd133d-f285-4051-b24d-9ccaac1f81d0.jpg";
    list.add(album);
    album = new Album();
    album.name = "Wilco, 'Schmilco'";
    album.thumbnail = "http://img.wennermedia.com/660-width/schmilco_wilco-1fa97413-1cf5-473d-b98c-b8544d09d89b.jpg";
    list.add(album);
    album = new Album();
    album.name = "Maxwell, 'blackSUMMERS'night'";
    album.thumbnail = "http://img.wennermedia.com/660-width/rs-maxwell-blacksummersnight-e27d87a6-8b50-43e7-9036-8763339728c4.jpg";
    list.add(album);
    album = new Album();
    album.name = "Elton John, 'Wonderful Crazy Night'";
    album.thumbnail = "http://img.wennermedia.com/660-width/wonderful_crazy_night-a3949316-7acb-4e58-a651-2584228697fa.jpg";
    list.add(album);
    album = new Album();
    album.name = "Tove Lo, 'Lady Wood'";
    album.thumbnail = "http://img.wennermedia.com/660-width/tove-lo-lady-wood-2016-2480x2480-8b8530ea-5828-4c5a-afdd-e66bdd26db98.jpg";
    list.add(album);
    album = new Album();
    album.name = "Bonnie Raitt, 'Dig In Deep'";
    album.thumbnail = "http://img.wennermedia.com/660-width/bonnieraittdig-2e906252-7312-483d-8e20-9afad1fa0920.jpg";
    list.add(album);
    album = new Album();
    album.name = "Metallica, 'Hardwired… to Self-Destruct'";
    album.thumbnail = "http://img.wennermedia.com/660-width/metallica-e00df7a2-08c9-43a3-9863-076e8158c566.jpg";
    list.add(album);
    album = new Album();
    album.name = "Anohni, 'Hopelessness'";
    album.thumbnail = "http://img.wennermedia.com/660-width/a1895762218_10-38143362-6beb-418d-9056-b1381c8e98e8.jpg";
    list.add(album);
    return list;
  }

}
