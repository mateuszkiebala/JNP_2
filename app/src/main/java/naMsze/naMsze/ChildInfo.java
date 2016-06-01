package naMsze.naMsze;

import android.view.View;

import java.util.List;

/**
 * Created by User on 2016-01-21.
 */
public class ChildInfo<String> {
    private List<String> itemList;
    private View myView;
    public ChildInfo(List<String> list) {
        this.itemList = list;
    }

    public void setItemList(List<String> list) {
        this.itemList = list;
    }

    public List<String> getItemList() {
        return this.itemList;
    }

    public View getMyView() {
        return myView;
    }

    public void setMyView(View v) {
        this.myView = v;
    }
}
