package com.scysun.app.ui.view;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scysun.app.R;
import com.scysun.app.core.Constants;
import com.scysun.app.ui.view.Listener.DatePickerListener;
import com.scysun.app.util.FBDateUtils;
import com.scysun.app.util.PreferenceUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import java.util.Calendar;
import java.util.Date;

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

    @InjectView(R.id.ageOfRetire) protected TextView ageOfRetire;
    @InjectView(R.id.workingTimeLeft) protected TextView workingTimeLeft;

    @InjectView(R.id.ageOfDeath) protected TextView ageOfDeath;
    @InjectView(R.id.timeLeft) protected TextView timeLeft;

    @InjectView(R.id.liveChronometer) protected TextView liveChronometer;

    private TimeLeftTextWatcher timeLeftTextWatcher = new TimeLeftTextWatcher();
    private LiveChronometerUpdater liveChronometerUpdater;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String CALCULATE_TYPE_DEATH = "DEATH";
    private static final String CALCULATE_TYPE_RETIREMENT = "RETIREMENT";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Activity activity;

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
        birthday.addTextChangedListener(timeLeftTextWatcher);

        ageOfRetire.addTextChangedListener(timeLeftTextWatcher);

        ageOfDeath.addTextChangedListener(timeLeftTextWatcher);

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
        this.activity = activity;

        super.onAttach(activity);
        Log.i("Test", "onAttach");
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
    public void onStart() {
        super.onStart();

        Log.i("Test", "onStart");
        String birthdayStr = PreferenceUtils.readSharedPreference(activity, Constants.SharedPreferences_LiveChronometer.NAME,
                Constants.SharedPreferences_LiveChronometer.BIRTHDAY, "1980-01-01");

        int ageOfRetirementInt = PreferenceUtils.readSharedPreferenceIntVariable(activity, Constants.SharedPreferences_LiveChronometer.NAME,
                Constants.SharedPreferences_LiveChronometer.AGE_OF_RETIREMENT, 60);
        ageOfRetire.setText(String.valueOf(ageOfRetirementInt));

        int ageOfDeathInt = PreferenceUtils.readSharedPreferenceIntVariable(activity, Constants.SharedPreferences_LiveChronometer.NAME,
                Constants.SharedPreferences_LiveChronometer.AGE_OF_DEATH, 80);
        ageOfDeath.setText(String.valueOf(ageOfDeathInt));
        birthday.setText(birthdayStr);
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
    private long calculateDateToLive(String type)
    {
        long result = 0l;

        String birthdayStr = birthday.getText().toString();
        String ageOfRetireStr = ageOfRetire.getText().toString();
        String ageOfDeathStr = ageOfDeath.getText().toString();
        try {
            if (StringUtils.isNotEmpty(birthdayStr)) {
                if(CALCULATE_TYPE_DEATH.equals(type) && StringUtils.isNotEmpty(ageOfDeathStr)) {
                    Long[] timeToLive = this.countRestDaytime(Integer.parseInt(ageOfDeathStr), birthdayStr, true);
                    result = timeToLive[0];

                    //Display the rest days to live
                    timeLeft.setText(String.valueOf(result) + " " + getString(R.string.unit_day));

                    //Display the rest time
                    final long timeMillis = timeToLive[1];
                    if (liveChronometerUpdater != null) {
                        liveChronometerUpdater.cancel();
                    }
                    liveChronometerUpdater = new LiveChronometerUpdater(timeMillis, 1000);
                    liveChronometerUpdater.start();
                }
                else if(CALCULATE_TYPE_RETIREMENT.equals(type) && StringUtils.isNotEmpty(ageOfRetireStr)) {
                    Long[] timeToLive = this.countRestDaytime(Integer.parseInt(ageOfRetireStr), birthdayStr, false);
                    result = timeToLive[0];
                    //Display the rest days to work
                    workingTimeLeft.setText(String.valueOf(result) + " " + getString(R.string.unit_day));
                }
            }

        } catch (DateParseException e) {
                Log.e("date-format", "Wrong date format that can't be parsed:" + birthdayStr);
                timeLeft.setText("");
        }
        return result;
    }

    private Long[] countRestDaytime(int years, String birthdayStr, boolean calTimeMillis) throws DateParseException
    {
        Long result[] = new Long[2];

        Date birth = DateUtils.parseDate(birthdayStr, new String[]{Constants.DateFormat.DATE_FORMAT});
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(birth);
        calendar.add(Calendar.YEAR, years);

        long currTimeMillis = System.currentTimeMillis();
        result[0] = (calendar.getTimeInMillis() - currTimeMillis) / Constants.DateFormat.TIME_MILLIS_PER_DAY;
        if(result[0]<0){
            result[0] = 0l;
        }

        if(calTimeMillis) {
            result[1] = (calendar.getTimeInMillis() - currTimeMillis) % Constants.DateFormat.TIME_MILLIS_PER_DAY;
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

    private class TimeLeftOnFocusChangeListener implements View.OnFocusChangeListener
    {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if(!hasFocus){
                calculateDateToLive();
            }
        }
    }*/

    private class TimeLeftTextWatcher implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            //do nothing
        }
        @Override
        public void afterTextChanged(Editable s) {
            SharedPreferences.Editor editor = null;

            if(StringUtils.isNotEmpty(birthday.getText().toString())) {
                editor = PreferenceUtils.writeSharedPreference(activity, Constants.SharedPreferences_LiveChronometer.NAME,
                        Constants.SharedPreferences_LiveChronometer.BIRTHDAY,
                        birthday.getText().toString(), true);
                calculateDateToLive(CALCULATE_TYPE_DEATH);
                calculateDateToLive(CALCULATE_TYPE_RETIREMENT);
            }

            if(StringUtils.isNotEmpty(ageOfDeath.getText().toString())) {
                editor = PreferenceUtils.writeSharedPreferenceIntValue(activity, Constants.SharedPreferences_LiveChronometer.NAME,
                        Constants.SharedPreferences_LiveChronometer.AGE_OF_DEATH,
                        Integer.parseInt(ageOfDeath.getText().toString()), true);
                calculateDateToLive(CALCULATE_TYPE_DEATH);
            }

            if(StringUtils.isNotEmpty(ageOfRetire.getText().toString())) {
                editor = PreferenceUtils.writeSharedPreferenceIntValue(activity, Constants.SharedPreferences_LiveChronometer.NAME,
                        Constants.SharedPreferences_LiveChronometer.AGE_OF_RETIREMENT,
                        Integer.parseInt(ageOfRetire.getText().toString()), true);
                calculateDateToLive(CALCULATE_TYPE_RETIREMENT);
            }

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){
            //do nothing
        }
    }
}
