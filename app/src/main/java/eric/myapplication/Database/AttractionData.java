package eric.myapplication.Database;


import eric.myapplication.R;

class AttractionData {
    private static String BTRT_INFO = "If you go to China Town, you’ll find this wonderful attraction " +
            "which is based on Tang dynasty architectural style. Opened in 2007, now this temple " +
            "has become one of the most popular attractions in China Town, Singapore. The Buddha Tooth " +
            "Relic Temple is a remarkable four-story temple. When entering the gate, you’ll immediately " +
            "notice the stunning main hall with its high ceiling. The bell tower and drum tower " +
            "are on the same floor. The Buddhist Culture Museum on the third floor houses about " +
            "300 Buddhist artifact from all around Asia including China, Thailand, Myanmar and " +
            "Pakistan. They offer a free English-language tour every Tuesday and Thursday and to avoid " +
            "disappointment, it is wise to book early by calling in advance. The little teahouse " +
            "located on the second floor is a nice place to relax after the tour which usually lasts " +
            "about an hour and here you’ll be served not only tea but some vegetarian cuisine.\nThe " +
            "temple holds ceremonies on a daily basis; visitors are allowed to take photos, even " +
            "during services. Avoid off-the-shoulder T-shirts, shorts and mini-skirts as dress with " +
            "respectful attire is required when visiting the temple.";
    private static String KITHCT_INFO = "The temple has existed since 1884 at its present location with a " +
            "reconstruction in 1895. The original temple was an example of Chinese temple architecture " +
            "and traditional craftsmanship. This temple is a traditional Chinese Temple situated at " +
            "178 Waterloo Street in Singapore. The temple is significance to the Chinese in Singapore " +
            "and believed to bring worshippers good luck after praying to the Kuan Yin. Each day a large " +
            "numbers of worshippers come to pay respect and ask for her blessing. This major temple " +
            "features Chinese-style roofs, granite tiles and the use of red, golden and yellow throughout " +
            "the building.";
    private static String SLT_INFO = "Lian Shan Shuang Lin Monastery (莲山双林寺, which means " +
            "“Twin Grove of the Lotus Mountain Temple”), or Shuang Lin Monastery for short, is a " +
            "Buddhist monastery located in Toa Payoh. The name of the monastery refers to the Twin " +
            "Groves of sala trees located at the Bodhgaya in India, where Buddha was believed " +
            "to have attained enlightenment. This large temple was built in 1908 and is now classified " +
            "a National Monument. The temple, which commemorates Buddha’s birth and death, has a highly " +
            "decorated gateway, accessible only by bridge, which opens into a courtyard. The " +
            "huge incense burners is inside the temple and a beautifully carved Buddha imported " +
            "from Thailand. The temple also equip with a monastery, a smaller temple and a rock " +
            "garden.";

    static final Object[][] ATTRACTION_ARRAY = {
            {"Buddha Tooth Relic Temple", "288 South Bridge Road, 058840",
                    R.mipmap.buddha_tooth_relic_temple,
                    R.mipmap.buddha_tooth_relic_temple_foreground, BTRT_INFO},
            {"Kwan Im Thong Hood Cho Temple", "178 Waterloo St, 187964",
                    R.mipmap.kwan_im_thong_hood_cho_temple,
                    R.mipmap.kwan_im_thong_hood_cho_temple_foreground, KITHCT_INFO},
            {"Siong Lim Temple", "184E Jalan Toa Payoh, 319941",
                    R.mipmap.siong_lim_temple,
                    R.mipmap.siong_lim_temple_foreground, SLT_INFO},
            {"Thian Hock Keng Temple", "158 Telok Ayer Street, 068613",
                    R.mipmap.thian_hock_keng_temple,
                    R.mipmap.thian_hock_keng_temple_foreground, ""},
            {"Kong Meng San Phor Kark See Monastery", "88 Bright Hill Rd, 574117",
                    R.mipmap.kong_meng_san_phor_kark_see_temple,
                    R.mipmap.kong_meng_san_phor_kark_see_temple_foreground, ""},
            {"Burmese Buddhist Temple", "14 Tai Gin Road, 327873",
                    R.mipmap.burmese_buddhist_temple,
                    R.mipmap.burmese_buddhist_temple_foreground, ""},
            {"Sakya Muni Buddha Gaya Temple", "366 Race Course Rd, 218638",
                    R.mipmap.sakya_muni_buddha_gaya_temple,
                    R.mipmap.sakya_muni_buddha_gaya_temple_foreground, ""},
            {"Foo Hai Ch'an Monastery", "87 Geylang East Ave 2, 389753",
                    R.mipmap.foo_hai_chan_monastery,
                    R.mipmap.foo_hai_chan_monastery_foreground, ""},
            {"Leong San Temple", "371 Race Course Rd, 218641",
                    R.mipmap.leong_san_see_temple,
                    R.mipmap.leong_san_see_temple_foreground, ""},
            {"Wat Ananda Metyarama", "50B Jalan Bukit Merah, 169545",
                    R.mipmap.wat_ananda_metyarama_thai_buddhist_temple,
                    R.mipmap.wat_ananda_metyarama_thai_buddhist_temple_foreground, ""}
    };
}
