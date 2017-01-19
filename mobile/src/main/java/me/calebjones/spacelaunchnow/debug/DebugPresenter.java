package me.calebjones.spacelaunchnow.debug;

/**
 * Created by cjones on 1/18/17.
 */

public class DebugPresenter implements DebugContract.Presenter{

    private final DebugContract.View debugView;

    public DebugPresenter(DebugContract.View view){
        debugView = view;
        debugView.setPresenter(this);
    }

    @Override
    public void start() {
    }
}
