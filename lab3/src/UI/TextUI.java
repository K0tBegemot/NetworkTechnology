package UI;

import Core.APIRequester;
import JSONconvertion.classes.*;

import java.util.ArrayList;
import java.util.Scanner;

public class TextUI implements UI {
    private Scanner sc;

    public TextUI() {
        sc = new Scanner(System.in);
    }

    @Override
    public String getPlaceName() {
        System.out.println("Enter the place name:");

        return sc.nextLine();
    }

    @Override
    public int getPlaceIdx() {
        System.out.println("Enter the idx of place you want to select:");
        return sc.nextInt();
    }

    @Override
    public int getRadius() {
        System.out.println("Enter the radius of search in meters:");
        return sc.nextInt();
    }

    @Override
    public String getGHkey() {
        System.out.println("Enter the graphhoper API key:");
        return sc.nextLine();
    }

    @Override
    public String getOTkey() {
        System.out.println("Enter the opentripmap API key:");
        return sc.nextLine();
    }

    @Override
    public String getOWkey() {
        System.out.println("Enter the openweather API key:");
        return sc.nextLine();
    }

    @Override
    public void displayPlaces(ArrayList<NamedPoints.Hit> places) {
        System.out.println("found places:");
        if (places == null) {
            System.out.println("    null");
            return;
        }
        if (places.size() == 0) {
            System.out.print("  no places found");
            return;
        }
        int idx = 0;

        for (NamedPoints.Hit place: places) {
            System.out.println("idx=" + idx);
            System.out.println("    name: " + place.name);
            System.out.println("    country: " + place.country);
            System.out.println("    point: " + "lat=" + place.point.lat + " lon=" + place.point.lng);
            idx++;
        }
        System.out.println();
    }

    @Override
    public synchronized void displayException(APIRequester.RequesterException e) {
        System.err.println(e.getExceptionMessage());
    }

    @Override
    public int getLimit() {
        System.out.println("Enter the object's count limit you want to display:");
        return sc.nextInt();
    }

    @Override
    public void displayPlacesWithinRadius(ArrayList<PlacesWithId.Feature> nearPlaces) {
        System.out.println("found places near the selected one: ");

        if (nearPlaces == null) {
            System.out.println("null");
            return;
        }
        if (nearPlaces.size() == 0) {
            System.out.print("empty");
            System.out.println();
            return;
        }

        for (PlacesWithId.Feature place: nearPlaces) {
            System.out.println("    name: " + place.properties.name);
            Point placePoint = place.geometry.getPoint();

            System.out.println("    point: " + "lat=" + placePoint.lat + " lon=" + placePoint.lon);
            System.out.println("    xid: " + place.properties.xid);
            System.out.println();
        }
        System.out.println("-------------------------------");
    }

    @Override
    public void displayWeather(PlacesWithId.Feature feature, WeatherDescription.Root weather) {
        if (feature == null) {
            System.out.println("null place");
            return;
        }
        Point placePoint = feature.geometry.getPoint();
        System.out.println("Weather in place " + feature.properties.name + "(" + "lat=" + placePoint.lat + " lon=" + placePoint.lon + "):");
        if (weather == null) {
            System.out.println("    null");
            return;
        }
        System.out.println("    weather: " + weather.weather.get(0).description);
        System.out.println("    wind speed: " + weather.wind.speed);
        System.out.println("    temperature description: ");
        System.out.println("        temperature: " + weather.main.temp);
        System.out.println("        fills like: " + weather.main.feels_like);
        System.out.println("        humidity: " + weather.main.humidity);
    }

    @Override
    public void displayDescription(PlacesWithId.Feature feature, PlacesDescription.PlaceDescription description) {
        if (feature == null) {
            System.out.println("null place");
            return;
        }
        Point placePoint = feature.geometry.getPoint();
        System.out.println("Description of place " + feature.properties.name + "(" + "lat=" + placePoint.lat + " lon=" + placePoint.lon + "):");
        if (description == null) {
            System.out.println("    null");
        }
        else if (description.info != null && description.info.descr != null) {
            System.out.println(description.info.descr);
        }
        else if (description.wikipedia_extracts != null && description.wikipedia_extracts.text != null) {
            System.out.println(description.wikipedia_extracts.text);
        }
        else {
            System.out.println("Description not found");
        }
        System.out.println();
    }

    @Override
    public boolean getStatus() {
        String status;
        boolean result = false;

        while(true) {
            status = sc.nextLine();
            if (status.equals("exit")) {
                result = true;
                break;
            }
            else if (status.equals("continue")) {
                break;
            }
            else {
                System.out.println("invalid status - " + status + ", enter 'exit' or 'continue'");
            }
        }
        return result;
    }
    public void close() {
        sc.close();
    }
}
