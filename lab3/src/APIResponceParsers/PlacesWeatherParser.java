package APIResponceParsers;

import JSONconvertion.classes.WeatherDescription;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

public class PlacesWeatherParser {
    public static WeatherDescription.Root parse(String jsonString) throws JsonIOException {
        Gson jsonParser = new Gson();
        return jsonParser.fromJson(jsonString, WeatherDescription.Root.class);
    }
}
