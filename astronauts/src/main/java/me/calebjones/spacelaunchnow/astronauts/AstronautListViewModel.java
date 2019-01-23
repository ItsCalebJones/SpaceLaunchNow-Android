package me.calebjones.spacelaunchnow.astronauts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;

public class AstronautListViewModel extends ViewModel {
    private MutableLiveData<Astronaut> astronauts;

    public AstronautListViewModel() { }


    public MutableLiveData<Astronaut> getAstronauts() {
        if (astronauts == null) {
            astronauts = new MutableLiveData<>();
        }
        return astronauts;
    }
}
