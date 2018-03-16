package com.pixectra.app.Utils;

/**
 * Created by Suhail on 2/12/2018.
 */


import android.app.Activity;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pixectra.app.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;


public class AlbumActivity extends Fragment {
    RecyclerView galleryGridView;
    ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
    String album_name = "";
    LoadAlbumImages loadAlbumTask;
    SingleAlbumAdapter adapter;

    public AlbumActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        album_name = getArguments().getString("name");
        //setTitle(album_name);
        ((TextView) view.findViewById(R.id.toolbar_text)).setText(album_name);
        (view.findViewById(R.id.toolbar_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack("albumactivity", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        galleryGridView = view.findViewById(R.id.galleryGridView);
        galleryGridView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        adapter = new SingleAlbumAdapter(getActivity(), imageList);
        galleryGridView.setAdapter(adapter);
        loadAlbumTask = new LoadAlbumImages();
        loadAlbumTask.execute();
    }




    class LoadAlbumImages extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            String path = null;
            String album = null;
            String timestamp = null;
            Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};

            Cursor cursorExternal = getActivity().getContentResolver().query(uriExternal, projection, "bucket_display_name = \"" + album_name + "\"", null, null);
            Cursor cursorInternal = getActivity().getContentResolver().query(uriInternal, projection, "bucket_display_name = \"" + album_name + "\"", null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});
            while (cursor.moveToNext()) {

                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                if (timestamp == null) {
                    timestamp = "1518331969";
                }
                imageList.add(Function.mappingInbox(album, path, timestamp, Function.converToTime(timestamp), null));
            }
            cursor.close();
            Collections.sort(imageList, new MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            adapter.notifyDataSetChanged();
        }
    }


    class SingleAlbumAdapter extends RecyclerView.Adapter<SingleAlbumAdapter.SingleAlbumViewHolder> {
        int w;
        private Activity activity;
        private ArrayList<HashMap<String, String>> data;

        SingleAlbumAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
            activity = a;
            data = d;
            DisplayMetrics dm = new DisplayMetrics();
            (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(dm);
            w = (dm.widthPixels / 3) - (int) (AlbumActivity.this.getResources().getDimension(R.dimen.image_cell_padding) * 5);
        }

        @Override
        public SingleAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(activity).inflate(
                    R.layout.image_recycler_item, parent, false);
            return new SingleAlbumViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SingleAlbumViewHolder holder, int position) {
            HashMap<String, String> song = data.get(position);
            GlideHelper.load(activity, new File(song.get(Function.KEY_PATH))
                    , holder.galleryImage, holder.loader, 200, 200);
        }



        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {

            return data.size();
        }

        class SingleAlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView galleryImage, overlay;
            ProgressBar loader;

            SingleAlbumViewHolder(View itemView) {
                super(itemView);
                galleryImage = itemView.findViewById(R.id.ListIcon);
                overlay = itemView.findViewById(R.id.selected_view);
                loader = itemView.findViewById(R.id.image_loading_progress);
                itemView.getLayoutParams().height = w;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (CartHolder.getInstance().getSize(getArguments().getString("key", "")) < getArguments().getInt("maxPics", 0)) {
                    if (overlay.getVisibility() == View.INVISIBLE) {
                        loader.setVisibility(View.VISIBLE);
                        try {
                            GlideHelper.getBitmap(getActivity(), new File(data.get(getAdapterPosition()).get(Function.KEY_PATH)), new RequestListener() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                                    loader.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Unable To Fetch Full Size Image", Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                                    loader.setVisibility(View.GONE);
                                    overlay.setVisibility(View.VISIBLE);
                                    CartHolder.getInstance().addImage(getActivity().getIntent().getStringExtra("key"), (Bitmap) resource);
                                    return false;
                                }
                            });
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    Toast.makeText(activity, "Images Already Selected", Toast.LENGTH_SHORT).show();
                }
                if (overlay.getVisibility() == View.VISIBLE) {
                    overlay.setVisibility(View.INVISIBLE);
                    try {
                        GlideHelper.getBitmap(getActivity(), new File(data.get(getAdapterPosition()).get(Function.KEY_PATH)), new RequestListener() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                                CartHolder.getInstance().removeImage(getActivity().getIntent().getStringExtra("key"), (Bitmap) resource);
                                return false;
                            }
                        });
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}