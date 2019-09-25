package com.example.r_clazz.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.r_clazz.Been.Course_Been;
import com.example.r_clazz.R;

import java.util.List;

public class Course_Adapter extends ArrayAdapter<Course_Been> {

    private int resouseID;
    public Course_Adapter(@NonNull Context context, int resource, @NonNull List<Course_Been> objects) {
        super(context, resource, objects);
        this.resouseID = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Course_Been course_been = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resouseID,parent,false);
        TextView clazz_name = view.findViewById(R.id.course_title);
        TextView clazz_code = view.findViewById(R.id.codings);
        clazz_name.setText(course_been.getCourse_name());
        clazz_code.setText(course_been.getCourse_code());
        return view;
    }
}
