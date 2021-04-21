package me.d3x.mobileapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import me.d3x.mobileapp.data.TabViewAdapter;
import me.d3x.mobileapp.util.Qtify;
import me.d3x.mobileapp.util.Qutils;

public class AccountActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    public static final String[] tabText = {"Requests", "Blocklist"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Qtify.getInstance().setActivity(this);
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        viewPager = (ViewPager2)findViewById(R.id.accountPager);
        Qtify.getInstance().setPager(viewPager);
        final TextView greet = findViewById(R.id.greetText);
        greet.setText("Hi, " + Qtify.getInstance().getUser().getUsername());

        final ImageButton infoBtn = findViewById(R.id.infoBtn);
        infoBtn.setOnClickListener((v)->{
            Qutils.alertDialog("AlertAccountInfo", "Room number: " + Qtify.getInstance().getUser().getId());
        });
        final ImageButton helpBtn = findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener((v)->{
            //Qutils.alertDialog("AlertPagerStatus", "Position" + Qtify.getInstance().getPager().getCurrentItem());
            //show help view
        });
        final Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener((v)->{
            Qtify.getInstance().logout();
        });

        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager,
            (tab, position)->{
                tab.setText(tabText[position]);
            }
        );
        viewPager.setAdapter(new TabViewAdapter());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch(position){
                    case 0:
                        Qtify.getInstance().setListSource(Qutils.ListSource.USER_SONGS);
                        break;
                    case 1:
                        Qtify.getInstance().setListSource(Qutils.ListSource.USER_BLOCKED);
                        break;
                }
            }
        });
        mediator.attach();
    }

}
