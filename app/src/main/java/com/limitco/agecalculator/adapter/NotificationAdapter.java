package com.limitco.agecalculator.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.limitco.agecalculator.R;
import com.limitco.agecalculator.database.NotificationInfo;

import java.text.SimpleDateFormat;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    List<NotificationInfo> notificationList;
    Context context;

    public NotificationAdapter(List<NotificationInfo> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }



    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item_layout,parent,false);
        return new NotificationViewHolder(view);

    }




    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {


        String name = notificationList.get(position).getPersonName();
        String subject = notificationList.get(position).getSubject();

        holder.descText.setText("Today is "+name+"'s "+subject+". You can wish him well");

        byte[] image = notificationList.get(position).getImage();
        long timeInMills = notificationList.get(position).getTimeInMills();

        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);

        holder.nameText.setText(name);
        holder.imageView.setImageBitmap(bitmap);
        holder.dateText.setText(covertTime(timeInMills));

        long getId = notificationList.get(position).getId();
        holder.itemView.setTag(getId);

    }




    @Override
    public int getItemCount() {
        return notificationList.size();
    }




    class NotificationViewHolder extends RecyclerView.ViewHolder{

        TextView nameText, dateText, descText;
        ImageView imageView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.itemNotificationNameId);
            dateText = itemView.findViewById(R.id.itemNotificationDateId);
            descText = itemView.findViewById(R.id.itemNotificationDescId);
            imageView = itemView.findViewById(R.id.itemNotificationImageId);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setClipToOutline(true);
            }

        }
    }


    private String covertTime(long timeInMills){

        SimpleDateFormat format = new SimpleDateFormat("EEEE, d MMM");
        String time = format.format(timeInMills);
        return time;

    }


}
