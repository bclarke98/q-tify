package me.d3x.mobileapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import me.d3x.mobileapp.util.Qtify;
import me.d3x.mobileapp.util.Qutils;

public class QSong {

    private String name;
    private String uri;
    private String artist;
    private String album;
    private String albumart;

    public QSong(String name, String uri, String artist, String album, String albumart){
        this.name = name;
        this.uri = uri;
        this.artist = artist;
        this.album = album;
        this.albumart = albumart;
    }

    public QSong(JSONObject j) throws JSONException {
        this(j.getString("songname"), j.getString("songuri"), j.getString("artistname"), j.getString("albumname"), j.getString("albumart"));
    }

    public QSong(String s) throws JSONException{
        this(new JSONObject(s));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumart() {
        return albumart;
    }

    public void setAlbumart(String albumart) {
        this.albumart = albumart;
    }

    public String getDesc(){
        return this.name + " - " + this.artist;
    }

    public JSONObject asJSON(){
        JSONObject j = new JSONObject();
        try {
            j.put("songname", this.name);
            j.put("songuri", this.uri);
            j.put("artistname", this.artist);
            j.put("albumname", this.album);
            j.put("albumart", this.albumart);
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return j;
    }

    public void request(){
        Qutils.sendRequest(
            Qutils.postRequest(
            "https://q.d3x.me/request/" + Qtify.getInstance().getRoomNumber(),
                (s)->{
                    try{
                        JSONObject j = new JSONObject(s);
                        Qutils.ApiResponse rApi = new Qutils.ApiResponse(j);
                        Qutils.alertDialog("RequestSongResponse", rApi.getMsg());
                        rApi.toLog("SongRequestResponse");
                    }catch(JSONException e){e.printStackTrace();}
                },
                this.asJSON().toString()
            )
        );
    }
}
