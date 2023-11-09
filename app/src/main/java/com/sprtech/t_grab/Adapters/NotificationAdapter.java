package com.sprtech.t_grab.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprtech.t_grab.Model.acceptModel;
import com.sprtech.t_grab.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    private final ArrayList<acceptModel> notifModel;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference ref;
    FirebaseDatabase db;

    public NotificationAdapter(ArrayList<acceptModel> notifModel, Context context) {
        this.notifModel = notifModel;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_of_notif, parent, false);
        return new NotificationAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        acceptModel model = notifModel.get(position);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("acceptBooking");

        holder.driver_name.setText(model.getDriverNAME());

        holder.driver_message.setOnClickListener(v -> {
            AlertDialog.Builder seeMessage = new AlertDialog.Builder(v.getRootView().getContext());
            @SuppressLint("InflateParams") View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.driver_message_layout, null);
            dialogView.getRootView().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            seeMessage.setView(dialogView);
            seeMessage.setCancelable(true);

            TextView message_of_driver = dialogView.findViewById(R.id.message_of_driver);

            ref.child(mUser.getUid()).child(model.getDriverUID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        String message = snapshot.child("message").getValue(String.class);
                        message_of_driver.setText(message);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            seeMessage.show();
        });
    }

    @Override
    public int getItemCount() {
        return notifModel.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView driver_name, driver_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            driver_name = itemView.findViewById(R.id.driver_name);
            driver_message = itemView.findViewById(R.id.driver_message);
        }
    }
}
