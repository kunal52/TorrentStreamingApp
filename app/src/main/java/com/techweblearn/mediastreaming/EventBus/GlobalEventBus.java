package com.techweblearn.mediastreaming.EventBus;

import org.greenrobot.eventbus.EventBus;

public class GlobalEventBus {

    private static EventBus sBus;
    public static EventBus getBus() {
        if (sBus == null)
            sBus = EventBus.getDefault();
        return sBus;
    }
}
