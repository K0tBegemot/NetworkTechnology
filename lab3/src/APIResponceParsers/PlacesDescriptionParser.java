package APIResponceParsers;

import JSONconvertion.classes.PlacesDescription.PlaceDescription;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

public class PlacesDescriptionParser {
    public static PlaceDescription parse(String jsonString) throws JsonIOException {
        Gson jsonParser = new Gson();
        return jsonParser.fromJson(jsonString, PlaceDescription.class);
    }
}
