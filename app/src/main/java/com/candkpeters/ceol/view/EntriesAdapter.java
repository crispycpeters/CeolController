package com.candkpeters.ceol.view;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.candkpeters.ceol.model.CeolBrowseEntries;
import com.candkpeters.ceol.model.CeolBrowseEntry;
import com.candkpeters.ceol.model.CeolDevice;

import java.util.ArrayList;

/**
 * Created by crisp on 17/11/2016.
 */

public class EntriesAdapter extends ArrayAdapter<CeolBrowseEntry> {

    CeolDevice ceolDevice;
    ArrayList<CeolBrowseEntry> entryList;

    public EntriesAdapter(CeolDevice ceolDevice, Context context, int resource) {
        super(context, resource);
        this.ceolDevice = ceolDevice;
        this.entryList = new ArrayList<CeolBrowseEntry>();
        refreshList();
    }

    private void refreshList() {
        CeolBrowseEntries entries = ceolDevice.CeolNetServer.getEntries();

        int chunkStartIdx = entries.getChunkStartIndex();
        CeolBrowseEntry[] ceolBrowseEntries = entries.getBrowseChunk();

        for (int i = 0; i< CeolBrowseEntries.MAX_LINES; i++ ) {
            int listIndex = chunkStartIdx + i;
            entryList.set( listIndex, ceolBrowseEntries[i]);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
