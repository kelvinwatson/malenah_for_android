package com.watsonlogic.malenah.malenah3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private RowItem profile;
    private Context context;
    private JSONArray reviews = new JSONArray();
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.context = getApplicationContext();
        linearLayout = (LinearLayout)findViewById(R.id.reviewsList);
        getDataFromMapActivity();
        setHeaderData();
        fetchReviews();
    }

    public void fetchReviews(){
        //new FetchReviewsAsyncTask(ProfileActivity.this, profile.getId()).execute();
        Log.d("FETCHREV", "executing async task with item.getId()" + profile.getId());
        FetchReviewsAsyncTask asyncTask = new FetchReviewsAsyncTask(ProfileActivity.this,profile.getId());
        try {
            asyncTask.execute(); //set asynchronously
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchReviewsDone(JSONArray reviews){
        this.reviews = reviews;
        Log.d("FETCHREV", "Back in Profile");
        Log.d("FETCHREV", reviews.toString()); //empty array if no reviews for provider
        boolean hasReviews = reviews.length()>0? true : false;
        try{
            setCommentArea(hasReviews);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    protected void setCommentArea(boolean hasReview) throws JSONException {
        if(hasReview){
            Log.d("PROFILE","Has Reviews!");
            for(int i=0, len=reviews.length(); i<len; i++){
                JSONObject ele = null;
                String uName = null;
                String comment = null;
                double rating = -1;
                try{
                    ele = reviews.getJSONObject(i);
                    uName = ele.getString("username");
                    rating = ele.getDouble("rating");
                    comment = ele.getString("comment");
                } catch(JSONException e){
                    e.printStackTrace();
                }
                TextView review = new TextView(this);
                review.setBackgroundResource(R.drawable.pink_border);
                review.setTextColor(Color.parseColor("#FFFFFF"));
                review.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                review.setText(uName + '\n' + rating + '\n' + comment + '\n');
                linearLayout.addView(review);
            }
        }else{
            Log.d("PROFILE", "No Reviews!");
            TextView noReviews = new TextView(this);
            noReviews.setTextColor(Color.parseColor("#FFFFFF"));
            noReviews.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            noReviews.setGravity(Gravity.CENTER);
            noReviews.setText("No Reviews Yet");
            linearLayout.addView(noReviews);
        }
    }


    protected void getDataFromMapActivity(){
        Intent i = getIntent();
        profile = (RowItem) i.getParcelableExtra("profileItem");
        Log.d("PROFILE getParcelable", profile.toString());
        Log.d("PROFILE getParcelable", profile.getFirstName());
        Log.d("PROFILE getParcelable", profile.getLastName());
    }

    protected void setHeaderData(){
        ImageView profilePhoto = (ImageView)findViewById(R.id.profilePhoto); //get reference to rowIcon
        Picasso.with(context)
                .load(profile.getIconURL())
                .fit()
                .centerInside()
                .into(profilePhoto);

        TextView nameDesignation = (TextView)findViewById(R.id.nameDesignation); //get reference to rowName
        String nd = profile.getFirstName()+" "+profile.getLastName()+", "+profile.getDesignation();
        nameDesignation.setText(nd);

        TextView organization = (TextView)findViewById(R.id.organization);
        organization.setText(profile.getOrganization());

        TextView address = (TextView)findViewById(R.id.address);

        String a = new String();
        if(!profile.getBuildingNumber().equals("")){
            a += profile.getBuildingNumber()+"-";
        }
        a += profile.getStreet()+", "+profile.getCity()+" "+profile.getCountry()+" "+profile.getPostalCode();
        address.setText(a);

        TextView specializations = (TextView)findViewById(R.id.specializations);
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (String s : profile.getSpecialty()) {
            sb.append(delim).append(s);
            delim = ", ";
        }
        specializations.setText(sb.toString());

    }


}
