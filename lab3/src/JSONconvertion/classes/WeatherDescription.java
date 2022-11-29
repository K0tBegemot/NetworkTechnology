package JSONconvertion.classes;

import java.util.ArrayList;

public class WeatherDescription {
    public class Coord{
        public double lon;
        public double lat;
    }

    public class Weather{
        public String main;
        public String description;
    }

    public class Main{
        public double temp;
        public double feels_like;
        public double temp_min;
        public double temp_max;
        public int pressure;
        public int humidity;
    }

    public class Wind{
        public double speed;
        public int deg;
    }

    public class Root{
        public Coord coord;
        public ArrayList<Weather> weather;
        public Main main;
        public Wind wind;
        public String name;
    }
}
