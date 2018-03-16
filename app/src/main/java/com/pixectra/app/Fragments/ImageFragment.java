package com.pixectra.app.Fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.pixectra.app.Adapter.ImageSelectAdapter;
import com.pixectra.app.FacebookActivity;
import com.pixectra.app.Instagram.ApplicationData;
import com.pixectra.app.Instagram.InstagramApp;
import com.pixectra.app.Models.Images;
import com.pixectra.app.R;
import com.pixectra.app.Utils.AlbumActivity;
import com.pixectra.app.Utils.Function;
import com.pixectra.app.Utils.GlideHelper;
import com.pixectra.app.Utils.MapComparator;
import com.pixectra.app.Utils.SessionHelper;
import com.pixectra.app.Utils.VolleyQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ImageFragment extends Fragment {

    public static final String TAG_DATA = "data";
    public static final String TAG_IMAGES = "images";
    public static final String TAG_THUMBNAIL = "thumbnail";
    public static final String TAG_URL = "url";
    static final int REQUEST_PERMISSION_KEY = 1;
    private static final int RC_AUTHORIZE_PICASA = 2;
    private static final int RC_SIGN_IN = 1;
    ImageView noLoginView;
    int category;
    String key;
    InstagramApp mApp;//Instagram
    List<Images> imageData;
    GraphResponse lastGraphResponse;
    LoadAlbum loadAlbumTask;
    /*
      <-- Images from device
    */
    AlbumAdapter albumAdapter;
    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
    int maxPics, minPics;
    private RecyclerView recyclerView;
    private ImageSelectAdapter adapter;
    public ImageFragment() {
    }

    public static ImageFragment newInstance(int type) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        // args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category = getArguments().getInt("type", -1);

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        key = getActivity().getIntent().getStringExtra("key");
        maxPics = getActivity().getIntent().getIntExtra("maxPics", 0);
        minPics = getActivity().getIntent().getIntExtra("minPics", 0);
        View layout = inflater.inflate(R.layout.image_select_fragment, null);
        imageData = new ArrayList<>();
        recyclerView = layout.findViewById(R.id.Imagelist);
        noLoginView = layout.findViewById(R.id.no_login_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        albumAdapter = new AlbumAdapter(getActivity(), albumList);
        adapter = new ImageSelectAdapter(getActivity(), key, getActivity().getIntent().getIntExtra("maxPics", 0), imageData);
        //imagegrid = layout.findViewById(R.id.PhoneImageGrid);
          // selectBtn = layout.findViewById(R.id.selectBtn);


        //<--For first fragment
        if (category == 0) {
            recyclerView.setAdapter(albumAdapter);
        }else{
            recyclerView.setAdapter(adapter);
        }
        checkAndLoadData();

        return layout;

    }


    /**
     * Main Driver Function Checks And Fetch Data from Different Places
     */
    void checkAndLoadData() {
        switch (category) {
            case 0: //Device Photos

                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)//Check For permission if not given show button
                    userLoggedIn(false);
                else {

                    loadAlbumTask = new LoadAlbum();
                    loadAlbumTask.execute();

                }
                break;
            case 1://facebook


                if (AccessToken.getCurrentAccessToken() == null)
                    userLoggedIn(false);
                else {
                    userLoggedIn(true);
                    Profile profile = Profile.getCurrentProfile();
                    if (profile != null) {
                        getFacebookImages(profile);
                    }
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (!recyclerView.canScrollVertically(1)) {
                                getNextImages(lastGraphResponse);
                            }
                        }
                    });
                }
                break;
            case 2:

                mApp = new InstagramApp(getActivity(), ApplicationData.CLIENT_ID,
                        ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);

                if(!mApp.hasAccessToken()) {
                    // User Is Not Logged In
                    userLoggedIn(false);   // Display Sign In Button set true to remove button

                    mApp.setListener( new InstagramApp.OAuthAuthenticationListener() {

                        @Override
                        public void onSuccess() {
                            // tvSummary.setText("Connected as " + mApp.getUserName());
                            userLoggedIn(true);
                            getInstagramImages();
                        }

                        @Override
                        public void onFail(String error) {
                            Toast.makeText(getActivity(), "Connot Load Images", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }else{
                    getInstagramImages();

                    //TODO:LOAD DATA

                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {  //Load More Data on scroll
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (!recyclerView.canScrollVertically(1)) {
                                Toast.makeText(getActivity(), "Api In Sandbox mode 20 only", Toast.LENGTH_SHORT)
                                        .show();
                            }

                        }
                    });
                }
                break;
            case 3:  //Google Photos
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
                if (account == null) {  // User Is Not Logged In
                    userLoggedIn(false);              // Display Sign In Button set true to remove button
                } else {
                    //TODO:LOAD DATA
                    if(checkForPicasaAuthPermission())
                        fetchOauthAccessToken(account);
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {  //Load More Data on scroll
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                        }
                    });
                }
                break;

        }
    }

    private boolean checkForPicasaAuthPermission() {
        Scope SCOPE_PICASA = new Scope("https://picasaweb.google.com/data/");
        String scope = "oauth2:https://picasaweb.google.com/data/";
        Context context = getActivity();
        if (!GoogleSignIn.hasPermissions(
                GoogleSignIn.getLastSignedInAccount(context),
                SCOPE_PICASA)) {
            GoogleSignIn.requestPermissions(
                    getActivity(),
                    RC_AUTHORIZE_PICASA,
                    GoogleSignIn.getLastSignedInAccount(context),
                    SCOPE_PICASA);
            return false;
        } else {
            return true;
        }
    }

    private void fetchOauthAccessToken(final GoogleSignInAccount account)
    {
        GetOAuthAccessTokenTask getOAuthAccessTokenTask =
                new GetOAuthAccessTokenTask(account);
        getOAuthAccessTokenTask.execute();

    }

    public void getImagesData(final String accessToken)
    {
        String url = "https://picasaweb.google.com/data/feed/api/user/default?kind=photo&access=all&max-results=100&alt=json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            getPicasaImages(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String errormsg = new String(error.networkResponse.data, "UTF-8");
                    Log.v("Picassa Error", errormsg);
                    Toast.makeText(getActivity(), errormsg, Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params ;//=  super.getHeaders();
                //if(params==null)
                params = new HashMap<>();
                params.put("Authorization","Bearer " + accessToken);
                params.put("GData-Version", "3");
                return params;
            }
        };

        VolleyQueue.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    private void getPicasaImages(JSONObject response) throws JSONException {
        Log.v("response", response.toString());
        JSONArray entries = response.getJSONArray("entry");
        int l = entries.length();
        for (int i = 0; i < l; i++) {
            String imageUrl = entries.getJSONObject(i).getJSONObject("content").getString("src");
            String thumbnailUrl = entries.getJSONObject(i).getJSONArray("media:thumbnail")
                    .getJSONObject(0).getString("url");
            imageData.add(new Images(imageUrl, thumbnailUrl));
        }
        adapter.notifyDataSetChanged();
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//
//        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//        if (!Function.hasPermissions(getActivity(), PERMISSIONS)) {
//            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_PERMISSION_KEY);
//        } else {
//
//        }
//
//    }

    /**
     * Parse Response And Add To List
     *
     * @param response
     */
    void parseFacebookJson(GraphResponse response) {
        JSONObject main = response.getJSONObject();
        try {
            JSONArray data = main.getJSONArray("data");
            int n = data.length();
            for (int i = 0; i < n; i++) {
                JSONObject temp = data.getJSONObject(i);
                JSONArray images = temp.getJSONArray("images");
                int imgs = images.length();
                for (int j = 0; j < imgs; j++) {
                    JSONObject img = images.getJSONObject(j);
                    Images image = null;
                    if (j == 0) {
                        image = new Images();
                        image.setUrl(img.getString("source"));
                        image.setThumbnail(images.getJSONObject(imgs - 1).getString("source"));
                        imageData.add(image);
                    }

                }
            }
            lastGraphResponse = response;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }


//<---Methods and classes for first fragment------------------END-----------------------------------

    /**
     * Fetch Images From Facebook Make Request
     *
     * @param profile
     */
   void getFacebookImages(Profile profile){
        Bundle params  = new Bundle();
        Log.v("permissions",AccessToken.getCurrentAccessToken().getPermissions().toString());
        params.putString("fields","images");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + profile.getId() + "/photos",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        parseFacebookJson(response);
                    }
                }
        ).executeAsync();
    }

    /**
     * Get More Images When User Scrolls
     *
     * @param lastGraphResponse
     */
    void getNextImages(GraphResponse lastGraphResponse) {
        if (lastGraphResponse != null) {
            GraphRequest nextResultsRequests = lastGraphResponse.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
            if (nextResultsRequests != null) {
                nextResultsRequests.setCallback(new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        parseFacebookJson(response);
                    }
                });
                nextResultsRequests.executeAsync();
            } else {
                Toast.makeText(getActivity(), "No Results To Load", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Display Buttons Of Respective Fragments
     *
     * @param status true if logged in else false
     */
    void userLoggedIn(boolean status) {
        if (status) {
            recyclerView.setVisibility(View.VISIBLE);
            noLoginView.setOnClickListener(null);
            noLoginView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noLoginView.setVisibility(View.VISIBLE);
            setImage();
            if (category == 0) {
                noLoginView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSION_KEY);
                    }
                });
            }

            if (category == 1) {
                noLoginView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), FacebookActivity.class);
                        intent.putExtra("auth", false);
                        startActivityForResult(intent, 3);
                    }
                });
            }

            if (category == 2){
                noLoginView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mApp.authorize();
                    }
                });
            }
            if (category == 3) {
                noLoginView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Scope SCOPE_PICASA = new Scope("https://picasaweb.google.com/data/");
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestScopes(SCOPE_PICASA)
                                .requestIdToken(getString(R.string.google_token))
                                .requestEmail()
                                .build();

                        googleSignIn(GoogleSignIn.getClient(getActivity(), gso));
                    }
                });

            }
        }
    }

    void googleSignIn(GoogleSignInClient mGoogleSignInClient) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    /**
     * Set Login Button Image As Per Fragment
     */
    void setImage() {
        switch (category) {
            case 0:
                noLoginView.setImageResource(R.drawable.allow);
                break;
            case 1:
                noLoginView.setImageResource(R.drawable.loginwithfb);
                break;
            case 2:
                noLoginView.setImageResource(R.drawable.instasignin);
                break;
            case 3:
                noLoginView.setImageResource(R.drawable.loginwithgp);

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_KEY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    userLoggedIn(true);
                    loadAlbumTask = new LoadAlbum();
                    loadAlbumTask.execute();
                } else {
                    Toast.makeText(getActivity(), "You must accept permissions.", Toast.LENGTH_LONG).show();
                }
                break;


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                checkAndLoadData();
            }

            if (requestCode == RC_SIGN_IN) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
                if (account == null)
                    return;
                userLoggedIn(true);
                if (checkForPicasaAuthPermission())
                    fetchOauthAccessToken(account);
            }
            if (requestCode == RC_AUTHORIZE_PICASA) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
                userLoggedIn(true);
                if (account == null)
                    return;
                if (checkForPicasaAuthPermission())
                    fetchOauthAccessToken(account);
            }
        }
    }

    private void getInstagramImages() {
      // ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Loading images...");
        String url="https://api.instagram.com/v1/users/"
                + new SessionHelper(getActivity()).getId()
                + "/media/recent/?client_id="
                + ApplicationData.CLIENT_ID
                +"&access_token="
                +new SessionHelper(getActivity()).getAccessToken();
        StringRequest jsonRequest = new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("response",response);
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(response);
                    parseInstagramJson(jsonObject);
                } catch (JSONException e) {
                        e.printStackTrace();
                    }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                String message = "Cannot Complete Request Kindly Contact Us";
                if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (volleyError instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

            }
        });



        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonRequest.setRetryPolicy(policy);

        VolleyQueue.getInstance(getActivity()).getRequestQueue().add(jsonRequest);

    }

    void parseInstagramJson(JSONObject jsonObject) throws JSONException {
        JSONArray data = jsonObject.getJSONArray(TAG_DATA);
        for (int data_i = 0; data_i < data.length(); data_i++) {
            JSONObject data_obj = data.getJSONObject(data_i);

            JSONObject images_obj = data_obj
                    .getJSONObject(TAG_IMAGES);

            JSONObject thumbnail_obj = images_obj
                    .getJSONObject(TAG_THUMBNAIL);
            JSONObject standard=images_obj
                    .getJSONObject("standard_resolution");
            // String str_height =
            // thumbnail_obj.getString(TAG_HEIGHT);
            //
            // String str_width =
            // thumbnail_obj.getString(TAG_WIDTH);

            String thumb = thumbnail_obj.getString(TAG_URL);
            String stand = standard.getString(TAG_URL);
            imageData.add(new Images(stand,thumb));
        }
    }

    //<---Methods and classes for first fragment------------------------------START-----------------
    private class LoadAlbum extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            String path = null;
            String album = null;
            String timestamp = null;
            String countPhoto = null;
            Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;


            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};
            Cursor cursorExternal = getActivity().getContentResolver().query(uriExternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);
            Cursor cursorInternal = getActivity().getContentResolver().query(uriInternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});

            while (cursor.moveToNext()) {

                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                countPhoto = Function.getCount(getApplicationContext(), album);


                Log.v("album", path + "," + album + "," + timestamp + "," + countPhoto);
                if (timestamp == null) {
                    timestamp = "1518331969";
                }

                albumList.add(Function.mappingInbox(album, path, timestamp, Function.converToTime(timestamp), countPhoto));
            }
            cursor.close();
            Collections.sort(albumList, new MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            albumAdapter.notifyDataSetChanged();
        }
    }

    class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
        int w;
        private Activity activity;
        private ArrayList<HashMap<String, String>> data;
        public AlbumAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
            activity = a;
            data = d;
            DisplayMetrics dm = new DisplayMetrics();
            a.getWindowManager().getDefaultDisplay().getMetrics(dm);
            w = (dm.widthPixels / 3) - (int) (a.getResources().getDimension(R.dimen.image_cell_padding) * 3);
        }


        @Override
        public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_row, parent, false);
            return new AlbumViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AlbumViewHolder holder, int position) {
            HashMap<String, String> song = data.get(position);
            holder.gallery_title.setText(song.get(Function.KEY_ALBUM));
            holder.gallery_count.setText(song.get(Function.KEY_COUNT));
            GlideHelper.load(activity, new File(song.get(Function.KEY_PATH)), holder.galleryImage, null, 200, 200);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        class AlbumViewHolder extends RecyclerView.ViewHolder {
            ImageView galleryImage;
            TextView gallery_count, gallery_title;

            public AlbumViewHolder(View convertView) {
                super(convertView);
                galleryImage = convertView.findViewById(R.id.galleryImage);
                gallery_count = convertView.findViewById(R.id.gallery_count);
                gallery_title = convertView.findViewById(R.id.gallery_title);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlbumActivity fragment = new AlbumActivity();
                        Bundle bundle = new Bundle();
                        bundle.putString("name", albumList.get(+getAdapterPosition()).get(Function.KEY_ALBUM));
                        bundle.putString("key", key);
                        bundle.putInt("maxPics", maxPics);
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction;
                        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left,
                                R.anim.slide_out_left, R.anim.slide_in_left,
                                R.anim.slide_out_left
                        );
                        fragmentTransaction.replace(R.id.show_more, fragment, "albumactivity");
                        fragmentTransaction.addToBackStack("albumactivity");
                        fragmentTransaction.commit();


                    }
                });
                convertView.getLayoutParams().height = w;

            }
        }
    }

    public class GetOAuthAccessTokenTask extends AsyncTask<Void, Void, Void> {

        private GoogleSignInAccount account;
        private String accessToken;

        public GetOAuthAccessTokenTask(GoogleSignInAccount account) {
            this.account = account;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (accessToken != null)
                getImagesData(accessToken);
            else
                Log.e("Error Retrieving token", "Oauth token was not retrieved");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String scope = "oauth2:https://picasaweb.google.com/data/";
                accessToken = GoogleAuthUtil.getToken(getApplicationContext(), account.getAccount(), scope, new Bundle());
                Log.d("accessToken", accessToken);//accessToken:ya29.Gl...

            } catch (IOException | GoogleAuthException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
