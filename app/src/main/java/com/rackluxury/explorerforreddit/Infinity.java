package com.rackluxury.explorerforreddit;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;

import com.evernote.android.state.StateSaver;
import com.livefront.bridge.Bridge;
import com.livefront.bridge.SavedStateHandler;

import org.greenrobot.eventbus.EventBus;

import com.rackluxury.explorerforreddit.broadcastreceivers.NetworkWifiStatusReceiver;
import com.rackluxury.explorerforreddit.broadcastreceivers.WallpaperChangeReceiver;
import com.rackluxury.explorerforreddit.events.ChangeNetworkStatusEvent;
import com.rackluxury.explorerforreddit.utils.Utils;

import com.rackluxury.explorerforreddit.EventBusIndex;

public class Infinity extends Application implements LifecycleObserver  {
    private AppComponent mAppComponent;


    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        //ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        Bridge.initialize(getApplicationContext(), new SavedStateHandler() {
            @Override
            public void saveInstanceState(@NonNull Object target, @NonNull Bundle state) {
                StateSaver.saveInstanceState(target, state);
            }

            @Override
            public void restoreInstanceState(@NonNull Object target, @Nullable Bundle state) {
                StateSaver.restoreInstanceState(target, state);
            }
        });

        EventBus.builder().addIndex(new EventBusIndex()).installDefaultEventBus();

        NetworkWifiStatusReceiver mNetworkWifiStatusReceiver = new NetworkWifiStatusReceiver(() -> EventBus.getDefault().post(new ChangeNetworkStatusEvent(Utils.getConnectedNetwork(getApplicationContext()))));
        registerReceiver(mNetworkWifiStatusReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        registerReceiver(new WallpaperChangeReceiver(), new IntentFilter(Intent.ACTION_WALLPAPER_CHANGED));
    }


    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
