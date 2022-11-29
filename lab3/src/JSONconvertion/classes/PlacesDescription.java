package JSONconvertion.classes;

public class PlacesDescription {
    public class Info{
        public String descr;
    }

    public class WikipediaExtracts{
        public String title;
        public String text;
    }

    public class PlaceDescription {
        public String xid;
        public String name;
        public Info info;
        public WikipediaExtracts wikipedia_extracts;
        public Point point;
    }
}
