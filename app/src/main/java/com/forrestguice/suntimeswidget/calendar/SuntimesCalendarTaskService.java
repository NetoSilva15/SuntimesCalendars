/**
    Copyright (C) 2018 Forrest Guice
    This file is part of SuntimesCalendars.

    SuntimesCalendars is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SuntimesCalendars is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SuntimesCalendars.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimeswidget.calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class SuntimesCalendarTaskService extends Service
{
    public static final String TAG = "SuntimesCalendarsTask";
    public static final String ACTION_UPDATE_CALENDARS = "update_calendars";
    public static final String ACTION_CLEAR_CALENDARS = "clear_calendars";

    public static final String EXTRA_CALENDAR_ITEMS = "calendar_items";
    public static final String EXTRA_CALENDAR_LISTENER = "calendar_listener";
    public static final String EXTRA_SERVICE_LISTENER = "service_listener";

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return taskBinder;
    }

    private final SuntimesCalendarTaskServiceBinder taskBinder = new SuntimesCalendarTaskServiceBinder();
    public class SuntimesCalendarTaskServiceBinder extends Binder
    {
        SuntimesCalendarTaskService getService() {
            return SuntimesCalendarTaskService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String action = intent.getAction();
        if (action != null)
        {
            SuntimesCalendarServiceListener serviceListener = intent.getParcelableExtra(EXTRA_SERVICE_LISTENER);
            SuntimesCalendarTask.SuntimesCalendarTaskListener listener = intent.getParcelableExtra(EXTRA_CALENDAR_LISTENER);
            if (action.equals(ACTION_UPDATE_CALENDARS))
            {
                Log.d(TAG, "onStartCommand: " + action);
                boolean started = runCalendarTask(this, intent, false, false, listener);
                if (serviceListener != null) {
                    serviceListener.onStartCommand(started);
                }

            } else if (action.equals(ACTION_CLEAR_CALENDARS)) {
                Log.d(TAG, "onStartCommand: " + action);
                boolean started = runCalendarTask(this, intent, true, false, listener);
                if (serviceListener != null) {
                    serviceListener.onStartCommand(started);
                }

            } else Log.d(TAG, "onStartCommand: unrecognized action: " + action);
        } else Log.d(TAG, "onStartCommand: null action");
        return START_NOT_STICKY;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private static SuntimesCalendarTask calendarTask = null;
    private static SuntimesCalendarTask.SuntimesCalendarTaskListener calendarTaskListener;
    public boolean runCalendarTask(final Context context, Intent intent, boolean clearCalendars, boolean clearPending, @Nullable SuntimesCalendarTask.SuntimesCalendarTaskListener listener)
    {
        ArrayList<SuntimesCalendarTask.SuntimesCalendarTaskItem> items = new ArrayList<>();
        if (!clearCalendars) {
            items = loadItems(intent, clearPending);
        }

        if (calendarTask != null)
        {
            switch (calendarTask.getStatus())
            {
                case PENDING:
                    Log.w(TAG, "runCalendarTask: A task is already pending! ignoring...");
                    return false;

                case RUNNING:
                    Log.w(TAG, "runCalendarTask: A task is already running! ignoring...");
                    return false;
            }
        }

        calendarTask = new SuntimesCalendarTask(context);
        calendarTaskListener = (listener != null) ? listener : new SuntimesCalendarTask.SuntimesCalendarTaskListener()
        {
            @Override
            public void onStarted(Context context, SuntimesCalendarTask task, String message) {
            }

            @Override
            public void onSuccess(Context context, SuntimesCalendarTask task, String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(final Context context, final String errorMsg)
            {
                super.onFailed(context, errorMsg);
                Intent errorIntent = new Intent(context, SuntimesCalendarErrorActivity.class);
                errorIntent.putExtra(SuntimesCalendarErrorActivity.EXTRA_ERROR_MESSAGE, errorMsg);
                errorIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(errorIntent);
            }
        };
        calendarTask.setTaskListener(calendarTaskListener);

        if (clearCalendars) {
            calendarTask.setFlagClearCalendars(true);
        }
        calendarTask.setItems(items.toArray(new SuntimesCalendarTask.SuntimesCalendarTaskItem[0]));
        calendarTask.execute();
        return true;
    }

    public static ArrayList<SuntimesCalendarTask.SuntimesCalendarTaskItem> loadItems(Intent intent, boolean clearPending)
    {
        SuntimesCalendarTask.SuntimesCalendarTaskItem[] items;
        Parcelable[] parcelableArray = intent.getParcelableArrayExtra(EXTRA_CALENDAR_ITEMS);
        if (parcelableArray != null) {
            items = Arrays.copyOf(parcelableArray, parcelableArray.length, SuntimesCalendarTask.SuntimesCalendarTaskItem[].class);
        } else items = new SuntimesCalendarTask.SuntimesCalendarTaskItem[0];

        if (clearPending) {
            intent.removeExtra(EXTRA_CALENDAR_ITEMS);
        }
        return new ArrayList<>(Arrays.asList(items));
    }

    /**
     * SuntimesCalendarServiceListener
     */
    public static abstract class SuntimesCalendarServiceListener implements Parcelable
    {
        public void onStartCommand(boolean result) {}

        public SuntimesCalendarServiceListener() {}
        protected SuntimesCalendarServiceListener(Parcel in) {}

        @Override
        public void writeToParcel(Parcel dest, int flags) {}

        @Override
        public int describeContents() {
            return 0;
        }
    }

}
