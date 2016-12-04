package edu.sfsu.csc780.chathub.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.sfsu.csc780.chathub.R;

/**
 * Created by SangSaephan on 12/4/16.
 */
public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] listItem;
    private final Integer[] imageId;

    public CustomListAdapter(Activity context, String[] listItem, Integer[] imageId) {
        super(context, R.layout.drawer_list_item, listItem);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.listItem = listItem;
        this.imageId = imageId;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.drawer_list_item, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.rowTextView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        txtTitle.setText(listItem[position]);
        imageView.setImageResource(imageId[position]);

        return rowView;

    };
}
