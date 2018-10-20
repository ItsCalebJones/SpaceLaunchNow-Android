package me.calebjones.spacelaunchnow.ui.launchdetail;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import me.calebjones.spacelaunchnow.data.models.main.Launch;

public class DetailsViewModel extends ViewModel {
    private MutableLiveData<Launch> launch;

    public DetailsViewModel() { }


    public MutableLiveData<Launch> getLaunch() {
        if (launch == null) {
            launch = new MutableLiveData<>();
        }
        return launch;
    }
}
