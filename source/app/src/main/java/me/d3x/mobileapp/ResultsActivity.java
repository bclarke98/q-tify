package me.d3x.mobileapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import me.d3x.mobileapp.data.QSong;
import me.d3x.mobileapp.data.SongViewAdapter;
import me.d3x.mobileapp.util.QGui;
import me.d3x.mobileapp.util.Qtify;
import me.d3x.mobileapp.util.Qutils;

public class ResultsActivity extends AppCompatActivity {

    private String mQuery;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Qtify.getInstance().setActivity(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        mQuery = getIntent().getStringExtra("query");

        if(savedInstanceState != null){
            //returns null ONLY on the initial creation of activity
            init(null);
        }else {
            if (mQuery == null)
                if(Qtify.getInstance().getListSource() == Qutils.ListSource.USER_BLOCKED)
                    Qtify.getInstance().getUser().getBlocklist(this::init);
                else
                    Qtify.getInstance().getUser().getSongRequests(this::init);
            else
                Qutils.c_searchSong(mQuery, this::init);
        }
    }

    protected void init(List<QSong> l){
        QGui.initSwipeView(recyclerView, swipeContainer, this);
        swipeContainer.setOnRefreshListener(()->{
            //we only want to allow refreshing the user's room's requests,
            //so return if we've refreshed within the last 5 seconds OR
            //if we're searching for a song to request (no need to refresh the search results)
            if(!Qtify.getInstance().canRefresh() || mQuery != null) {
                swipeContainer.setRefreshing(false);
                return;
            }
            Qtify.getInstance().refreshLock(5000, ()->{
                Qtify.getInstance().getUser().getSongRequests((t)->{
                    Qtify.getInstance().getAdapter().notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                });
            });
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }
}