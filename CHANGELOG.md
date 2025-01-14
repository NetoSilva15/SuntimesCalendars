### ~

### v0.3.0 (2019-04-09)
* adds "moon" calendar (moonrise, moonset) (#9).
* adds "astronomical twilight", "nautical twilight", and "civil twilight" calendars (sunrise, sunset) (#9).
* enhances the progress UI; improved dialog and notifications.
* updates translations to Polish (pl) and Esperanto (eo) (#12 by Verdulo).
* now requires Suntimes v0.10.3+ (previously v0.10.0).

### v0.2.0 (2019-01-02)
* adds individual "enabled" prefs for each calendar (Solstices/Equinoxes, Moon Phases) (#3).
* fixes bug "missing calendars/events when closing app while task is still running" (#4); moves calendar notifications into a foreground service. 
* changes the "Calendar Integration" pref to match calendar state (vs desired state); existing calendars (and prefs) should be preserved when updating (or removing / re-adding) the app. 
* changes the strategy used when initializing calendars; existing calendars are no longer cleared prior to (re)adding; subsequent calls to "add" instead "update" a calendar (insert, replace).          
* changes when permissions are requested; adds a request on first launch (or if data is cleared) before allowing access to remaining UI; permissions are used to recover calendars from previous installations.
* misc improvements to permissions handling; more robust; support for actions on individual calendars.

### v0.1.1 (2018-12-12)
* fixes broken build (jcenter fails to resolve deps).
* updates Android gradle plugin to `com.android.tools.build:gradle:3.0.0` (#2).
* updates the gradle wrapper to `gradle 4.1` (#2).

### v0.1.0 (2018-12-06)
* refactors "Calendar Integration" to use the ContentProvider supplied by Suntimes.
* updates copyright notices ("SuntimesWidget" changed to "SuntimesCalendars"), actual copyright remain unchanged; Copyright (C) 2018 Forrest Guice.
* initial commit; imports existing GPLv3 code from `com.forrestguice.suntimeswidget.calendar` (from Suntimes v0.9.4, commit 1ef4dcd4009b8adedd4538ccbeddb07564111613).
