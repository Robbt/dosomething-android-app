package com.eutectoid.dosomething;

/**
 * Created by TJ on 3/26/2016.
 */
//http://www.dreamincode.net/forums/topic/270612-how-to-get-started-with-expandablelistview/

import java.util.ArrayList;

public class WhatdoListGroup {
    private String Name;
    private ArrayList<WhatdoListChild> Items;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public ArrayList<WhatdoListChild> getItems() {
        return Items;
    }

    public void setItems(ArrayList<WhatdoListChild> items) {
        this.Items = items;
    }
}
