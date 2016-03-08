package com.watsonlogic.malenah.malenah3;

import com.squareup.picasso.Picasso;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomAdapter extends ArrayAdapter<RowItem> {
    private final String TAG = "CustomAdapter";
    Context context;
    private User user;
    ArrayList<RowItem> userFavorites = null;


    public CustomAdapter(Context context, ArrayList<RowItem> resources, User user) {
        super(context, R.layout.custom_row, resources);
        this.context=context;
        this.user=user;
        if(user.getFavorites() != null){
            this.userFavorites = user.getFavorites();
        }
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

        ImageButton writeReviewIcon = (ImageButton)customRow.findViewById(R.id.writeReviewIcon);
        writeReviewIcon.setTag(item.getId()); //for passing list item index
        writeReviewIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Toast.makeText(context,"clicked"+v.getTag()+"item="+item.getFirstName(),Toast.LENGTH_SHORT).show();

                Intent i = new Intent(context, ProfileActivity.class);
                i.putExtra("profileItem",(Parcelable)item);
                context.startActivity(i);
            }
        });

        TextView rowName = (TextView)customRow.findViewById(R.id.rowName); //get reference to rowName
        if(item.getCategory().equals("Physician")){
            rowName.setTextColor(Color.parseColor("#00BFFF"));
        } else if(item.getCategory().equals("Nurse") || item.getCategory().equals("Nurse Practitioner")){
            rowName.setTextColor(Color.parseColor("#FF1493"));
        } else if(item.getCategory().equals("Chiropractor")){
            rowName.setTextColor(Color.parseColor("#FF8C00"));
        }
        String nameDesignation = item.getFirstName()+" "+item.getLastName()+", "+item.getDesignation();
        rowName.setText(nameDesignation);

        TextView rowOrganization = (TextView)customRow.findViewById(R.id.rowOrganization);
        rowOrganization.setText(item.getOrganization());

        TextView rowAddress = (TextView)customRow.findViewById(R.id.rowAddress);

        String address = new String();
        if(!item.getBuildingNumber().equals("")){
            address += item.getBuildingNumber()+"-";
        }
        address+=item.getStreet()+", "+item.getCity()+" "+item.getCountry()+" "+item.getPostalCode();
        rowAddress.setText(address);

        TextView rowNotes = (TextView)customRow.findViewById(R.id.rowNotes);
        String ss=new String();
        for (String s : item.getSpecialty()) {
            ss+=s+", ";
        }
        rowNotes.setText(ss);

        TextView distance = (TextView)customRow.findViewById(R.id.distance);
        distance.setText(item.getDistance() + "mi");

        //TODO check user's favorites and compare to the current row item; display gold star if favorited

        Log.d("CustomAdapter","printing/checking user faves!");

        ToggleButton favoriteToggle = (ToggleButton)customRow.findViewById(R.id.favoriteToggle);
        if(userFavorites != null || userFavorites.size()>0) {
            for (RowItem f : userFavorites) {
                Log.d("CustomAdapter", ""+f.getId());
                if(f.getId()==item.getId()){
                    favoriteToggle.setChecked(true);
                } else{
                    favoriteToggle.setChecked(false);
                }
            }
        }

        favoriteToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("CustomAdapter isChecked", item.getId() + " " + item.getFirstName() + " " + isChecked);
                Map<String,String> postParams = new LinkedHashMap<>();
                if(isChecked){ //user favorited the provider
                    postParams.put("post_action","add_favorite");
                }else{ //user unfavorited the provider
                    postParams.put("post_action","remove_favorite");
                }
                postParams.put("favorites[]",String.valueOf(item.getId()));
                postParams.put("user_id", user.getUserId());
                //new UpdateUserAsyncTask(postParams).execute();

            }
        });
        return customRow;
    }
}