package com.pixectra.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pixectra.app.CroppingActivity;
import com.pixectra.app.R;
import com.pixectra.app.Utils.CartHolder;

/**
 * Created by XCODER on 2/24/2018.
 */

public class SelectedItemsAdapter extends RecyclerView.Adapter<SelectedItemsAdapter.ViewHolder> {

    Context context;
    String key;

    public SelectedItemsAdapter(Context context, String key) {
        this.context = context;
        this.key = key;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_image_item, parent, false);
        return new SelectedItemsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context).load(CartHolder.getInstance().getImage(key, position)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return CartHolder.getInstance().getSize(key);
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
                    CartHolder.getInstance().removeImage(key
                            , CartHolder.getInstance().getImage(key, getAdapterPosition()));
                }
            });
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, CroppingActivity.class);
            intent.putExtra("key", key);
            intent.putExtra("index", getAdapterPosition());
            context.startActivity(intent);

        }
    }
}
