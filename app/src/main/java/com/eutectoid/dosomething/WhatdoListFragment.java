package com.eutectoid.dosomething;


import android.os.Bundle;

/**
 * Created by TJ on 3/26/2016.
 */

import java.util.ArrayList;
import com.eutectoid.dosomething.WhatdoListView.Adapter.WhatdoListAdapter;

import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ExpandableListView;

public class WhatdoListFragment extends ListFragment {
    private WhatdoListAdapter ExpAdapter;
    private ArrayList<WhatdoListGroup> ExpListItems;
    private ExpandableListView ExpandList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("myTag", "Whatdo List Creation Success");
        //getActivity().setTitle(R.strings.dosomething);
        ExpandList = (ExpandableListView) getView().findViewById(R.id.WhatdoList);
        ExpListItems = SetStandardGroups();
        ExpAdapter = new WhatdoListAdapter(getActivity(), ExpListItems);
        ExpandList.setAdapter(ExpAdapter);
    }

    public ArrayList<WhatdoListGroup> SetStandardGroups() {
        ArrayList<WhatdoListGroup> list = new ArrayList<WhatdoListGroup>();
        ArrayList<WhatdoListChild> list2 = new ArrayList<WhatdoListChild>();

        WhatdoListGroup activities = new WhatdoListGroup();
        activities.setName("Do Something");
        WhatdoListChild ch1 = new WhatdoListChild();
        ch1.setName("Anything");
        ch1.setTag(null);
        list2.add(ch1);

        WhatdoListChild ch2 = new WhatdoListChild();
        ch2.setName("Watch a Movie");
        ch2.setTag(null);
        list2.add(ch2);

        WhatdoListChild ch3 = new WhatdoListChild();
        ch3.setName("Eat Food");
        ch3.setTag(null);
        list2.add(ch3);

        activities.setItems(list2);

        list.add(activities);

        return list;
    }
}
