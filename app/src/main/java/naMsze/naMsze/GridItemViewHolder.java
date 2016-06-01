package naMsze.naMsze;

import android.widget.TextView;

/**
 * Created by User on 2016-02-29.
 */
public class GridItemViewHolder {
    private TextView textView;
    private HeaderInfo headerInfo;

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setHeaderInfo(HeaderInfo headerInfo) {
        this.headerInfo = headerInfo;
    }

    public HeaderInfo getHeaderInfo() {
        return headerInfo;
    }
}
