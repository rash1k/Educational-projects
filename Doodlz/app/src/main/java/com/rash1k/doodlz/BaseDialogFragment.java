package com.rash1k.doodlz;

import android.content.Context;
import android.support.v4.app.DialogFragment;


public abstract class BaseDialogFragment extends DialogFragment {

    protected MainActivityFragment getDoodleFragment() {
        return (MainActivityFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.doodleFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivityFragment fragment = getDoodleFragment();

        if (fragment != null) {
            fragment.setDialogOnScreen(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        MainActivityFragment fragment = getDoodleFragment();

        if (fragment != null) {
            fragment.setDialogOnScreen(false);
        }
    }
}
