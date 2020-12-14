package me.d3x.mobileapp.util;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import me.d3x.mobileapp.data.SongViewAdapter;

public class QGui {

    public static void initSwipeView(RecyclerView recyclerView, SwipeRefreshLayout swipeContainer, Context context){
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        Qtify.getInstance().setAdapter(new SongViewAdapter());
        //recyclerView.setHasFixedSize(true); //might need to remove
        recyclerView.setAdapter(Qtify.getInstance().getAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

}
