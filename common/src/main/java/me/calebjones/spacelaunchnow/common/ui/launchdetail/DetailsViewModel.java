package me.calebjones.spacelaunchnow.common.ui.launchdetail;



import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
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
