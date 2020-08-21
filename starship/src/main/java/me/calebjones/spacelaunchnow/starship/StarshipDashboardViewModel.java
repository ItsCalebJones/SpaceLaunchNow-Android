package me.calebjones.spacelaunchnow.starship;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.calebjones.spacelaunchnow.data.models.main.dashboards.Starship;

public class StarshipDashboardViewModel extends ViewModel {
    private MutableLiveData<Starship> starship;

    public StarshipDashboardViewModel() { }


    public MutableLiveData<Starship> getStarshipDashboard() {
        if (starship == null) {
            starship = new MutableLiveData<>();
        }
        return starship;
    }
}
