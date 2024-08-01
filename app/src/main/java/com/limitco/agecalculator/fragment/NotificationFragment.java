package com.limitco.agecalculator.fragment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.limitco.agecalculator.R;
import com.limitco.agecalculator.adapter.NotificationAdapter;
import com.limitco.agecalculator.database.NotificationInfo;
import com.limitco.agecalculator.database.PersonDao;
import com.limitco.agecalculator.database.PersonDatabase;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


/**
 * Create by Naim on 19/02/2022
 */


public class NotificationFragment extends Fragment {

    RecyclerView recyclerView;
    NotificationAdapter adapter;
    LinearLayout emptyNotificationLay;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        /** Change Status Bar Color **/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        }

        /** find by id */
        emptyNotificationLay = view.findViewById(R.id.emptyNotificationLayoutId);

        recyclerView = view.findViewById(R.id.notificationRecyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        /** Set Recycler view to adapter */
        setAdapter();
    }

    private void setAdapter() {

        /** Show Data to Room Database */
        PersonDatabase database = Room.databaseBuilder(getContext(), PersonDatabase.class, "Person_Database")
                .allowMainThreadQueries().build();
        PersonDao personDao = database.personDao();

        List<NotificationInfo> list = personDao.showNotification();
        adapter = new NotificationAdapter(list,getContext());
        recyclerView.setAdapter(adapter);


        /** if no notification are then show empty notification layout */
        if (adapter.getItemCount()==0){
            recyclerView.setVisibility(View.INVISIBLE);
            emptyNotificationLay.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyNotificationLay.setVisibility(View.GONE);
        }


        /** Item Touch to swipe */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                long id = (long) viewHolder.itemView.getTag();

                /** Delete Data to Room Database */
                PersonDatabase database = Room.databaseBuilder(getContext(), PersonDatabase.class, "Person_Database")
                        .allowMainThreadQueries().build();
                PersonDao personDao = database.personDao();

                personDao.deleteNotification((int) id);
                onResume();
            }

            // For Set Item Background
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {


                // Also add RecyclerViewSwipeDecorator dependence
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(),R.color.delete))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("Delete")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(),R.color.white))
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        }).attachToRecyclerView(recyclerView);


    }


}