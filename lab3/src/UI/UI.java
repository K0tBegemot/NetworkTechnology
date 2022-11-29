package UI;

import JSONconvertion.classes.NamedPoints;
import Core.APIRequester.RequesterException;
import JSONconvertion.classes.PlacesDescription;
import JSONconvertion.classes.PlacesWithId;
import JSONconvertion.classes.WeatherDescription;

import java.util.ArrayList;

public interface UI {
    // get API1 (place by name) input
    String getPlaceName();
    // select item from API1 output (returns index in list)
    int getPlaceIdx();
    // get radius of search near selected place
    int getRadius();

    // get API keys methods
    String getGHkey();
    String getOTkey();
    String getOWkey();
    // display places method
    void displayPlaces(ArrayList<NamedPoints.Hit> places);

    // display exception threw while app executing
    void displayException(RequesterException e);
    // get maximum number of places to describe
    int getLimit();
    // display places with xid
    void displayPlacesWithinRadius(ArrayList<PlacesWithId.Feature> nearPlaces);

    void displayWeather(PlacesWithId.Feature feature, WeatherDescription.Root weather);

    void displayDescription(PlacesWithId.Feature feature, PlacesDescription.PlaceDescription description);

    boolean getStatus();

    void close();
}
