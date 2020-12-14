package me.d3x.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import me.d3x.mobileapp.data.QUser;
import me.d3x.mobileapp.util.Qtify;
import me.d3x.mobileapp.util.Qutils;


public class MainActivity extends AppCompatActivity {

    private AuthenticationRequest.Builder builder;

    private void launchLogin(){
        Qtify.getInstance().retrieveStoredUser();
        if(Qtify.getInstance().getUser() == null) {
            AuthenticationRequest request = builder.build();
            AuthenticationClient.openLoginActivity(this, 1337, request);
        }else{
            launchRoom();
        }
    }

    private void launchSearch(String query){
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    private void launchRoom(){
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Qtify.getInstance().setActivity(this);
        Qtify.getInstance().setReqQueue(Volley.newRequestQueue(this));
        //TODO: change verbose value as needed
        Qtify.getInstance().setVerbose(true);
        Qutils.initHandshake(getString(R.string.D3X_HANDSHAKE), ()->{
            try {
                builder = new AuthenticationRequest.Builder(
                        Qtify.getInstance().getAPICreds().getString("client_id"), AuthenticationResponse.Type.CODE, getString(R.string.S_REDIRECT_URI))
                        .setScopes(new String[]{"user-read-private", "user-read-email", "user-read-playback-state", "user-modify-playback-state"})
                        .setCampaign(Qtify.getInstance().getAPICreds().getString("client_secret"));
                getString(R.string.D3X_HANDSHAKE);
            }catch(JSONException e){e.printStackTrace();}
        });

        final Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener((v)->{
            launchLogin();
        });

        final Button searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                EditText songQ = (EditText)findViewById(R.id.songQuery);
                EditText roomNum = (EditText)findViewById(R.id.roomNumberInput);
                if(roomNum.getText().toString().length() == 0){
                    Qutils.alertDialog("SpecifyRoomNumber", "Please enter a room number first!");
                    return;
                }
                String query = songQ.getText().toString();
                if(query.length() == 0){
                    Qutils.alertDialog("EmptySearchQuery", "Enter a search query first!");
                    return;
                }
                int room = Integer.parseInt(roomNum.getText().toString());
                if(room <= 0){
                    Qutils.alertDialog("InvalidRoomNumber", "Please enter a valid room number first!");
                    roomNum.getText().clear();
                    return;
                }
                Qtify.getInstance().setRoomNumber(room);
                Qtify.getInstance().storeActiveUser();
                launchSearch(query);
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == 1337){
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch(response.getType()){
                case CODE:
                    String code = response.getCode();
                    Qutils.sendRequest(Qutils.makeRequest(Request.Method.POST,
                    "https://q.d3x.me/callback?redir=" + getString(R.string.S_REDIRECT_URI) + "&code=" + code,
                        (s) -> {
                            try {
                                JSONObject j = new JSONObject(s);
                                String message = j.getString("message");
                                int result = j.getInt("result");
                                if(result == 1) {
                                    Qtify.getInstance().setUser(new QUser(j.getString("user")));
                                    launchRoom();
                                }else{
                                    Log.e("InitBackendClientLogin", "Backend was unable to successfully complete authorization flow.");
                                    Log.e("InitBackendClientLogin", "Message from backend: " + message);
                                }
                            }catch(JSONException e){e.printStackTrace();}
                        }
                    ));
                    break;
                case ERROR:
                    Log.e("InitConnectionToBackend", "Error initializing On-App Android Spotify auth flow.");
                    break;
                default:
                    Log.e("InitConnectionToBackend", "Unhandled response type on onActivityResult: " + response.getType());
                    Log.e("InitConnectionToBackend", "(Probably just a user exiting the login screen)");
            }
        }
    }
}