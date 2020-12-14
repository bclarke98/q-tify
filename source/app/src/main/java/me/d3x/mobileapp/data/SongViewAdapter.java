package me.d3x.mobileapp.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import me.d3x.mobileapp.util.Qtify;
import me.d3x.mobileapp.R;
import me.d3x.mobileapp.util.Qutils;

public class SongViewAdapter extends RecyclerView.Adapter<SongViewAdapter.SongViewHolder>{

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public Button reqBtn, approvebtn, denyBtn, blockBtn, unblockBtn;
        public TextView desc;
        public SongViewHolder(View item){
            super(item);
            this.img = (ImageView)item.findViewById(R.id.albumIcon);
            this.reqBtn = (Button) item.findViewById(R.id.requestBtn);
            this.approvebtn = (Button) item.findViewById(R.id.approveBtn);
            this.denyBtn = (Button) item.findViewById(R.id.denyBtn);
            this.blockBtn = (Button) item.findViewById(R.id.blockBtn);
            this.unblockBtn = (Button) item.findViewById(R.id.unblockBtn);
            this.desc = (TextView)item.findViewById(R.id.songDetails);
        }
    }

    @Override
    public SongViewAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View songView = inflater.inflate(Qtify.getInstance().getCardLayout(), parent, false);

        return new SongViewHolder(songView);
    }

    private void removeAndNotify(int position, boolean flag){
        if(!flag){ return; }
        Qtify.getInstance().getSongCache().remove(position);
        this.notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(SongViewAdapter.SongViewHolder holder, int position) {
        QSong s = Qtify.getInstance().getSongCache().get(position);
        holder.desc.setText(s.getDesc());
        Picasso.get().load(s.getAlbumart()).into(holder.img);
        /* SONG_SEARCH */
        if(holder.reqBtn != null) {
            holder.reqBtn.setOnClickListener((v) -> {
                Qutils.confirmDialog(
                        "ConfirmSongRequest",
                        "Request \"" + s.getDesc() + "\"?",
                        (t) -> {
                            s.request();
                        }
                );
            });
        }
        /* USER_SONG */
        if(holder.denyBtn != null && holder.approvebtn != null){
            holder.denyBtn.setOnClickListener((v)->{
                Qutils.confirmDialog(
                    "DenySongRequest",
                    "Remove \"" + s.getDesc() + "\" from request list?",
                    (t) -> {
                        Qtify.getInstance().getUser().denySong(s, (b)->{
                            removeAndNotify(position, b);
                        });
                    }
                );
            });
            holder.approvebtn.setOnClickListener((v)->{
                Qutils.confirmDialog(
                    "ApproveSongRequest",
                    "Add \"" + s.getDesc() + "\" to your Spotify queue?",
                    (t) -> {
                        Qtify.getInstance().getUser().approveSong(s, (b)->{
                            removeAndNotify(position, b);
                        });
                    }
                );
            });
            holder.blockBtn.setOnClickListener((v)->{
                Qutils.confirmDialog(
                        "BlockSongRequest",
                        "Add \"" + s.getDesc() + "\" to your blocklist?",
                        (t) -> {
                            Qtify.getInstance().getUser().blockSong(s, (b)->{
                                removeAndNotify(position, b);
                            });
                        }
                );
            });
        }
        /* USER_BLOCKED */
        if(holder.unblockBtn != null){
            holder.unblockBtn.setOnClickListener((v)->{
                Qutils.confirmDialog(
                        "UserUnblockSong",
                        "Remove \"" + s.getDesc() + "\" from your blocklist?",
                        (t)->{
                            Qtify.getInstance().getUser().unblockSong(s, (b)->{
                                removeAndNotify(position, b);
                            });
                        }
                );
            });
        }

    }

    @Override
    public int getItemCount() {
        return Qtify.getInstance().getSongCache().size();
    }



}
