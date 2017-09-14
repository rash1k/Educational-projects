package ua.example.rash1k.cannongame;


import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainActivityFragment extends Fragment {

    private CannonView mCannonView;

    public MainActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mCannonView = (CannonView) rootView.findViewById(R.id.cannonView);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//       Allow the volume control buttons
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCannonView.stopGame();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Release sound resources
        mCannonView.releaseResources();
    }
}
