package com.example.cristina.arc_autopaxcounter;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Cristina on 10/18/2015.
 */
public class ThirdFragment extends Fragment {

    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.study_layout, container, false);
        String sName = getArguments().getString("studyName");
        String routeName = getArguments().getString("route");
        String vT = getArguments().getString("vType");
        String vC = getArguments().getString("vCap");

        TextView study = (TextView) myView.findViewById(R.id.tvStudyInfo);
        TextView route = (TextView) myView.findViewById(R.id.tvRouteInfo);
        TextView vehicleType = (TextView) myView.findViewById(R.id.tvTypeInfo);
        TextView capacity = (TextView) myView.findViewById(R.id.tvCapInfo);
        TextView date = (TextView) myView.findViewById(R.id.tvDateInfo);

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String dateTime = df.format(Calendar.getInstance().getTime());

        study.setText(sName);
        route.setText(routeName);
        vehicleType.setText(vT);
        capacity.setText(vC);
        date.setText(dateTime);

        return myView;
    }
}
