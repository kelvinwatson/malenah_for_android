package com.watsonlogic.malenah.malenah3;

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

public class CustomAdapter extends ArrayAdapter<String> {

    public CustomAdapter(Context context, String[] resources) {
        super(context, R.layout.custom_row, resources);
    }


    //layout the custom row
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customRow = inflater.inflate(R.layout.custom_row, parent, false);
        final String singleItem = getItem(position); //get reference to string array item
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

        rowIcon.setImageResource(R.drawable.mdplaceholder);
        rowName.setText(singleItem);
        rowDescription.setText("Some Description");
        rowAddress.setText("Some Address");
        rowNotes.setText("Some Notes");
        distance.setText("0.9mi");
        return customRow;
    }
}
