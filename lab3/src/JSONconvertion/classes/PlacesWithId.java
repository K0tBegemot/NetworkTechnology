package JSONconvertion.classes;

import java.util.ArrayList;

public class PlacesWithId {

    public class Geometry{
        public ArrayList<Double> coordinates;

        public Point getPoint() {
            Point result = new Point();
            result.lon = coordinates.get(0);
            result.lat = coordinates.get(1);

            return result;
        }
    }

    public class Properties{
        public String xid;
        public String name;
        public double dist;
    }

    public class Feature{
        public Geometry geometry;
        public Properties properties;
    }

    public class Root{
        public ArrayList<Feature> features;
    }
}
