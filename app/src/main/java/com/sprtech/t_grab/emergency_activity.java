package com.sprtech.t_grab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Objects;

public class emergency_activity extends AppCompatActivity {
    Toolbar mToolbar;
    CardView lgu, bfp, police, bjmp, army, red_cross;
    TextView number_mho, number_bfp, number_police, number_bjmp, number_army, number_red_cross;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        Var();

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //call mho
        lgu.setOnClickListener(v -> {
            String number = number_mho.getText().toString();
            String MHO_number = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(MHO_number));
            startActivity(intent);
        });

        //call bfp
        bfp.setOnClickListener(v -> {
            String number = number_bfp.getText().toString();
            String bfp_number = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(bfp_number));
            startActivity(intent);
        });

        //call pnp
        police.setOnClickListener(v -> {
            String number = number_police.getText().toString();
            String police_number = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(police_number));
            startActivity(intent);
        });

        //call bjmp
        bjmp.setOnClickListener(v -> {
            String number = number_bjmp.getText().toString();
            String bjmp_number = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(bjmp_number));
            startActivity(intent);
        });

        //call army
        army.setOnClickListener(v -> {
            String number = number_army.getText().toString();
            String army_number = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(army_number));
            startActivity(intent);
        });

        //call red_cross
        red_cross.setOnClickListener(v -> {
            String number = number_red_cross.getText().toString();
            String red_cross_number = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(red_cross_number));
            startActivity(intent);
        });
    }

    private void Var() {
        lgu = findViewById(R.id.lgu);
        bfp = findViewById(R.id.bfp);
        police = findViewById(R.id.police);
        bjmp = findViewById(R.id.bjmp);
        army = findViewById(R.id.army);
        red_cross = findViewById(R.id.red_cross);
        number_mho = findViewById(R.id.number_mho);
        number_bfp = findViewById(R.id.number_bfp);
        number_police = findViewById(R.id.number_police);
        number_bjmp = findViewById(R.id.number_bjmp);
        number_army = findViewById(R.id.number_army);
        number_red_cross = findViewById(R.id.number_red_cross);
    }
}