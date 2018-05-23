package com.techweblearn.mediastreaming.mvp.interfaces;

import java.io.File;
import java.util.ArrayList;

public interface HomeActivityViewInter {

    void onCompleted(ArrayList<File> files);
    void onError();
    void onPermissionError();
}
