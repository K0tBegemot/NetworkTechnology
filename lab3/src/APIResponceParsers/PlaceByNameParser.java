package APIResponceParsers;

import JSONconvertion.classes.NamedPoints.Place;
import JSONconvertion.classes.NamedPoints.Hit;
import UI.UI;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import java.util.ArrayList;

public class PlaceByNameParser {
    // parse JSON and get selected Hit from Place
    public static ArrayList<Hit> parse(String jsonString) throws JsonIOException {
        Gson jsonParser = new Gson();
        // get Place object from JSON
        Place place = jsonParser.fromJson(jsonString, Place.class);
        // select place
        return place.hits;
    }
}
