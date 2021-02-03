package com.candkpeters.ceol.view;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candkpeters.chris.ceol.R;

public class LogFragment extends DialogFragment implements View.OnClickListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "3";

    public LogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.log_fragment, container, false);

//        ListView logListView = rootView.findViewById(R.id.logListView);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.logview_item,R.id.logRow, ceolManager.getLogItems());
//        logListView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        this.dismiss();
        return;
    }

}

