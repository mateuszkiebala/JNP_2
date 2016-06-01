package naMsze.naMsze;

import android.content.Context;
import android.widget.ArrayAdapter;

public class CircularArrayAdapter<T> extends ArrayAdapter {

    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE  / 2;
    public final int MIDDLE;
    public final int QUARTER;
    private T[] objects;
    private Context mContext;
    private int textViewResId;

    public CircularArrayAdapter(Context context, int textViewResId, T[] objects) {
        super(context, textViewResId, objects);
        this.mContext = context;
        this.objects = objects;
        this.textViewResId = textViewResId;
        MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % objects.length;
        QUARTER = MIDDLE / 2 - (MIDDLE / 2) % objects.length;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {
        return objects[position % objects.length];
    }
}
