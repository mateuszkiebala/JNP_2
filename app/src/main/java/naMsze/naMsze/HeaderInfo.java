package naMsze.naMsze;

import android.view.View;

import java.util.ArrayList;

public class HeaderInfo {
    private String name;
    private View myView;
    private ArrayList<ChildInfo> childrenList = new ArrayList<>();

    public HeaderInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ChildInfo> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(ArrayList<ChildInfo> childrenList) {
        this.childrenList = childrenList;
    }

    public void setMyView(View v) {
        this.myView = v;
    }

    public View getMyView() {
        return myView;
    }
}
