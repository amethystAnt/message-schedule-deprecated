package com.patlejch.messageschedule.fileobserver;
import android.os.FileObserver;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/*
* (C) by Fimagena
* Taken from https://stackoverflow.com/questions/29227425/multiple-fileobserver-on-same-file-failed
* A replacement for android.os.FileObserver which solves a bug
* that occurs when there is more than one observer on a single file.
* */

public abstract class FixedFileObserver {

    private final static HashMap<File, Set<FixedFileObserver>> sObserverLists = new HashMap<>();

    private FileObserver mObserver;
    private final File mRootPath;
    private final int mMask;

    public FixedFileObserver(String path) {this(path, FileObserver.ALL_EVENTS);}
    public FixedFileObserver(String path, int mask) {
        mRootPath = new File(path);
        mMask = mask;
    }

    public abstract void onEvent(int event, String path);

    public void startWatching() {
        synchronized (sObserverLists) {
            if (!sObserverLists.containsKey(mRootPath)) sObserverLists.put(mRootPath, new HashSet<FixedFileObserver>());

            final Set<FixedFileObserver> fixedObservers = sObserverLists.get(mRootPath);

            mObserver = fixedObservers.size() > 0 ? fixedObservers.iterator().next().mObserver : new FileObserver(mRootPath.getPath()) {
                @Override public void onEvent(int event, String path) {
                    for (FixedFileObserver fixedObserver : fixedObservers)
                        if ((event & fixedObserver.mMask) != 0) fixedObserver.onEvent(event, path);
                }};
            mObserver.startWatching();
            fixedObservers.add(this);
        }
    }

    public void stopWatching() {
        synchronized (sObserverLists) {
            Set<FixedFileObserver> fixedObservers = sObserverLists.get(mRootPath);
            if ((fixedObservers == null) || (mObserver == null)) return;

            fixedObservers.remove(this);
            if (fixedObservers.size() == 0) mObserver.stopWatching();

            mObserver = null;
        }
    }

    protected void finalize() {stopWatching();}
}