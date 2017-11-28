package eric.myapplication.Adapter;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eric.myapplication.R;
import eric.myapplication.Misc.Attraction;

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

        return view;
    }
}
