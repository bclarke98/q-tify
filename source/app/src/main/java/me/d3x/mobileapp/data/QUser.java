package me.d3x.mobileapp.data;

import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import me.d3x.mobileapp.util.Qtify;
import me.d3x.mobileapp.util.Qutils;

public class QUser {
    private String spotifyid;
    private String refreshtoken;
    private String accesstoken;
    private List<QSong> requests;
    private List<QSong> blocklist;
    private int id;
    private int enabled;

    public QUser(String spotifyid, String refreshtoken, String accesstoken, int id){
        this.spotifyid = spotifyid;
        this.refreshtoken = refreshtoken;
        this.accesstoken = accesstoken;
        this.id = id;
        this.requests = new ArrayList<QSong>();
        this.blocklist = new ArrayList<QSong>();
    }

    public QUser(JSONObject j) throws JSONException {
        this.spotifyid = j.getString("spotifyid");
        this.refreshtoken = j.getString("refreshtoken");
        this.accesstoken = j.getString("accesstoken");
        this.id = j.getInt("id");
        this.requests = new ArrayList<QSong>();
        this.blocklist = new ArrayList<QSong>();
    }

    public QUser(String jsonString) throws JSONException {
        JSONObject j = new JSONObject(jsonString);
        this.spotifyid = j.getString("spotifyid");
        this.refreshtoken = j.getString("refreshtoken");
        this.accesstoken = j.getString("accesstoken");
        this.id = j.getInt("id");
        this.requests = new ArrayList<QSong>();
        this.blocklist = new ArrayList<QSong>();
    }

    public JSONObject getJSON() throws JSONException {
        return new JSONObject()
                .put("spotifyid", this.spotifyid)
                .put("refreshtoken", this.refreshtoken)
                .put("accesstoken", this.accesstoken)
                .put("id", this.id);
    }

    public String authBasic(){
        String creds = this.spotifyid + ":" + this.refreshtoken;
        return "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
    }

    public Map<String, String> authHeaders(){
        HashMap<String, String> h = new HashMap<String, String>();
        h.put("Authorization", this.authBasic());
        return h;
    }

    public int getId(){
        return this.id;
    }

    public String getUsername(){
        return this.spotifyid;
    }

    public int getEnabled() {
        return this.enabled;
    }

    public synchronized List<QSong> getCachedRequests(){
        return this.requests;
    }

    public synchronized List<QSong> getCachedBlocklist(){
        return this.blocklist;
    }

    public QUser copy() throws JSONException{
        QUser q = new QUser(this.getJSON());
        q.requests.addAll(this.requests);
        return q;
    }

    public QUser refcopy() throws JSONException{
        QUser q = new QUser(this.getJSON());
        q.requests = this.requests;
        return q;
    }

    public void syncUserState(Consumer<Boolean> callback){
        Qutils.sendRequest(
                Qutils.getRequest("https://q.d3x.me/api/dump_user",
                        (s) -> {
                            try{
                                JSONObject j = new JSONObject(s);
                                Qutils.ApiResponse rApi = new Qutils.ApiResponse(j);
                                JSONObject s_user = new JSONObject((rApi.getMsg()));
                                this.enabled = s_user.getInt("enabled");
                                if(callback != null)
                                    callback.accept(rApi.getResult());
                            }catch(JSONException e){e.printStackTrace();}
                        },
                        this.authHeaders()
                )
        );
    }
//----------------------------------------------------------------------------------------------------------

    public void toggleRoomLock(Consumer<Boolean> callback){
        if(this.enabled == 0)
            this.unlockRoom(callback);
        else
            this.lockRoom(callback);
    }

    public void lockRoom(Consumer<Boolean> callback){
        Qutils.sendRequest(
                Qutils.getRequest("https://q.d3x.me/api/disable_req",
                        (s) -> {
                            try{
                                JSONObject j = new JSONObject(s);
                                Qutils.ApiResponse rApi = new Qutils.ApiResponse(j);
                                if(rApi.getResult())
                                    this.enabled = 0;
                                rApi.toLog("UserRoomLocked");
                                if(callback != null)
                                    callback.accept(rApi.getResult());
                            }catch(JSONException e){e.printStackTrace();}
                        },
                        this.authHeaders()
                )
        );
    }

