package com.watsonlogic.malenah.malenah3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import android.view.inputmethod.InputMethodManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private RowItem profile;
    private Context context;
    private JSONArray reviews = new JSONArray();
    private LinearLayout linearLayout;
    Button submitCommentBtn;
    EditText commentEditText;
    EditText ratingEditText;
    private User user;
    TextView noReviews;
    private int id;
    private String username;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.context = getApplicationContext();

        submitCommentBtn=(Button)findViewById(R.id.submitCommentBtn);
        commentEditText=(EditText)findViewById(R.id.commentEditText);
        ratingEditText=(EditText)findViewById(R.id.ratingEditText);
        submitCommentBtn.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View view) {
                    Log.v("EditText", commentEditText.getText().toString());

                    /*Check for empty input fields */
                    String ratingStr = ratingEditText.getText().toString();
                    String commentStr = commentEditText.getText().toString();
                    if(TextUtils.isEmpty(ratingStr)){
                        ratingEditText.setError("You must enter a rating between 0.0 and 5.0.");
                        return;
                    }
                    if(TextUtils.isEmpty(commentStr)){
                        commentEditText.setError("You must enter a comment.");
                        return;
                    }

                    /*Check for invalid input */
                    double rating = Double.valueOf(ratingEditText.getText().toString());
                    if (rating<0.0 || rating>5.0){
                        ratingEditText.setError("Rating must be between 0.0 and 5.0");
                        return;
                    }

                    //POST THIS COMMENT
                    Map<String,String> postParams = new LinkedHashMap<>();
                    postParams.put("username", user.getName());
                    postParams.put("rating", ratingEditText.getText().toString());
                    postParams.put("comment", commentEditText.getText().toString());
                    postParams.put("provider", String.valueOf(profile.getId()));
                    Log.d("POSTREVIEW","execute async task from ProfileActivity()");
                    new PostReviewAsyncTask(ProfileActivity.this, postParams).execute();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    commentEditText.setText("");
                }
            });
    }

    public void postReviewDone(JSONObject j, Boolean result) throws JSONException {
        //parse string
        TextView newR = new TextView(this);
        newR.setBackgroundResource(R.drawable.pink_border);
        newR.setTextColor(Color.parseColor("#FFFFFF"));
        newR.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        newR.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        if(result) {
            //append comment to scrollable view
            if (noReviews != null){
                if (noReviews.getVisibility() == View.VISIBLE) { //remove "no reviews yet"
                    noReviews.setVisibility(View.GONE);
                }
            }
            String u = (String) j.get("username");
            String r = String.valueOf(j.get("rating"));
            String c = (String) j.get("comment");
            newR.setText("User:" + u + '\n' + "Rating:" + r + '\n' + "Comment:" + c + '\n');
            linearLayout.addView(newR);
        }else{
            //append error to scrollable view
            newR.setText("Sorry, an error occurred while posting your comment. Please try again.");
            linearLayout.addView(newR);
        }
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

    public void getUser(){

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
                //review.setBackgroundResource(R.drawable.pink_border);
                review.setTextColor(Color.parseColor("#FFFFFF"));
                review.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                review.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                review.setText(uName + '\n' + rating + '\n' + comment + '\n');
                linearLayout.addView(review);
            }
        }else{
            Log.d("PROFILE", "No Reviews!");
            noReviews = new TextView(this);
            noReviews.setTextColor(Color.parseColor("#FFFFFF"));
            noReviews.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            noReviews.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
            noReviews.setGravity(Gravity.CENTER);
            noReviews.setText("No Reviews Yet");
            noReviews.setVisibility(View.VISIBLE);
            linearLayout.addView(noReviews);
        }
    }


    protected void getDataFromMapActivity(){
        Intent i = getIntent();
        profile = (RowItem) i.getParcelableExtra("profileItem");
        user = (User)i.getSerializableExtra("user");
        Log.d("PROFILE getParcelable", profile.toString());
        Log.d("PROFILE getParcelable", profile.getFirstName());
        Log.d("PROFILE getParcelable", profile.getLastName());
        Log.d("PROFILE getSerializable", user.getName());
    }

    protected void setHeaderData(){
        ImageView profilePhoto = (ImageView)findViewById(R.id.profilePhoto); //get reference to rowIcon
        Picasso.with(context)
                .load(profile.getIconURL())
                .fit()
                .centerInside()
                .into(profilePhoto);

        TextView nameDesignation = (TextView)findViewById(R.id.nameDesignation); //get reference to rowName
        nameDesignation.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        String nd = profile.getFirstName()+" "+profile.getLastName()+", "+profile.getDesignation();
        nameDesignation.setText(nd);

        TextView organization = (TextView)findViewById(R.id.organization);
        organization.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        organization.setText(profile.getOrganization());

        TextView address = (TextView)findViewById(R.id.address);

        String a = new String();
        if(!profile.getBuildingNumber().equals("")){
            a += profile.getBuildingNumber()+"-";
        }
        a += profile.getStreet()+", "+profile.getCity()+" "+profile.getCountry()+" "+profile.getPostalCode();
        address.setText(a);

        TextView specializations = (TextView)findViewById(R.id.specializations);
        specializations.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (String s : profile.getSpecialty()) {
            sb.append(delim).append(s);
            delim = ", ";
        }
        specializations.setText(sb.toString());

    }


}
