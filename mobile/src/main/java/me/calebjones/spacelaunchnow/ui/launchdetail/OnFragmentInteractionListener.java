package me.calebjones.spacelaunchnow.ui.launchdetail;

public interface OnFragmentInteractionListener {

    int AGENCY = 0;
    int SUMMARY = 1;
    int MISSION = 2;

    void sendLaunchToFragment(int fragment);

}
