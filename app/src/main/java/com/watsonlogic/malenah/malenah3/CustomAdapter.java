package com.watsonlogic.malenah.malenah3;

import com.squareup.picasso.Picasso;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
    private User user;

    public CustomAdapter(Context context, ArrayList<RowItem> resources, User user) {
        super(context, R.layout.custom_row, resources);
        this.context=context;
        this.user=user;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /* layout the custom row */
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customRow = inflater.inflate(R.layout.custom_row, parent, false);
        final RowItem item = getItem(position); //get reference to string array item

        /* Reference and set views */
        ImageView rowIcon = (ImageView)customRow.findViewById(R.id.rowIcon); //get reference to rowIcon
        Picasso.with(context)
                .load(item.getIconURL())
                .fit()
                .centerInside()
                .into(rowIcon);

        TextView rowName = (TextView)customRow.findViewById(R.id.rowName); //get reference to rowName
        String nameDesignation = item.getFirstName()+" "+item.getLastName()+", "+item.getDesignation();
        rowName.setText(nameDesignation);

        TextView rowOrganization = (TextView)customRow.findViewById(R.id.rowOrganization);
        rowOrganization.setText(item.getOrganization());

        TextView rowAddress = (TextView)customRow.findViewById(R.id.rowAddress);
        String address = item.getBuildingNumber()+" "+item.getStreet()+", "+
                item.getCity()+" "+item.getCountry()+" "+item.getPostalCode();
        rowAddress.setText(address);

        TextView rowNotes = (TextView)customRow.findViewById(R.id.rowNotes);
        String ss=new String();
        for (String s : item.getSpecialty()) {
            ss+=s+", ";
        }
        rowNotes.setText(ss);

        TextView distance = (TextView)customRow.findViewById(R.id.distance);
        distance.setText(item.getDistance()+"mi");

        ToggleButton favoriteToggle = (ToggleButton)customRow.findViewById(R.id.favoriteToggle);
        if(item.isFavourited()) {
            Log.d("CUSTOMADAPTER",item.getFirstName()+" favorited");
            favoriteToggle.setChecked(true);
        } else {
            Log.d("CUSTOMADAPTER",item.getLastName()+" NOT favorited");
            favoriteToggle.setChecked(false);
        }
        favoriteToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final boolean isCheckedFinal = isChecked;
                item.setFavourited(isChecked);
                Intent updateFavorites = new Intent(context, UpdateFavorites.class);
                try {
                    updateFavorites.putExtra("userID", user.getId());
                    updateFavorites.putExtra("itemID", item.getId());
                    updateFavorites.putExtra("checkedFinal", isCheckedFinal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //TODO:Uncomment the startService() call below when updateFavorites setup correctly
                // getContext().startService(updateFavorites);
            }
        });
        return customRow;
    }
}