package me.calebjones.spacelaunchnow.events.detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import me.calebjones.spacelaunchnow.data.models.main.Event;

public class EventDetailViewModel extends ViewModel {
    private MutableLiveData<Event> eventMutableLiveData;

    public EventDetailViewModel() { }


    public MutableLiveData<Event> getEvent() {
        if (eventMutableLiveData == null) {
            eventMutableLiveData = new MutableLiveData<>();
        }
        return eventMutableLiveData;
    }
}
