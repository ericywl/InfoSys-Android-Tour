package eric.myapplication.Adapter;


import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import eric.myapplication.Activity.PlanActivity;
import eric.myapplication.R;
import eric.myapplication.Misc.Attraction;

import static eric.myapplication.Database.AttractionContract.AttractionEntry.AVAILABLE_TABLE_NAME;
import static eric.myapplication.Database.AttractionContract.AttractionEntry.SELECTED_TABLE_NAME;

public class CustomListAdapter extends ArrayAdapter {
    private Activity context;
    private ArrayList<Attraction> selectedList;

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
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

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

        Attraction attr = selectedList.get(position);
        holder.nameText.setText(attr.getName());
        holder.addrText.setText(attr.getAddress());
        holder.imageView.setImageResource(attr.getImage());

        ImageButton deleteBtn = view.findViewById(R.id.delete_list);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(PlanActivity.this);
                final Attraction attr = selectedAttractions.get(position);

                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + attr.getName());
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        attractionDB.beginTransaction();
                        String attrName = attr.getName();

                        // Remove from list of selected attractions
                        selectedAttractions.remove(attr);
                        removeFromTable(SELECTED_TABLE_NAME, attrName);

                        // Add to list of available attractions
                        availableAttractionNames.add(attrName);
                        addToTable(AVAILABLE_TABLE_NAME, attr);

                        Collections.sort(availableAttractionNames);
                        attractionDB.setTransactionSuccessful();
                        attractionDB.endTransaction();
                        adapter.notifyDataSetChanged();

                        Toast.makeText(PlanActivity.this, attrName + " removed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                adb.show();
            }
        });

        return view;
    }
}
