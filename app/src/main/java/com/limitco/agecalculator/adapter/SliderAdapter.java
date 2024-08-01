package com.limitco.agecalculator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.limitco.agecalculator.R;

public class SliderAdapter extends PagerAdapter {


    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }


    int anim[] = {
            R.raw.age_calculator_lottie,
            R.raw.name_of_day_lottie,
            R.raw.age_compare_lottie,
            R.raw.person_add_lottie
    };


    int headings[] = {
            R.string.age_calculate,
            R.string.name_of_day,
            R.string.age_compare,
            R.string.person_add
    };


    int description[] = {
            R.string.age_calculate_description,
            R.string.name_of_day_description,
            R.string.age_compare_description,
            R.string.person_add_description
    };


    int background[] = {
            R.color.card1,
            R.color.card2,
            R.color.card3,
            R.color.card4
    };


    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slides_layout,container,false);

        /** find by Id */
        LinearLayout slideLayout = view.findViewById(R.id.slidesLayoutId);
        LottieAnimationView lottieAnimationView = view.findViewById(R.id.slidesAnimId);
        TextView heading = view.findViewById(R.id.slidesNameTextId);
        TextView desc = view.findViewById(R.id.slidesDescTextId);


        slideLayout.setBackgroundColor(context.getResources().getColor(background[position]));
        lottieAnimationView.setAnimation(anim[position]);
        heading.setText(headings[position]);
        desc.setText(description[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
