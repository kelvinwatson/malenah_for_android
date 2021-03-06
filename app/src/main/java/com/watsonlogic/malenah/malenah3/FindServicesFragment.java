package com.watsonlogic.malenah.malenah3;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindServicesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindServicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindServicesFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public FindServicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindServicesFragment.
     */
    public static FindServicesFragment newInstance(String param1, String param2) {
        FindServicesFragment fragment = new FindServicesFragment();
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


    public void loadIcons(View rv) {
        Context context = getActivity();

        ImageView doctorIcon = (ImageView) rv.findViewById(R.id.physicians); //get reference
        Picasso.with(context)
                .load(R.drawable.doctor_icon)
                .fit()
                .centerInside()
                .into(doctorIcon);
        ImageView nurseIcon = (ImageView) rv.findViewById(R.id.nurses);
        Picasso.with(context)
                .load(R.drawable.nurse_icon)
                .fit()
                .centerInside()
                .into(nurseIcon);
        ImageView counselorIcon = (ImageView) rv.findViewById(R.id.counselors);
        Picasso.with(context)
                .load(R.drawable.counselor_icon)
                .fit()
                .centerInside()
                .into(counselorIcon);
        ImageView labIcon = (ImageView) rv.findViewById(R.id.labs);
        Picasso.with(context)
                .load(R.drawable.lab_icon)
                .fit()
                .centerInside()
                .into(labIcon);
        ImageView chiropractorIcon = (ImageView) rv.findViewById(R.id.chiropractors);
        Picasso.with(context)
                .load(R.drawable.chiropractor_icon)
                .fit()
                .centerInside()
                .into(chiropractorIcon);
        ImageView insuranceIcon = (ImageView) rv.findViewById(R.id.insurance);
        Picasso.with(context)
                .load(R.drawable.insurance_icon)
                .fit()
                .centerInside()
                .into(insuranceIcon);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_services, container, false);
        try {
            loadIcons(rootView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
