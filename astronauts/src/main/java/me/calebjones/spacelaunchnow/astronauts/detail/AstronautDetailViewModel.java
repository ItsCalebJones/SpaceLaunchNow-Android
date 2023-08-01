package me.calebjones.spacelaunchnow.astronauts.detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;

public class AstronautDetailViewModel extends ViewModel {
    private MutableLiveData<Astronaut> astronautMutableLiveData;
    private MutableLiveData<Agency> agencyMutableLiveData;

    public AstronautDetailViewModel() { }


    public MutableLiveData<Astronaut> getAstronaut() {
        if (astronautMutableLiveData == null) {
            astronautMutableLiveData = new MutableLiveData<>();
        }
        return astronautMutableLiveData;
    }

    public MutableLiveData<Agency> getAgency() {
        if (agencyMutableLiveData == null) {
            agencyMutableLiveData = new MutableLiveData<>();
        }
        return agencyMutableLiveData;
    }
}
