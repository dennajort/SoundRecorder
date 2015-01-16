package com.example.stephane.soundrecorder;

import android.app.Activity;
import android.content.ContentResolver;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Stéphane on 11/01/2015.
 */

public class AllRecordsFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private ArrayList<Record> allRecords = new ArrayList<Record>();
    private RecordsAdapter adapter;
    private ListView allRecordsListView;
    private ActionMode mActionMode = null ;
    private View rootView;

    public AllRecordsFragment() {
    }

    public void updateList() {
        // List musics
        ContentResolver cr = getActivity().getContentResolver();
        this.allRecords.clear();

        File recordsDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/records");
        recordsDirectory.mkdirs();
        File files[] = recordsDirectory.listFiles();
        for (int i=0; i < files.length; i++) {
            File file = files[i];
            MediaMetadataRetriever meta = new MediaMetadataRetriever();
            meta.setDataSource(file.getPath());
            allRecords.add(new Record(file.getPath(), file.getName(), meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        }


        this.allRecordsListView = (ListView) rootView.findViewById(R.id.allRecordsListView);
        adapter = new RecordsAdapter(rootView.getContext(), this.allRecords);
        this.allRecordsListView.setAdapter(adapter);

        allRecordsListView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        allRecordsListView.setOnItemClickListener(this);
        allRecordsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemSelect(position);
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.rootView = inflater.inflate(R.layout.fragment_all_records, container, false);
        updateList();
        return this.rootView;
    }

    public void onListItemSelect(int position) {
        adapter.toggleSelection(position);
        Toolbar toolbar = (Toolbar) this.getActivity().findViewById(R.id.toolbar_actionbar);

        boolean hasCheckedItems = adapter.getSelectedCount() > 0;
        if (hasCheckedItems && mActionMode == null) {
            ActionBarActivity activity = (ActionBarActivity)getActivity();
            mActionMode = activity.startSupportActionMode(new ActionModeCallback(this.getActivity()));
            toolbar.setVisibility(View.GONE);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
        }

        if (mActionMode != null) {
            mActionMode.setTitle(String.valueOf(adapter.getSelectedCount()));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mActionMode == null) {
            ((MainActivity)getActivity()).getPlayerFragment().getFragmentControls().playSong(allRecords.get(position));
        } else {
            onListItemSelect(position);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return true;
    }

    private class ActionModeCallback implements ActionMode.Callback {

        private Activity mActivity;

        public ActionModeCallback(Activity main) {
            mActivity = main;
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_delete:
                    SparseBooleanArray selected = adapter.getSelectedItemsId();
                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            Record record = adapter.getItem(selected.keyAt(i));
                            record.remove();
                            adapter.remove(record);
                        }
                    }
                    actionMode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            adapter.removeSelection();
            mActionMode = null;
            Toolbar toolbar = (Toolbar) this.mActivity.findViewById(R.id.toolbar_actionbar);
            toolbar.setVisibility(View.VISIBLE);
        }
    }
}