package eric.myapplication.Adapter;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import eric.myapplication.Misc.TSPPath;
import eric.myapplication.R;

public class DetailsListAdapter extends ArrayAdapter {
    private Activity context;
    private List<TSPPath> paths;

    @SuppressWarnings("unchecked")
    public DetailsListAdapter(Activity context, List<TSPPath> paths) {
        super(context, R.layout.detailslistview_row, paths);

        this.context = context;
        this.paths = paths;
    }

    private class ViewHolder {
        private TextView fromText;
        private TextView toText;
        private TextView detailsText;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.detailslistview_row, parent, false);

            holder = new ViewHolder();
            holder.fromText = view.findViewById(R.id.from_text);
            holder.toText = view.findViewById(R.id.to_text);
            holder.detailsText = view.findViewById(R.id.details_text);

            view.setTag(holder);
        }

        holder = (ViewHolder) view.getTag();

        // Travelling from
        TSPPath path = paths.get(position);
        String fromStr = "From: " + path.getFrom().replace("_", " ");
        holder.fromText.setText(fromStr);

        // Travelling to
        String toStr = "To: " + path.getTo().replace("_", " ");
        holder.toText.setText(toStr);

        // Details like time, cost and transport mode
        String timeStr = "Time: " + ((path.getTransportMode().equals("TAXI")) ?
                path.getTaxiTime() : path.getAltTime()) + " mins";
        String costStr = "Cost: " + ((path.getTransportMode().equals("TAXI")) ?
                path.getTaxiCost() : path.getAltCost()) + " SGD";
        String modeStr = "Mode: " + path.getTransportMode();
        String detailsTextStr = timeStr + "      " + costStr + "      " + modeStr;
        holder.detailsText.setText(detailsTextStr);

        return view;
    }
}
