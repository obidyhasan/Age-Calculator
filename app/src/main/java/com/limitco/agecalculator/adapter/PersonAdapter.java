package com.limitco.agecalculator.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.limitco.agecalculator.DetailsActivity;
import com.limitco.agecalculator.R;
import com.limitco.agecalculator.database.PersonInfo;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {


    List<PersonInfo> list;
    Context context;


    public PersonAdapter(List<PersonInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item_layout,parent,false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.name.setText(list.get(position).getName());
        holder.subject.setText(list.get(position).getSubject());
        holder.day.setText(""+list.get(position).getDay());
        holder.month.setText(""+list.get(position).getMonth());
        holder.year.setText(""+list.get(position).getYear());

        byte[] bytes = list.get(position).getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        holder.image.setImageBitmap(bitmap);


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Send data from Person Details Activity by intent
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("Id",list.get(position).getId());
                intent.putExtra("Name",list.get(position).getName());
                intent.putExtra("Image",list.get(position).getImage());
                intent.putExtra("Subject",list.get(position).getSubject());
                intent.putExtra("Day",list.get(position).getDay());
                intent.putExtra("Month",list.get(position).getMonth());
                intent.putExtra("Year",list.get(position).getYear());
                intent.putExtra("RequestCode",list.get(position).getRequestCode());
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }





    class PersonViewHolder extends RecyclerView.ViewHolder{

        TextView name, day, month, year, subject;
        ImageView image;
        RelativeLayout layout;


        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);


            name = itemView.findViewById(R.id.personItemNameId);
            image = itemView.findViewById(R.id.personItemImgId);
            day = itemView.findViewById(R.id.personItemDayId);
            month = itemView.findViewById(R.id.personItemMonthId);
            year = itemView.findViewById(R.id.personItemYearId);
            subject = itemView.findViewById(R.id.personItemSubjectId);
            layout = itemView.findViewById(R.id.addPersonItemLayoutId);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                image.setClipToOutline(true);
            }

        }
    }

}
