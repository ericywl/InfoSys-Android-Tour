package eric.myapplication.Adapter;


import android.app.Activity;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import eric.myapplication.Database.AttractionDBHelper;
import eric.myapplication.R;
import eric.myapplication.Misc.Attraction;

import static eric.myapplication.Database.AttractionContract.AttractionEntry.*;
import static eric.myapplication.Database.AttractionDBHelper.*;
import static eric.myapplication.Activity.PlanActivity.availableAttractionNames;

public class CustomListAdapter extends ArrayAdapter {
    private Activity context;
    private ArrayList<Attraction> selectedList;
    private SQLiteDatabase attractionDB;

    @SuppressWarnings("unchecked")
    public CustomListAdapter(Activity context, ArrayList<Attraction> selectedListParam) {
        super(context, R.layout.listview_row, selectedListParam);

        this.context = context;
        this.selectedList = selectedListParam;
    }

    private class ViewHolder {
        private TextView nameText;
        private TextView addrText;
        private ImageView imageView;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        // Initializing view
        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.listview_row, parent, false);

            holder = new ViewHolder();
            holder.nameText = view.findViewById(R.id.name);
            holder.addrText = view.findViewById(R.id.addr);
            holder.imageView = view.findViewById(R.id.image_placeholder);

            view.setTag(holder);
        }

        holder = (ViewHolder) view.getTag();

        // Setting view
        Attraction attr = selectedList.get(position);
        holder.nameText.setText(attr.getName());
        holder.addrText.setText(attr.getAddress());
        holder.imageView.setImageResource(attr.getImage());

        AttractionDBHelper attractionDBHelper = new AttractionDBHelper(context);
        attractionDB = attractionDBHelper.getWritableDatabase();

        // Click on deleteBtn to delete the attraction from selected
        ImageButton deleteBtn = view.findViewById(R.id.delete_list);
        deleteBtn.setTag(position);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.delete_anim));
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                final Attraction attr = selectedList.get(position);

                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + attr.getName());
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        attractionDB.beginTransaction();
                        String attrName = attr.getName();

                        // Remove from list of selected attractions
                        selectedList.remove(attr);
                        removeFromTable(attractionDB ,SELECTED_TABLE_NAME, attrName);

                        // Add to list of available attractions
                        availableAttractionNames.add(attrName);
                        addToTable(attractionDB ,AVAILABLE_TABLE_NAME, attr);

                        Collections.sort(availableAttractionNames);
                        attractionDB.setTransactionSuccessful();
                        attractionDB.endTransaction();
                        notifyDataSetChanged();

                        Toast.makeText(context, attrName + " removed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                adb.show();
            }
        });

        return view;
    }
}
