package com.autipro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autipro.R;
import com.autipro.helpers.Config;
import com.autipro.helpers.DataType;
import com.autipro.models.Notification;
import com.autipro.models.SensorData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Notification> notificationArrayList;

    public NotificationAdapter(Context context, ArrayList<Notification> notificationArrayList){
        this.context = context;
        this.notificationArrayList = notificationArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Notification notification = notificationArrayList.get(position);
            holder.textViewTitle.setText(notification.getTitle());
            holder.textViewbody.setText(notification.getBody());
            holder.textViewTime.setText(notification.getTime());

            markNotificationRead(notification);
    }

    private void markNotificationRead(Notification notification) {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference(Config.FIREBASE_NOTIFICATIONS);
        notifRef.child(notification.getId()).child("read").setValue(true);
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewbody, textViewTime;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.title);
            textViewbody = itemView.findViewById(R.id.body);
            textViewTime = itemView.findViewById(R.id.time);
        }
    }
}
