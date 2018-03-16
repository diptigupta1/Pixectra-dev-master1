package com.pixectra.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pixectra.app.CroppingActivity;
import com.pixectra.app.R;

import java.util.Vector;

/**
 * Created by XCODER on 2/24/2018.
 */

public class CartImagesAdapter extends RecyclerView.Adapter<CartImagesAdapter.ViewHolder> {

    Context context;
    Vector<Bitmap> data;
    int position;

    public CartImagesAdapter(Context context, Vector<Bitmap> data, int pos) {
        this.context = context;
        this.data = data;
        position = pos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_image_item, parent, false);
        return new CartImagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context).load(data.get(position)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.getLayoutParams().width = (int) context.getResources().getDimension(R.dimen.selected_images_dimen);
            itemView.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.selected_images_dimen);
            image = itemView.findViewById(R.id.ListIcon);
            itemView.findViewById(R.id.image_loading_progress).setVisibility(View.GONE);
            image.setOnClickListener(this);
            itemView.findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    data.remove(getAdapterPosition());
                    CartImagesAdapter.this.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, CroppingActivity.class);
            intent.putExtra("cart", true);
            intent.putExtra("position", position);
            intent.putExtra("index", getAdapterPosition());
            context.startActivity(intent);

        }
    }
}
