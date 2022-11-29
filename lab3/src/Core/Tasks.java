package Core;

import APIResponceParsers.NearestPlacesParser;
import APIResponceParsers.PlaceByNameParser;
import APIResponceParsers.PlacesDescriptionParser;
import APIResponceParsers.PlacesWeatherParser;
import Core.APIRequester.RequesterException;
import Core.APIRequester.RequesterException.ExceptionType;
import JSONconvertion.classes.NamedPoints;
import JSONconvertion.classes.PlacesDescription.PlaceDescription;
import JSONconvertion.classes.PlacesWithId;
import JSONconvertion.classes.Point;
import JSONconvertion.classes.WeatherDescription;
import com.google.gson.JsonIOException;

import java.io.IOException;
import java.util.ArrayList;


public class Tasks {
    public enum Language {
        EN("en"), RU("ru");
        private String value;

        Language(String value) {
            this.value = value;
        }
        String getValue() {
            return value;
        }
    }
    // API keys
    private static String graphHooperAPIkey;
    private static String openTripMapAPIkey;
    private static String openWeatherMapAPIkey;
    // request timeouts
    private static int connectTimeout;
    private static int readTimeout;

    private static final String urlTemplate1 = "https://graphhopper.com/api/1/geocode?q= &locale=de&debug=true&key=";
    private static final String urlTemplate2 = "https://api.opentripmap.com/0.1/ru/places/radius?radius= &lon= &lat= &limit= &apikey=";
    private static final String urlTemplate3 = "https://api.opentripmap.com/0.1/ /places/xid/ ?apikey=";
    private static final String urlTemplate4 = "https://api.openweathermap.org/data/2.5/weather?lat= &lon= &appid=";

    private static boolean isConfigured = false;

    static void config(String ghKey, String otKey, String owKey, int cTimeout, int rTimeout) {
        graphHooperAPIkey = ghKey;
        openTripMapAPIkey = otKey;
        openWeatherMapAPIkey = owKey;
        connectTimeout = cTimeout;
        readTimeout = rTimeout;

        isConfigured = true;
    }

    private static String joinSubstrings(String[] arr) {
        StringBuilder builder = new StringBuilder();
        for (String item: arr) {
            builder.append(item);
        }
        return builder.toString();
    }
    // get place by name method
    static ArrayList<NamedPoints.Hit>  getPlacesByName(String placeName) throws RequesterException {
        assert isConfigured;
        // construct url:
        String[] urlParts = urlTemplate1.split(" ");
        // set geo name
        urlParts[0] = urlParts[0] + placeName;
        // set API-key
        urlParts[1] = urlParts[1] + graphHooperAPIkey;

        // concat all substrings
        String resultUrl = joinSubstrings(urlParts);

        // url request
        HTTPclient myClient = new HTTPclient(readTimeout, connectTimeout);
        String jsonResult;
        try {
            jsonResult = myClient.request(resultUrl);
        }
        catch (IOException e) {
            throw new RequesterException(ExceptionType.HTTP_REQUEST, "request failed. 'get places by name' task");
        }

        // parse JSON result
        ArrayList<NamedPoints.Hit> result;
        System.out.println(jsonResult + "\n"); //
        try {
            result = PlaceByNameParser.parse(jsonResult);
        }
        catch (JsonIOException e) {
            throw new RequesterException(ExceptionType.API_RESPONSE, "invalid JSON got: " + jsonResult + "\n'get places by name' task");
        }
        return result;
    }
    // get nearest places
    static ArrayList<PlacesWithId.Feature> getNearestPlaces(NamedPoints.Point point, int radius, int limit) throws RequesterException {
        assert isConfigured;
        // construct url:
        String[] urlParts = urlTemplate2.split(" ");
        // insert radius
        urlParts[0] = urlParts[0] + radius;
        // insert lon and lat
        urlParts[1] = urlParts[1] + point.lng;
        urlParts[2] = urlParts[2] + point.lat;
        // insert limit
        urlParts[3] = urlParts[3] + limit;
        // insert API-key
        urlParts[4] = urlParts[4] + openTripMapAPIkey;

        // concat all substrings
        String resultUrl = joinSubstrings(urlParts);

        // url request:
        HTTPclient myClient = new HTTPclient(readTimeout, connectTimeout);
        String jsonResult;

        try {
            jsonResult = myClient.request(resultUrl);
            System.out.println(jsonResult + "\n"); //
        }
        catch (IOException e) {
            throw new RequesterException(ExceptionType.HTTP_REQUEST, "request failed. 'get nearest places' task");
        }

        // parse JSON result
        ArrayList<PlacesWithId.Feature> result;
        try {
            result = NearestPlacesParser.parse(jsonResult);
        }
        catch (JsonIOException e) {
            throw new RequesterException(ExceptionType.API_RESPONSE, "invalid JSON got: " + jsonResult + "\n'get nearest places' task");
        }

        return result;
    }
    // get place description
    static PlaceDescription getPlaceDescription(String xid, Language lang) throws RequesterException {
        assert isConfigured;
        // construct url:
        String[] urlParts = urlTemplate3.split(" ");
        // set language
        urlParts[0] = urlParts[0] + lang.getValue();
        // set xid
        urlParts[1] = urlParts[1] + xid;
        // set API key
        urlParts[2] =  urlParts[2] + openTripMapAPIkey;

        // concat all substrings
        String resultUrl = joinSubstrings(urlParts);

        // url request
        HTTPclient myClient = new HTTPclient(readTimeout, connectTimeout);
        String jsonResult;

        try {
            jsonResult = myClient.request(resultUrl);
            System.out.println("@@@" + jsonResult + "\n"); //
        }
        catch (IOException e) {
            throw new RequesterException(ExceptionType.HTTP_REQUEST, "request failed. 'get place description' task");
        }

        // parse JSON result
        PlaceDescription result;
        try {
            result = PlacesDescriptionParser.parse(jsonResult);
        }
        catch(JsonIOException e) {
            throw new RequesterException(ExceptionType.API_RESPONSE, "invalid JSON got: " + jsonResult + "\n'get place description' task");
        }
        return result;
    }
    // get place weather
    static WeatherDescription.Root getPlaceWeather(Point point) throws RequesterException {
        assert isConfigured;
        // construct url:
        String[] urlParts = urlTemplate4.split(" ");
        // set lat and lon
        urlParts[0] = urlParts[0] + point.lat;
        urlParts[1] = urlParts[1] + point.lon;
        // set API-key
        urlParts[2] = urlParts[2] + openWeatherMapAPIkey;

        // concat all substrings
        String resultUrl = joinSubstrings(urlParts);

        // url request
        HTTPclient myClient = new HTTPclient(readTimeout, connectTimeout);
        String jsonResult;

        try {
            jsonResult = myClient.request(resultUrl);
            System.out.println("@@" + jsonResult + "\n"); //
        }
        catch (IOException e) {
            throw new RequesterException(ExceptionType.HTTP_REQUEST, "request failed. 'get place weather' task");
        }

        // parse JSON result
        WeatherDescription.Root result;
        try {
            result = PlacesWeatherParser.parse(jsonResult);
        }
        catch(JsonIOException e) {
            throw new RequesterException(ExceptionType.API_RESPONSE, "invalid JSON got: " + jsonResult + "\n'get place weather' task");
        }
        return result;
    }
}
