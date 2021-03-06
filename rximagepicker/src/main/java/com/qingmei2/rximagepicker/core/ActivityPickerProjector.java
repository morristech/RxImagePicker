package com.qingmei2.rximagepicker.core;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import com.qingmei2.rximagepicker.ui.ICustomPickerConfiguration;
import com.qingmei2.rximagepicker.ui.ICustomPickerView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public final class ActivityPickerProjector implements ICustomPickerView {

    private volatile static ActivityPickerProjector INSTANCE;

    private PublishSubject<Uri> publishSubject;

    private Class<? extends Activity> activityClass;

    public static ActivityPickerProjector getInstance() {
        if (INSTANCE == null) {
            synchronized (ActivityPickerProjector.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ActivityPickerProjector();
                }
            }
        }
        return INSTANCE;
    }

    private ActivityPickerProjector() {

    }

    public void setActivityClass(Class<? extends Activity> clazz) {
        activityClass = clazz;
    }

    public void resetSubject() {
        publishSubject = PublishSubject.create();
    }

    @Override
    public void display(FragmentActivity fragmentActivity,
                        int viewContainer,
                        String viewKey,
                        ICustomPickerConfiguration configuration) {
        resetSubject();
        fragmentActivity.startActivity(new Intent(fragmentActivity, activityClass));
    }

    @Override
    public Observable<Uri> pickImage() {
        return publishSubject;
    }

    public void emitUri(Uri uri) {
        publishSubject.onNext(uri);
    }

    public void emitUris(List<Uri> uris) {
        for (Uri uri : uris) {
            emitUri(uri);
        }
    }

    public void emitError(Throwable e) {
        publishSubject.onError(e);
    }

    public void endUriEmitAndReset() {
        publishSubject.onComplete();
        resetSubject();
    }
}
