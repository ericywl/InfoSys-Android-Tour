package eric.myapplication.Database;


import android.provider.BaseColumns;

public class TravelContract {
    public static final class TravelEntry implements BaseColumns {
        public static final String TAXI_TIME = "TaxiTime";
        public static final String TAXI_COST = "TaxiCost";
        public static final String PT_TIME = "PublicTransportTime";
        public static final String PT_COST = "PublicTransportCost";
        public static final String WALK_TIME = "WalkTime";

        public static final String ORIGIN = "Origin";
        public static final String MBS = "Marina_Bay_Sands";
        public static final String BUDDHA_TOOTH = "Buddha_Tooth_Relic_Temple";
        public static final String KWAN_IM = "Kwan_Im_Thong_Hood_Cho_Temple";
        public static final String SIONG_LIM = "Siong_Lim_Temple";
        public static final String THIAN_HOCK = "Thian_Hock_Keng_Temple";
        public static final String KONG_MENG = "Kong_Meng_San_Phor_Kark_See_Monastery";
        public static final String BURMESE = "Burmese_Buddhist_Temple";
        public static final String SAKYA_MUNI = "Sakya_Muni_Buddha_Gaya_Temple";
        public static final String FOO_HAI = "Foo_Hai_Chan_Monastery";
        public static final String LEONG_SAN = "Leong_San_Temple";
        public static final String WAT_ANANDA = "Wat_Ananda_Metyarama";
    }
}
