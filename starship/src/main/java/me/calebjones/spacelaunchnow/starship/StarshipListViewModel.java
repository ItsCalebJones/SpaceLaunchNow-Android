package me.calebjones.spacelaunchnow.starship;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.calebjones.spacelaunchnow.data.models.main.dashboards.Starship;

public class StarshipListViewModel extends ViewModel {
    private MutableLiveData<Starship> starship;

    public StarshipListViewModel() { }


    public MutableLiveData<Starship> getStarshipDashboard() {
        if (starship == null) {
            starship = new MutableLiveData<>();
        }
        return starship;
    }
}
