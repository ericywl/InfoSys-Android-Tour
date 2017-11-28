package eric.myapplication.Database;


import android.provider.BaseColumns;

public class TravelContract {
    public static final class TravelEntry implements BaseColumns {
        public static final String TAXI_TIME = "TaxiTime";
        public static final String TAXI_COST = "TaxiCost";
        public static final String TRANSPORT_TIME = "TransportTime";
        public static final String TRANSPORT_COST = "TransportCost";
        public static final String WALK_TIME = "WalkTime";

        public static final String ORIGIN = "Origin";
        public static final String MBS = "Marina Bay Sands";
        public static final String BUDDHA_TOOTH = "Buddha Tooth Relic Temple";
        public static final String KWAN_IM = "Kwan Im Thong Hood Cho Temple";
        public static final String SIONG_LIM = "Siong Lim Temple";
        public static final String THIAN_HOCK = "Thian Hock Keng Temple";
        public static final String KONG_MENG = "Kong Meng San Phor Kark See Monastery";
        public static final String BURMESE = "Burmese Buddhist Temple";
        public static final String SAKYA_MUNI = "Sakya Muni Buddha Gaya Temple";
        public static final String FOO_HAI = "Foo Hai Ch'an Monastery";
        public static final String LEONG_SAN = "Leong San Temple";
        public static final String WAT_ANANDA = "Wat Ananda Metyarama";
    }
}