    public void unlockRoom(Consumer<Boolean> callback){
        Qutils.sendRequest(
                Qutils.getRequest("https://q.d3x.me/api/enable_req",
                        (s) -> {
                            try{
                                JSONObject j = new JSONObject(s);
                                Qutils.ApiResponse rApi = new Qutils.ApiResponse(j);
                                if(rApi.getResult())
                                    this.enabled = 1;
                                rApi.toLog("UserRoomUnlocked");
                                if(callback != null)
                                    callback.accept(rApi.getResult());
                            }catch(JSONException e){e.printStackTrace();}
                        },
                        this.authHeaders()
                )
        );
    }

    public void blockSong(QSong song, Consumer<Boolean> callback){
        Qutils.sendRequest(
                Qutils.getRequest("https://q.d3x.me/api/block_song?song=" + song.getUri(),
                        (s) -> {
                            try{
                                JSONObject j = new JSONObject(s);
                                Qutils.ApiResponse rApi = new Qutils.ApiResponse(j);
                                rApi.toLog("UserBlockSong");
                                if(callback != null)
                                    callback.accept(rApi.getResult());
                            }catch(JSONException e){e.printStackTrace();}
                        },
                        this.authHeaders()
                )
        );
    }

    public void unblockSong(QSong song, Consumer<Boolean> callback){
        Qutils.sendRequest(
                Qutils.getRequest("https://q.d3x.me/api/unblock_song?song=" + song.getUri(),
                        (s) -> {
                            try{
                                JSONObject j = new JSONObject(s);
                                Qutils.ApiResponse rApi = new Qutils.ApiResponse(j);
                                rApi.toLog("UserUnblockSong");
                                if(callback != null)
                                    callback.accept(rApi.getResult());
                            }catch(JSONException e){e.printStackTrace();}
                        },
                        this.authHeaders()
                )
        );
    }

    public void approveSong(QSong song, Consumer<Boolean> callback){
        Qutils.sendRequest(
            Qutils.getRequest("https://q.d3x.me/api/approve_song?song=" + song.getUri(),
                (s) -> {
                    try{
                        JSONObject j = new JSONObject(s);
                        Qutils.ApiResponse rApi = new Qutils.ApiResponse(j);
                        rApi.toLog("UserApproveSongResponse");
                        if(callback != null)
                            callback.accept(rApi.getResult());
                    }catch(JSONException e){e.printStackTrace();}
                },
                this.authHeaders()
            )
        );
    }

    public void denySong(QSong song, Consumer<Boolean> callback){
        Qutils.sendRequest(
            Qutils.getRequest("https://q.d3x.me/api/deny_song?song=" + song.getUri(),
                (s) -> {
                    try{
                        JSONObject j = new JSONObject(s);
                        Qutils.ApiResponse rApi = new Qutils.ApiResponse(j);
                        rApi.toLog("UserApproveSongResponse");
                        if(callback != null)
                            callback.accept(rApi.getResult());
                    }catch(JSONException e){e.printStackTrace();}
                },
                this.authHeaders()
            )
        );
    }

    public void getBlocklist(Consumer<List<QSong>> callback){
        Qutils.sendRequest(
                Qutils.getRequest("https://q.d3x.me/api/get_blocked_tracks",
                        (s) -> {
                            try{
                                List<QSong> l = new ArrayList<QSong>();
                                JSONArray ja = new JSONArray(new JSONObject(s).getString("message"));
                                for(int i = 0; i < ja.length(); i++)
                                    l.add(new QSong(ja.getString(i)));

                                this.blocklist.clear();
                                this.blocklist.addAll(l);
                                Qtify.getInstance().setListSource(Qutils.ListSource.USER_BLOCKED);
                                if(callback != null)
                                    callback.accept(l);
                            }catch(JSONException e){e.printStackTrace();}
                        },
                        this.authHeaders()
                )
        );

    }

    public void getSongRequests(Consumer<List<QSong>> callback){
        Qutils.sendRequest(
            Qutils.getRequest("https://q.d3x.me/api/get_requests",
                (s) -> {
                    try{
                        List<QSong> l = new ArrayList<QSong>();
                        JSONArray ja = new JSONArray(new JSONObject(s).getString("message"));
                        for(int i = 0; i < ja.length(); i++)
                            l.add(new QSong(ja.getString(i)));

                        this.requests.clear();
                        this.requests.addAll(l);
                        Qtify.getInstance().setListSource(Qutils.ListSource.USER_SONGS);
                        if(callback != null)
                            callback.accept(l);
                    }catch(JSONException e){e.printStackTrace();}
                },
                this.authHeaders()
            )
        );

    }

}
