package com.dorsaydevelopment.convoy;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.facebook.UiLifecycleHelper;

/**
 * Created by brycen on 15-03-17.
 */
public class AuthenticationFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private UiLifecycleHelper uiHelper;

    public AuthenticationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(getActivity(), null);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout l = (LinearLayout) inflater.inflate(R.layout.fragment_authentication, container, false);

        ((Button) l.findViewById(R.id.to_login_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AuthenticationActivity) getActivity()).switchFragment(0);
            }
        });

        ((Button) l.findViewById(R.id.to_register_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AuthenticationActivity) getActivity()).switchFragment(2);
            }
        });

        return l;
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

    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
}
