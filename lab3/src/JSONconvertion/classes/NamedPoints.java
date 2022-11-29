package JSONconvertion.classes;

import java.util.ArrayList;

// describes list of geography points with specified names and types
public class NamedPoints {
    public class Point {
        public double lng;
        public double lat;
    }
    public class Hit{
        public long osm_id;
        public String osm_type; //

        public String country;

        public String countrycode; //
        public String state; //

        public String osm_key;

        public String osm_value;
        public String name;
        public Point point;
    }

    public class Place {
        public ArrayList<Hit> hits;
    }
}
