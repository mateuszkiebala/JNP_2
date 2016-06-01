package naMsze.naMsze;

import android.graphics.Typeface;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private ArrayList<HeaderInfo> expMenuList;
    private String date;
    private String target;
    private double range;
    private String dateHeader;
    private String targetHeader;
    private String rangeHeader;
    private int gridViewWidth;

    private void initializeGridView(View convertView, final HeaderInfo headerInfo,
                                    final ChildInfo childInfo) {
        final CustomGridView gridView = (CustomGridView) convertView.findViewById(R.id.GridView_toolbar);
        // Creates heightMod columns
        int heightMod = Integer.MAX_VALUE;
        if (headerInfo.getName().equals(targetHeader)) {
            gridView.setNumColumns(3);
            heightMod = 3;
        } else if (headerInfo.getName().equals(rangeHeader)) {
            gridView.setNumColumns(5);
            heightMod = 5;
        }
        // Set spacing between columns, we need to count spacing when counting width of column.
        int spacing = 10;
        gridView.setHorizontalSpacing(spacing);
        // Grid adapter.
        final GridAdapter adapter = new GridAdapter(_context, childInfo.getItemList(), headerInfo);
        gridView.setAdapter(adapter);
        // Set all children for the header.
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); ++i) {
            adapter.getView(i, null, gridView);
            if (i % heightMod == 0) {
                totalHeight += (gridViewWidth - ((heightMod - 1) * spacing)) / heightMod;
            }
        }
        gridView.SetHeight(totalHeight);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridItemViewHolder holder = (GridItemViewHolder) view.getTag();
                String textClicked = holder.getTextView().getText().toString();
                setNewHeaderResult(headerInfo, textClicked);
            }
        });
    }

    public ExpandableListAdapter(Context context, ArrayList<HeaderInfo> list,
                                 String date, String target, double range) {
        this._context = context;
        this.expMenuList = list;
        this.date = date;
        this.target = target;
        this.range = range;
        dateHeader = _context.getResources().getString(R.string.date_header);
        targetHeader = _context.getResources().getString(R.string.target_header);
        rangeHeader = _context.getResources().getString(R.string.range_header);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ChildInfo> childrenList = expMenuList.get(groupPosition).getChildrenList();
        return childrenList.get(childPosition);
    }

    @Override
    public int getGroupCount() {
        return expMenuList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ChildInfo> childrenList = expMenuList.get(groupPosition).getChildrenList();
        return childrenList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return expMenuList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        HeaderInfo headerInfo = (HeaderInfo) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_name, null);
            headerInfo.setMyView(convertView);
            TextView headerResultView = (TextView) convertView.findViewById(R.id.mainListHeaderResult);
            headerResultView.setTypeface(null, Typeface.BOLD);

            // When the view of header creates we set the header result.
            if (headerInfo.getName().equals(targetHeader)) {
                headerResultView.setText(target);
            } else if (headerInfo.getName().equals(rangeHeader)) {
                headerResultView.setText(range + " km");
            } else if (headerInfo.getName().equals(dateHeader)) {
                headerResultView.setText(date.substring(0, 10) + "\n" + date.substring(11));
            }
        }

        TextView headerTitleView = (TextView) convertView.findViewById(R.id.mainListHeader);
        headerTitleView.setText(headerInfo.getName().trim());
        // Set the width of children grid, here is the only moment we know the length of the child grid.
        gridViewWidth = convertView.getWidth();
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        final ChildInfo childInfo = (ChildInfo) getChild(groupPosition, childPosition);
        final HeaderInfo headerInfo = (HeaderInfo) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_view, null);
        }
        initializeGridView(convertView, headerInfo, childInfo);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    // Set new text for header result.
    public void setNewHeaderResult(HeaderInfo headerInfo, String newText) {
        final View headerResultView = headerInfo.getMyView();
        TextView headerResult = (TextView) headerResultView.findViewById(R.id.mainListHeaderResult);
        if (headerInfo.getName().equals(rangeHeader)) {
            Double range = Double.parseDouble(newText);
            // Set text with dot.
            headerResult.setText(Double.toString(range) + " km");
        } else {
            headerResult.setText(newText);
        }
    }

    /** Returns view of the header result. */
    public View getHeaderResultView(HeaderInfo headerInfo) {
        final View headerResultView = headerInfo.getMyView();
        View headerResult = headerResultView.findViewById(R.id.mainListHeaderResult);
        return headerResult;
    }
}
