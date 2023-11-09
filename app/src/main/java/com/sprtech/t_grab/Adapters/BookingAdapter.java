package com.sprtech.t_grab.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.sprtech.t_grab.Model.BookModel;
import com.sprtech.t_grab.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    Context context;
    private final ArrayList<BookModel> bookModels;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference ref, AcceptRef, userRef;
    FirebaseDatabase db;
    String name;
    Dialog dialog;

    public BookingAdapter(ArrayList<BookModel> bookModels, Context context) {
        this.bookModels = bookModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_of_booking, parent, false);
        return new BookingAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookModel book = bookModels.get(position);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("booking");
        userRef = db.getReference("Users");
        AcceptRef = db.getReference("acceptBooking");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        assert mUser != null;
        userRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    name = snapshot.child("name").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.pass_name.setText(book.getName());
        holder.pass_location.setText(book.getDestination());
        holder.pass_phone.setText(book.getPhone());

        holder.call_passenger.setOnClickListener(v -> {
            String number = holder.pass_phone.getText().toString();
            String MHO_number = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(MHO_number));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.cancel_request.setOnClickListener(v -> {
            ref.child(mUser.getUid()).child(book.getPassenger_uid()).removeValue();
            Toast.makeText(context, "Cancel Book of " + book.getName() + " Successfully!", Toast.LENGTH_SHORT).show();
        });

        holder.accept_request.setOnClickListener(v -> {
            AlertDialog.Builder sendMessage = new AlertDialog.Builder(v.getRootView().getContext());
            View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.sendmessagelayout, null);
            dialogView.getRootView().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            EditText messageEdit = dialogView.findViewById(R.id.messageEdit);
            ImageView bt_send = dialogView.findViewById(R.id.bt_send);
            sendMessage.setView(dialogView);
            sendMessage.setCancelable(true);

            bt_send.setOnClickListener(v1 -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("message", messageEdit.getText().toString());
                map.put("driverNAME", name);
                map.put("driverUID", mUser.getUid());

                AcceptRef.child(book.getPassenger_uid()).child(mUser.getUid()).setValue(map);
                ref.child(mUser.getUid()).child(book.getPassenger_uid()).removeValue();
                Toast.makeText(context, "Message Send Successfully.", Toast.LENGTH_SHORT).show();
                sendMessage.create().dismiss();
            });
            sendMessage.show();
        });
    }

    @Override
    public int getItemCount() {
        return bookModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView pass_name, pass_location, pass_phone;
        ImageView call_passenger, accept_request, cancel_request;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pass_name = itemView.findViewById(R.id.passenger_name);
            pass_location = itemView.findViewById(R.id.passenger_location);
            pass_phone = itemView.findViewById(R.id.passenger_phone);
            call_passenger = itemView.findViewById(R.id.call_passenger);
            accept_request = itemView.findViewById(R.id.accept_request);
            cancel_request = itemView.findViewById(R.id.cancel_request);
        }
    }
}
