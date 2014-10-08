package com.scysun.app.ui.view;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scysun.app.R;
import com.scysun.app.core.Constants;
import com.scysun.app.ui.view.Listener.DatePickerListener;
import com.scysun.app.util.FBDateUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;
import butterknife.Views;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class HomeFragment extends Fragment
{
    @InjectView(R.id.birthday) protected TextView birthday;
    @InjectView(R.id.ageOfDeath) protected TextView ageOfDeath;
    @InjectView(R.id.timeLeft) protected TextView timeLeft;
    @InjectView(R.id.liveChronometer) protected TextView liveChronometer;

    private TimeLeftOnFocusChangeListener timeLeftOnFocusChangeListener = new TimeLeftOnFocusChangeListener();
    private LiveChronometerUpdater liveChronometerUpdater;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Views.inject(this, view);

        birthday.setInputType(InputType.TYPE_NULL);
        birthday.setOnClickListener(new DatePickerListener(birthday));

        birthday.setOnFocusChangeListener(timeLeftOnFocusChangeListener);
//        ageOfDeath.setOnFocusChangeListener(timeLeftOnFocusChangeListener);

        ageOfDeath.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //do nothing
            }
            @Override
            public void afterTextChanged(Editable s) {
                calculateDateToLive();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
                //do nothing
            }
        });

        timeLeft.setInputType(InputType.TYPE_NULL);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        /*
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /***
     * Calculate the date to live if Birthday and AgeOfDeath are all ready
     * @return days of rest life
     */
    private long calculateDateToLive()
    {
        long result = 0l;

        String birdayStr = birthday.getText().toString();
        if(StringUtils.isNotEmpty(birdayStr)
                && StringUtils.isNotEmpty(ageOfDeath.getText().toString()))
        {
            try {
                Date birth = DateUtils.parseDate(birdayStr, new String[]{Constants.DateFormat.DATE_FORMAT});
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(birth);
                calendar.add(Calendar.YEAR, Integer.parseInt(ageOfDeath.getText().toString()));

                long currTimeMillis = System.currentTimeMillis();
                result = (calendar.getTimeInMillis() - currTimeMillis) / Constants.DateFormat.TIME_MILLIS_PER_DAY;

                //Display the rest days to live
                timeLeft.setText(
//                        getString(R.string.timeLeft) + " " +
                        String.valueOf(result) + " " + getString(R.string.unit_day));

                //Display the rest time
                final long timeMillis = (calendar.getTimeInMillis() - currTimeMillis) % Constants.DateFormat.TIME_MILLIS_PER_DAY;

                if(liveChronometerUpdater!=null){
                    liveChronometerUpdater.cancel();
                }
                liveChronometerUpdater = new LiveChronometerUpdater(timeMillis, 1000);
                liveChronometerUpdater.start();

            } catch (DateParseException e) {
                Log.e("date-format", "Wrong date format that can't be parsed:" + birdayStr);
                timeLeft.setText("");
            }
        }

        return result;
    }

    private class LiveChronometerUpdater extends android.os.CountDownTimer{
        public LiveChronometerUpdater(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }
        public void onTick(long millisUntilFinished) {
            liveChronometer.setText(FBDateUtils.formatTime(millisUntilFinished));
        }
        public void onFinish() {

        }
    }


    /***
     * Listener for Birthday/AgeOfDeath changed
     */
    private class TimeLeftOnFocusChangeListener implements View.OnFocusChangeListener
    {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if(!hasFocus){
                calculateDateToLive();
            }
        }
    }
}
