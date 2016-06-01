package naMsze.naMsze;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private final List<String> values;
    private final HeaderInfo headerInfo;

    public GridAdapter(Context context, List<String> values, HeaderInfo headerInfo) {
        mContext = context;
        this.values = values;
        this.headerInfo = headerInfo;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public String getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridItemViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            holder = new GridItemViewHolder();
            holder.setTextView((TextView) convertView.findViewById(R.id.label));
            holder.setHeaderInfo(headerInfo);
            convertView.setTag(holder);
        } else {
            holder = (GridItemViewHolder) convertView.getTag();
        }

        holder.getTextView().setText(values.get(position));
        return convertView;
    }
}
