package com.sprtech.t_grab.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprtech.t_grab.Model.tricycleModel;
import com.sprtech.t_grab.R;
import com.sprtech.t_grab.booking_activity;

import java.util.ArrayList;

public class TricycleAdapter extends RecyclerView.Adapter<TricycleAdapter.ViewHolder> {
    Context context;
    private final ArrayList<tricycleModel> mytricycleModels;
    String visitUID;
    DatabaseReference ref, ref1;
    FirebaseDatabase db;

    public TricycleAdapter(ArrayList<tricycleModel> mytricycleModels, Context context) {
        this.mytricycleModels = mytricycleModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_of_tricycle, parent, false);
        return new TricycleAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        tricycleModel model = mytricycleModels.get(position);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("DriversAvailable");
        ref1 = db.getReference("Users").child(model.getUid());

        visitUID = ref1.getKey();

        holder.driver_name.setText(model.getName());
        holder.driver_location.setText(model.getLocation());
        holder.driver_phone.setText(model.getPhone_number());

        ref.child(model.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    holder.status.setCardBackgroundColor(Color.rgb(33, 255, 21));
                }else {
                    holder.status.setCardBackgroundColor(Color.rgb(255, 1, 53));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.status.setOnClickListener(v -> {
            Intent intent = new Intent(context, booking_activity.class);
            intent.putExtra("visit_user_id", model.getUid());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mytricycleModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView driver_name, driver_location, driver_phone;
        CardView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            driver_name = itemView.findViewById(R.id.driver_name);
            driver_location = itemView.findViewById(R.id.driver_location);
            driver_phone = itemView.findViewById(R.id.driver_phone);
            status = itemView.findViewById(R.id.status);

        }
    }
}
