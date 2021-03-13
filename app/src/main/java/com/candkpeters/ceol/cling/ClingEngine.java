package com.candkpeters.ceol.cling;

import android.content.Context;

import com.candkpeters.ceol.device.CeolEngine;
import com.candkpeters.ceol.model.CeolModel;

public class ClingEngine extends CeolEngine {

//    private final ClingGatherer2 clingGatherer;

    public ClingEngine(final Context context, final CeolModel ceolModel) {
        super(context, ceolModel);

        // No longer works
//        clingGatherer = new ClingGatherer2(context, ceolModel, new OnClingListener() {
//            @Override
//            public void onClingDisconnected() {
////                if ( haveNetwork) {
////                    Log.d(TAG, "onClingDisconnected: We were disconnected. Let's connect again.");
////                    startGatherers();
////                }
//            }
//        });

    }

    @Override
    public void start() {
//        clingGatherer.start(getPrefs());
    }

    @Override
    public void stop() {
//        clingGatherer.pauseCling();
    }

    @Override
    public void nudge() {

    }

    @Override
    public void sendCommandStr(String commandStr) {
//        clingGatherer.sendOpenHomeCommand(commandStr);
    }

    @Override
    public void sendCommandSeekTrack(int trackId) {
//        clingGatherer.sendOpenHomeSeekIdCommand(trackId);
    }

    @Override
    public void sendCommandSeekAbsoluteSecond(int absoluteSeconds) {
//        clingGatherer.sendOpenHomeSeekSecondAbsolute(absoluteSeconds);
    }
}
