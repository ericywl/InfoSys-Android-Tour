package eric.myapplication.Database;


import android.provider.BaseColumns;

public class AttractionContract {
    public static final class AttractionEntry implements BaseColumns {
        public static final String SELECTED_TABLE_NAME = "SelectedAttractions";
        public static final String AVAILABLE_TABLE_NAME = "AvailableAttractions";

        public static final String COL_NAME = "Name";
        public static final String COL_ADDR = "Address";
        public static final String COL_INFO = "Description";
        public static final String COL_IMAGE = "Image";
        public static final String COL_LARGE_IMAGE = "LargeImage";
    }
}
