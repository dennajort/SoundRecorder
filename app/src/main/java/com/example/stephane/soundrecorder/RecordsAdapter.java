package com.example.stephane.soundrecorder;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;


/**
 * Created by Stéphane on 14/01/2015.
 */

public class RecordsAdapter extends ArrayAdapter<Record> {

    private ArrayList<Record>   records;
    private SparseBooleanArray  mSelectedItemsId;

    public RecordsAdapter(Context context, ArrayList<Record> records) {
        super(context, 0, records);
        this.mSelectedItemsId = new SparseBooleanArray();
        this.records = records;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Record record = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_record, parent, false);
        }

        convertView.setBackgroundColor(mSelectedItemsId.get(position) ? Color.parseColor("#EEEEEE") : Color.WHITE);
        convertView.findViewById(R.id.recordRightLayout). setBackgroundColor(mSelectedItemsId.get(position) ? Color.parseColor("#EEEEEE") : Color.WHITE);

        TextView recordName = (TextView) convertView.findViewById(R.id.recordName);
        TextView recordDuration = (TextView) convertView.findViewById(R.id.recordDuration);

        recordName.setText(record.name);
        recordDuration.setText(record.duration);

        return convertView;
    }

    @Override
    public void add(Record object) {
        records.add(object);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Record object) {
        records.remove(object);
        notifyDataSetChanged();
    }

    public ArrayList<Record> getRecords() {
        return this.records;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsId.get(position));
    }

    public void removeSelection() {
        this.mSelectedItemsId = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value) {
            mSelectedItemsId.put(position, value);
        } else {
            mSelectedItemsId.delete(position);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return this.mSelectedItemsId.size();
    }

    public SparseBooleanArray getSelectedItemsId() {
        return this.mSelectedItemsId;
    }
}