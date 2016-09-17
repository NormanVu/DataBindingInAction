package it.droidcon.databinding.login;

import android.os.Parcelable;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseRxViewModel<M extends Parcelable> extends BaseViewModel<M> {

    private CompositeSubscription createSubscription = new CompositeSubscription();
    private CompositeSubscription startSubscription = new CompositeSubscription();
    private CompositeSubscription resumeSubscription = new CompositeSubscription();

    @Override public final void onCreate() {
        onCreate(createSubscription);
    }

    protected void onCreate(CompositeSubscription subscription) {
    }

    @Override public final void onStart() {
        onStart(startSubscription);
    }

    protected void onStart(CompositeSubscription subscription) {
    }

    @Override public final void onResume() {
        onResume(resumeSubscription);
    }

    protected void onResume(CompositeSubscription subscription) {
    }

    @Override public void onPause() {
        resumeSubscription.clear();
    }

    @Override public void onStop() {
        startSubscription.clear();
    }

    @Override public void onDestroy(boolean changingConfigurations) {
        createSubscription.clear();
    }
}
