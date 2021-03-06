package com.candkpeters.ceol.view;

/**
 * Created by crisp on 01/06/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candkpeters.ceol.controller.CeolController;
import com.candkpeters.ceol.model.AudioStreamItem;
import com.candkpeters.ceol.model.CeolModel;
import com.candkpeters.ceol.model.ObservedControlType;
import com.candkpeters.ceol.model.OnControlChangedListener;
import com.candkpeters.ceol.model.control.ControlBase;
import com.candkpeters.ceol.model.control.PlaylistControlBase;
import com.candkpeters.chris.ceol.R;

public class FragmentLibrary extends Fragment {
    private static final String TAG="FragmentLibrary";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    CeolController ceolController;
    private ContentRecyclerAdapter contentRecyclerAdapter;
    private RecyclerView recyclerView;

    public FragmentLibrary() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_library, container, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_library_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
        Log.d(TAG, "onStart in playlist fragment: ");
        ceolController.start( new OnControlChangedListener() {

            @Override
            public void onControlChanged(CeolModel ceolModel, final ObservedControlType observedControlType, final ControlBase controlBase) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        switch (observedControlType) {

                            case All:
                                break;
                            case Connection:
                            case Power:
                            case Audio:
                            case Input:
                            case Progress:
                            case Navigator:
                                break;

                            case Track:
                                break;
                            case Playlist:
//                                playlistRecyclerAdapter.notifyDataSetChanged();
                                break;
                        }
                    }
                });

            }
        }
        );

        contentRecyclerAdapter = new ContentRecyclerAdapter(getContext(), ceolController, new OnAudioItemClickListener() {
            @Override
            public void onAudioItemClick(AudioStreamItem item, boolean isCurrentTrack) {
                Log.d(TAG, "onAudioItemClick: Got a click");

                ceolController.togglePlaylistItem(item, isCurrentTrack);
            }
        });
        recyclerView.setAdapter( contentRecyclerAdapter) ;

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

    private void scrollPlayListToCurrent() {
        PlaylistControlBase playlistControlBase = ceolController.getCeolModel().inputControl.playlistControl;
        int currentPos = playlistControlBase.getCurrentTrackPosition();

//        currentPos--;
//        if ( currentPos < 0 ) currentPos = 0;

        if ( recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager)(recyclerView.getLayoutManager());
            linearLayoutManager.scrollToPositionWithOffset(currentPos, 120);
        } else {
            recyclerView.scrollToPosition(currentPos);
        }
    }

}
