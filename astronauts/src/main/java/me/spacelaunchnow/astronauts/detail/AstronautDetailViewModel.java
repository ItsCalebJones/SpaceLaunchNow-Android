package me.spacelaunchnow.astronauts.detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;

public class AstronautDetailViewModel extends ViewModel {
    private MutableLiveData<Astronaut> astronautMutableLiveData;

    public AstronautDetailViewModel() { }


    public MutableLiveData<Astronaut> getAstronaut() {
        if (astronautMutableLiveData == null) {
            astronautMutableLiveData = new MutableLiveData<>();
        }
        return astronautMutableLiveData;
    }
}
