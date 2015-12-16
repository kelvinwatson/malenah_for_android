package com.watsonlogic.malenah.malenah3;

import com.squareup.picasso.Picasso;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<RowItem> {
    Context context;

    public CustomAdapter(Context context, ArrayList<RowItem> resources) {
        super(context, R.layout.custom_row, resources);
        this.context=context;
    }


    //layout the custom row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customRow = inflater.inflate(R.layout.custom_row, parent, false);
        final RowItem item = getItem(position); //get reference to string array item

        /* Reference views */
        ImageView rowIcon = (ImageView)customRow.findViewById(R.id.rowIcon); //get reference to rowIcon
        TextView rowName = (TextView)customRow.findViewById(R.id.rowName); //get reference to rowName
        TextView rowDescription = (TextView)customRow.findViewById(R.id.rowDescription);
        TextView rowAddress = (TextView)customRow.findViewById(R.id.rowAddress);
        TextView rowNotes = (TextView)customRow.findViewById(R.id.rowNotes);
        TextView distance = (TextView)customRow.findViewById(R.id.distance);
        ToggleButton favoriteToggle = (ToggleButton)customRow.findViewById(R.id.favoriteToggle);
        favoriteToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final boolean isCheckedFinal = isChecked;
                //singleItem.setFavorited(isChecked);
                /*Intent updateIntent = new Intent(context, UpdateFavorites.class);
                try {
                    updateIntent.putExtra("userID", user.getId());
                    updateIntent.putExtra("beerID", singleBeerItem.getId());
                    updateIntent.putExtra("checkedFinal", isCheckedFinal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getContext().startService(updateIntent);*/
            }
        });

        /* Set views */
        Picasso.with(context)
                .load(item.getIconURL())
                .fit()
                .centerInside()
                .into(rowIcon);
        //rowIcon.setImageResource(singleItem.getIconURL());
        rowName.setText(item.getName());
        rowDescription.setText(item.getDescription());
        String address = item.getBuildingNumber()+" "+item.getStreetNumber()+" "+
                item.getStreetName()+", "+item.getCity()+" "+item.getCountry()+" "+
                item.getPostalCode();
        rowAddress.setText(address);
        rowNotes.setText(item.getNotes());
        distance.setText(item.getDistance()+"mi");
        return customRow;
    }
}