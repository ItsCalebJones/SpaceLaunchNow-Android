package me.calebjones.spacelaunchnow.spacestation.detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;

public class SpacestationDetailViewModel extends ViewModel {
    private MutableLiveData<Spacestation> spacestationMutableLiveData;

    public SpacestationDetailViewModel() { }


    public MutableLiveData<Spacestation> getSpacestation() {
        if (spacestationMutableLiveData == null) {
            spacestationMutableLiveData = new MutableLiveData<>();
        }
        return spacestationMutableLiveData;
    }
}
