package com.pixectra.app;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pixectra.app.Adapter.CartImagesAdapter;
import com.pixectra.app.Models.CheckoutData;
import com.pixectra.app.Models.Product;
import com.pixectra.app.Utils.CartHolder;
import com.pixectra.app.Utils.ImageController;

import java.util.Locale;

public class Checkout extends AppCompatActivity {
    CardView empty;
    RecyclerView recyclerView;
    CartAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        recyclerView = findViewById(R.id.cart_recyclerview);
        empty = findViewById(R.id.empty_view_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter();
        recyclerView.setAdapter(adapter);
        findViewById(R.id.cart_proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onplaceorder();
            }
        });
        toggleVisibility();
    }

    void toggleVisibility() {
        if (CartHolder.getInstance().getCart().size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
    }

    public void onplaceorder() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Checkout.this);
        View parentview = getLayoutInflater().inflate(R.layout.bottomsheet, null);
        bottomSheetDialog.setContentView(parentview);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) parentview.getParent());
        bottomSheetBehavior.setPeekHeight(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics()));
        bottomSheetDialog.show();
        final EditText editText = parentview.findViewById(R.id.couponedittext);
        final Button button = parentview.findViewById(R.id.applycoupn);
        TextView carttotal = parentview.findViewById(R.id.carttotal);
        TextView cartDiscount = parentview.findViewById(R.id.cartdiscount);
        TextView stategst = parentview.findViewById(R.id.stategst);
        TextView centralgst = parentview.findViewById(R.id.centralgst);
        TextView subtotal = parentview.findViewById(R.id.subtotal);
        TextView totalpayable = parentview.findViewById(R.id.totalpayable);
        Button cancel = parentview.findViewById(R.id.bottomsheet_cancel);
        Button pay = parentview.findViewById(R.id.bottomsheet_pay);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageController.placeOrder(new CheckoutData());
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {
        // ArrayList<String> keys;

        public CartAdapter() {
            //  keys=new ArrayList<>(CartHolder.getInstance().getCart().keySet());
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
            return new CartAdapter.VH(view);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            Product product = CartHolder.getInstance().getCart().get(position).first;
            holder.title.setText(String.format(Locale.getDefault(), "%s ( %d )", product.getType(), CartHolder.getInstance().getCart().get(position).second.size()));
            holder.type.setText(String.format(Locale.getDefault(), "%s@%d", product.getTitle(), product.getPrice()));
            Glide.with(Checkout.this).load(product.getUrl()).into(holder.titleImage);
            holder.images.setAdapter(new CartImagesAdapter(Checkout.this, CartHolder.getInstance().getCart().get(position).second, position));
        }

        @Override
        public int getItemCount() {
            return CartHolder.getInstance().getCart().size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView title, type;
            ImageView titleImage;
            RecyclerView images;

            public VH(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                type = itemView.findViewById(R.id.type_price);
                titleImage = itemView.findViewById(R.id.title_image);
                images = itemView.findViewById(R.id.images_cart);
                images.setLayoutManager(new LinearLayoutManager(Checkout.this, LinearLayoutManager.HORIZONTAL, false));
            }
        }
    }
}
