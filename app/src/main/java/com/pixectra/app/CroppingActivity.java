package com.pixectra.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pixectra.app.Utils.CartHolder;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CroppingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropping);
        final CropImageView cropper = findViewById(R.id.cropImageView);
        if (getIntent().getBooleanExtra("cart", false)) {
            cropper.setImageBitmap(CartHolder.getInstance().getCart()
                    .get(getIntent().getIntExtra("position", 0))
                    .second.get(getIntent().getIntExtra("index", 0)));
        } else {
            cropper.setImageBitmap(CartHolder.getInstance().getImage(getIntent().getStringExtra("key")
                    , getIntent().getIntExtra("index", -1)));
        }
        findViewById(R.id.crop_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.crop_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getBooleanExtra("cart", false)) {
                    CartHolder.getInstance().getCart()
                            .get(getIntent().getIntExtra("position", 0))
                            .second.set(getIntent().getIntExtra("index", 0)
                            , cropper.getCroppedImage());
                } else {
                    CartHolder.getInstance().setImage(getIntent().getStringExtra("key")
                            , getIntent().getIntExtra("index", -1)
                            , cropper.getCroppedImage());

                }
                finish();
            }
        });
    }
}
