package com.candkpeters.ceol.view;

/**
 * Created by crisp on 01/06/2017.
 */

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candkpeters.ceol.controller.CeolController;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.ObservedControlType;
import com.candkpeters.ceol.model.OnControlChangedListener;
import com.candkpeters.ceol.model.control.CeolNavigatorControl;
import com.candkpeters.ceol.model.control.ControlBase;
import com.candkpeters.chris.ceol.R;

/**
 * Section 3 - Ceol navigator control.
 */
public class FragmentNavigator extends Fragment {
    private static final String TAG="FragmentNavigator";
    CeolController ceolController;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    public FragmentNavigator() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tablayout_navigator, container, false);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Entering");
        ceolController = new CeolController(getContext());

        ceolController.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: In Navigator fragment");
        ceolController.start(new OnControlChangedListener() {

            @Override
            public void onControlChanged(final CeolModel ceolModel, final ObservedControlType observedControlType, final ControlBase controlBase) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        switch (observedControlType) {

                            case Connection:
                            case Power:
                            case Audio:
                            case Input:
                            case Track:
                                break;
                            case Navigator:
                            case All:
                                updateNavigation( ceolModel.inputControl.navigatorControl);
                                break;
                            case Playlist:
                            case Progress:
                                break;
                        }
                    }
                });

            }
        }
        );

    }

    private void updateNavigation(CeolNavigatorControl navigatorControl) {

        if ( navigatorControl != null) {
            updateNavigationRow(navigatorControl, R.id.textRow0, 0);
            updateNavigationRow(navigatorControl, R.id.textRow1, 1);
            updateNavigationRow(navigatorControl, R.id.textRow2, 2);
            updateNavigationRow(navigatorControl, R.id.textRow3, 3);
            updateNavigationRow(navigatorControl, R.id.textRow4, 4);
            updateNavigationRow(navigatorControl, R.id.textRow5, 5);
            updateNavigationRow(navigatorControl, R.id.textRow6, 6);
            updateNavigationRow(navigatorControl, R.id.textRow7, 7);
        }
//        ListView entriesList = (ListView)findViewById(R.id.entriesList);
//        ListAdapter adapter = entriesList.getAdapter();

    }

    private void updateNavigationRow(CeolNavigatorControl navigatorControl, int rowResId, int rowIndex) {
        TextView textV = (TextView)(getView().findViewById(rowResId));
        if ( textV != null) {
            if ( navigatorControl.isBrowsing() ) {
                SpannableString s = new SpannableString(navigatorControl.getEntries().getBrowseLineText(rowIndex));
                if ( navigatorControl.getEntries().getSelectedEntryIndex() == rowIndex) {
                    s.setSpan(new StyleSpan(Typeface.BOLD_ITALIC),0, s.length(),0);
                }
                textV.setText(s);
            } else {
                textV.setText("");
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Entering");
//        ceolController.activityOnStop();
        ceolController.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ceolController.destroy();
    }

}

