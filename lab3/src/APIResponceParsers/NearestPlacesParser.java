package APIResponceParsers;

import JSONconvertion.classes.PlacesWithId;
import JSONconvertion.classes.PlacesWithId.Feature;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import java.util.ArrayList;

public class NearestPlacesParser {
    // returns ArrayList of Feature's
    public static ArrayList<Feature> parse(String jsonString) throws JsonIOException {
        Gson jsonParser = new Gson();
        PlacesWithId.Root result = jsonParser.fromJson(jsonString, PlacesWithId.Root.class);

        return result.features;
    }
}
