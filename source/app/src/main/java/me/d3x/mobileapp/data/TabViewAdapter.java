package me.d3x.mobileapp.data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import me.d3x.mobileapp.AccountActivity;
import me.d3x.mobileapp.R;
import me.d3x.mobileapp.util.QGui;
import me.d3x.mobileapp.util.Qtify;
import me.d3x.mobileapp.util.Qutils;

public class TabViewAdapter extends RecyclerView.Adapter<TabViewAdapter.ViewHolder>{


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(Qtify.getInstance().getTabLayout(), parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //TODO: swapping tabs occasionally reduces list length to shorter list (request cache vs blocklist)
        //race condition?
        Log.e("TabViewAdapter onBindViewHolder", "Position: " + position);
        switch(position){
            case 0:
                Qtify.getInstance().getUser().getSongRequests((l)->{
                    holder.init(position);
                });
                break;
            case 1:
                Qtify.getInstance().getUser().getBlocklist((l)->{
                    holder.init(position);
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return AccountActivity.tabText.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView recyclerView;
        private ConstraintLayout accView;
        private SwipeRefreshLayout swipeContainer;

        public ViewHolder(View item) {
            super(item);
            recyclerView = item.findViewById(R.id.recyclerview);
            swipeContainer = item.findViewById(R.id.swipeContainer);
            accView = item.findViewById(R.id.accountView);
        }

        public void init(int position){
            if(recyclerView != null && swipeContainer != null){
                QGui.initSwipeView(recyclerView, swipeContainer, Qtify.getInstance().getActivity());
                swipeContainer.setOnRefreshListener(()->{
                    //we only want to allow refreshing the user's room's requests,
                    //so return if we've refreshed within the last 5 seconds
                    if(!Qtify.getInstance().canRefresh()) {
                        swipeContainer.setRefreshing(false);
                        return;
                    }
                    Qtify.getInstance().refreshLock(5000, ()->{
                        switch(position){
                            case 0:
                                Qtify.getInstance().getUser().getSongRequests((t)->{
                                    Qtify.getInstance().getAdapter().notifyDataSetChanged();
                                    swipeContainer.setRefreshing(false);
                                });
                                break;
                            case 1:
                                Qtify.getInstance().getUser().getBlocklist((t)->{
                                    Qtify.getInstance().getAdapter().notifyDataSetChanged();
                                    swipeContainer.setRefreshing(false);
                                });
                                break;
                        }
                    });
                });

            }
        }
    }
}
