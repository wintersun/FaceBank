package com.scysun.app.ui.view.Listener;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.scysun.app.core.Constants;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by PhoenixBack on 2014/10/2.
 */
public class DatePickerListener implements View.OnClickListener
{
    private TextView targetTextView;

    public DatePickerListener(TextView targetTextView){
        this.targetTextView = targetTextView;
    }

    private Calendar m_Calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            m_Calendar.set(Calendar.YEAR, year);
            m_Calendar.set(Calendar.MONTH, monthOfYear);
            m_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = Constants.DateFormat.DATE_FORMAT;
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
            targetTextView.setText(sdf.format(m_Calendar.getTime()));
        }
    };

    @Override
    public void onClick(View view) {
        DatePickerDialog dialog = new DatePickerDialog(targetTextView.getContext(), datePickerListener,
                1980, 0, 1);

        try {
            dialog.getDatePicker().setMinDate(DateUtils.parseDate("1900-01-01").getTime());
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        } catch (DateParseException e) {
            e.printStackTrace();
        }

        dialog.show();
    }
}
