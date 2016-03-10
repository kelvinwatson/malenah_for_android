package com.watsonlogic.malenah.malenah3;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MyProfileFragment";
    private TextView tv;
    private User user;
    private EditText editTextName;
    private EditText editTextEmail;
    private Button submitChangesBtn;
    private ArrayList<RowItem> favorites = new ArrayList<RowItem>();
    private JSONObject userObj = null;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProfileFragment.
     */
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    protected void fetchUserDone(String userInfoStr) {
        parseUser(userInfoStr);
    }

    private void parseUser(String userInfoStr) {
        try {
            userObj = new JSONObject(userInfoStr);
            long key = userObj.getLong("key");
            String userId = userObj.getString("user_id");
            String name = userObj.getString("name");
            String email = userObj.getString("email");
            String faves = userObj.getString("favorites");
            JSONArray favoriteProvidersArr = new JSONArray(faves);

            favorites.clear();

            for (int i = 0, len = favoriteProvidersArr.length(); i < len; i++) {
                JSONObject obj = null;
                obj = favoriteProvidersArr.getJSONObject(i);
                JSONArray spA = obj.getJSONArray("specializations");
                ArrayList<String> spL = new ArrayList<String>();
                for (int k = 0, le = spA.length(); k < le; k++) {
                    spL.add(((JSONObject) spA.get(k)).getString("name"));
                }
                RowItem ri = new RowItem(
                        obj.getString("category"),
                        obj.getLong("key"),
                        obj.getString("icon_url"),
                        obj.getString("first_name"),
                        obj.getString("last_name"),
                        obj.getString("designation"),
                        spL,
                        obj.getString("organization"),
                        obj.getString("building"),
                        obj.getString("street"),
                        obj.getString("city"),
                        obj.getString("state"),
                        obj.getString("country"),
                        obj.getString("zipcode"),
                        obj.getString("notes"),
                        obj.getDouble("latitude"),
                        obj.getDouble("longitude"),
                        0.0,
                        true
                );
                favorites.add(ri);
            }
            user = new User(key, userId, email, name, favorites);//Create user object
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editTextName = (EditText) getActivity().findViewById(R.id.editTextName);
        editTextName.setText(user.getName());
        editTextEmail = (EditText) getActivity().findViewById(R.id.editTextEmail);
        editTextEmail.setText(user.getEmail());
        submitChangesBtn = (Button) getActivity().findViewById(R.id.submitChangesBtn);
        submitChangesBtn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                    /*Check for empty input fields */
                        String nameStr = editTextName.getText().toString();
                        String emailStr = editTextEmail.getText().toString();
                        if (TextUtils.isEmpty(nameStr)) {
                            editTextName.setError("You must enter display name.");
                            return;
                        }
                        if (TextUtils.isEmpty(emailStr)) {
                            editTextEmail.setError("You must enter a comment.");
                            return;
                        }

                        Map<String, String> postParams = new LinkedHashMap<>();
                        postParams.put("post_action", "edit_user");
                        postParams.put("user_id", user.getUserId());
                        postParams.put("name", nameStr);
                        postParams.put("email", emailStr);
                        new UpdateUserAsyncTask(MyProfileFragment.this, postParams).execute();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    }
                });
    }

    public void editProfileDone(Boolean r) {
        String msg = null;
        if (r) {
            //result is 200 - OK
            msg = "Profile updated successfully.";
        } else {
            //result in 400 - error
            msg = "Unable to edit profile. Please try again later.";
        }
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
