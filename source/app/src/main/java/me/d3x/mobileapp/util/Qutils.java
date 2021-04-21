package me.d3x.mobileapp.util;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import me.d3x.mobileapp.R;
import me.d3x.mobileapp.data.QSong;
import me.d3x.mobileapp.data.RequestDialog;

public class Qutils {

    public enum ListSource {
        NO_DATA,
        SONG_SEARCH,
        USER_SONGS,
        USER_BLOCKED
    }

    public static class ApiResponse{
        private String msg;
        private boolean result;
        public ApiResponse(boolean result, String msg){
            this.result = result;
            this.msg = msg;
        }

        public ApiResponse(JSONObject j) throws JSONException{
            this(j.getInt("result") > 0, j.getString("message"));
        }

        public String getMsg() {
            return msg;
        }
        public boolean getResult() {
            return result;
        }

        public String dump(){ return "Result: " + result + "\nMessage: '" + msg.trim() + "'"; }
        public void toLog(String tag) { if(Qtify.getInstance().isVerbose()){ Log.e(tag, this.dump()); } }
    }

    public static final QSong rickRoll = new QSong(
            "Never Gonna Give You Up", "spotify:track:7GhIk7Il098yCjg4BQjzvb",
            "Rick Astley",
            "Whenever You Need Somebody",
            "https://i.scdn.co/image/ab67616d0000b273237665d08de01907e82a7d8a");

    public static final ArrayList<QSong> d_songList = new ArrayList<QSong>(Arrays.asList(rickRoll));

    public static StringRequest generateRequest(int type, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, Map<String, String> headers, String requestBody){
        final Map<String, String> hdr = (headers == null) ? new HashMap<String, String>() : new HashMap<String, String>(headers);
        StringRequest req = new StringRequest(type, url, listener, errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //add any params
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> h = new HashMap<String, String>(super.getHeaders());
                if(!hdr.isEmpty()) {
                    h.putAll(hdr);
                }
                return h;
            }
            @Override
            public String getBodyContentType(){
                return (type == Method.POST) ? "application/json; charset=utf-8" : "text/plain; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws  AuthFailureError {
                try{
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                }catch(UnsupportedEncodingException e){
                    Log.e("VolleyRequestGetBody",
                            "Unsupported Encoding while trying to get bytes of '" + requestBody + "' using UTF-8");
                    return null;
                }
            }
        };
        req.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        return req;
    }

    public static StringRequest generateRequest(int type, String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        return generateRequest(type, url, listener, errorListener, null, null);
    }

    public static StringRequest makeRequest(int type, String url, Consumer<String> onResp, Consumer<VolleyError> onVolleyError, Map<String, String> headers, String requestBody){
        return generateRequest(type, url, onResp::accept, error -> {
            if(onVolleyError != null)
                onVolleyError.accept(error);
            else
                Log.e("VolleyRequestQueue", "VolleyError Thrown: " + error.toString());
        },
                headers,
                requestBody
        );
    }
    /*
    // Lambdas are cool, method references are cooler. See above makeRequest function to see how much
    // cleaner they made the below function.

    public static StringRequest makeRequest(int type, String url, Consumer<String> onResp, Consumer<VolleyError> onVolleyError, Map<String, String> headers, String requestBody){
        return generateRequest(type, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onResp.accept(response);
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(onVolleyError != null)
                            onVolleyError.accept(error);
                        else
                            Log.e("VolleyRequestQueue", "VolleyError Thrown: " + error.toString());
                    }
                },
                headers,
                requestBody
        );
    }
    */
    public static StringRequest makeRequest(int type, String url, Consumer<String> onResp, Map<String, String> headers, String requestBody){
        return makeRequest(type, url, onResp, null, headers, requestBody);
    }

    public static StringRequest makeRequest(int type, String url, Consumer<String> onResp){
        return makeRequest(type, url, onResp, null, null, null);
    }

    public static void sendRequest(StringRequest request){
        Qtify.getInstance().getQueue().add(request);
    }

    public static StringRequest postRequest(String url, Consumer<String> onResp, Consumer<VolleyError> onVolleyError, Map<String, String> headers, String requestBody){
        return makeRequest(Request.Method.POST, url, onResp, onVolleyError, headers, requestBody);
    }

    public static StringRequest postRequest(String url, Consumer<String> onResp, Consumer<VolleyError> onVolleyError){
        return makeRequest(Request.Method.POST, url, onResp, onVolleyError, null, null);
    }

    public static StringRequest postRequest(String url, Consumer<String> onResp, Map<String, String> headers, String requestBody){
        return postRequest(url, onResp, null, headers, requestBody);
    }

    public static StringRequest postRequest(String url, Consumer<String> onResp, String reqestBody){
        return postRequest(url, onResp, null, null, reqestBody);
    }

    public static StringRequest getRequest(String url, Consumer<String> onResp, Consumer<VolleyError> onVolleyError, Map<String, String> headers){
        return makeRequest(Request.Method.GET, url, onResp, onVolleyError, headers, null);
    }

    public static StringRequest getRequest(String url, Consumer<String> onResp, Map<String, String> headers){
        return getRequest(url, onResp, null, headers);
    }

    public static StringRequest getRequest(String url, Consumer<String> onResp){
        return getRequest(url, onResp, null);
    }

    public static void initHandshake(String uid, Runnable callback){
        if(Qtify.getInstance().getAPICreds().length() > 0) {
            if (callback != null)
                callback.run();
            return;
        }
        sendRequest(getRequest("https://q.d3x.me/handshake/" + uid,
            (s)->{
                try{
                    JSONObject j = new JSONObject(s);
                    if(j.getInt("result") > 0){
                        Qtify.getInstance().cacheHandshakeResults(j);
                        callback.run();
                    }
                }catch (JSONException e){e.printStackTrace();}
            }
        ));
    }

    public static List<QSong> searchSong(String query, Consumer<List<QSong>> callback){
        List<QSong> l = new ArrayList<>();
        sendRequest(getRequest("https://q.d3x.me/search/" + query, (s)->{
            try {
                JSONArray ja = new JSONArray(s);
                for(int i = 0; i < ja.length(); i++)
                    l.add(new QSong(ja.getJSONObject(i)));
                Qtify.getInstance().cacheSongs(l, false);
                if(callback != null)
                    callback.accept(l);
            } catch (JSONException e) { e.printStackTrace(); }
        }));
        return l;
    }

    public static List<QSong> c_searchSong(String query, boolean clearFirst, Consumer<List<QSong>>callback){
        return Qtify.getInstance().cacheSongs(searchSong(query, callback), clearFirst);
    }

    public static List<QSong> c_searchSong(String query, boolean clearFirst){
        return Qtify.getInstance().cacheSongs(searchSong(query, null), clearFirst);
    }

    public static List<QSong> c_searchSong(String query, Consumer<List<QSong>> callback){
        return c_searchSong(query, true, callback);
    }

    public static List<QSong> c_searchSong(String query){
        return c_searchSong(query, true);
    }

    public static void confirmDialog(String tag, String title, String yesText, String noText, Consumer<String> onConfirm){
        RequestDialog confirmFrag = new RequestDialog(title, yesText, noText);
        confirmFrag.setOnConfirm(onConfirm);
        confirmFrag.show(Qtify.getInstance().getActivity().getSupportFragmentManager(), tag);
    }

    public static void confirmDialog(String tag, String title, Consumer<String> onConfirm){
        confirmDialog(tag, title, "Confirm", "Cancel", onConfirm);
    }

    public static void alertDialog(String tag, String title){
        confirmDialog(tag, title, "Dismiss", null, (t)->{});
    }

}
