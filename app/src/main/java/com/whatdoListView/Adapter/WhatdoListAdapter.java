package com.whatdoListView.Adapter;

/**
 * Created by TJ on 3/26/2016.
 */
//http://www.dreamincode.net/forums/topic/270612-how-to-get-started-with-expandablelistview/
import java.util.ArrayList;

import com.eutectoid.dosomething.R;
import com.eutectoid.dosomething.WhatdoListChild;
import com.eutectoid.dosomething.WhatdoListGroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class WhatdoListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<WhatdoListGroup> groups;
    public WhatdoListAdapter(Context c, ArrayList<WhatdoListGroup> g) {
        this.context = c;
        this.groups = g;
    }

    public void addItem(WhatdoListChild item, WhatdoListGroup group) {
        if(!groups.contains(group)) {
            groups.add(group);
        }
        int index = groups.indexOf(group);
        ArrayList<WhatdoListChild> ch = groups.get(index).getItems();
        ch.add(item);
        groups.get(index).setItems(ch);
    }

    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        ArrayList<WhatdoListChild> chList = groups.get(groupPosition).getItems();
        return chList.get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated mehod stub
        return childPosition;
    }

    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View view, ViewGroup parent) {
        WhatdoListChild child = (WhatdoListChild)getChild(groupPosition, childPosition);
        if(view == null) {
            LayoutInflater infInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = infInflater.inflate(R.layout.whatdo_fragment, null);
        }
        TextView tv = (TextView)view.findViewById(R.id.tvChild);
        tv.setText(child.getName().toString());
        tv.setTag(child.getTag());
        // TODO Auto-generated method stub
        return view;
    }

    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        ArrayList<WhatdoListChild> chList = groups.get(groupPosition).getItems();
        return chList.size();
    }

    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return groups.get(groupPosition);
    }

    public int getGroupCount() {
        // TODO Auto-generated method stub
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {
        WhatdoListGroup group = (WhatdoListGroup)getGroup(groupPosition);
        if(view == null) {
            LayoutInflater inf = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.whatdolist_group_item, null);
        }
        TextView tv = (TextView)view.findViewById(R.id.tvGroup);
        tv.setText(group.getName());
        // TODO Auto-generated method stub
        return view;
    }

    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean isChildSelectable(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return true;
    }
}
