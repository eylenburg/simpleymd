# Simple YMD

Minimal FOSS Android homescreen widget:

```
Childs birthday:
1 year 3 months 18 days
```

- Default size 2×1; resizeable if the launcher allows it
- Per-widget label and start date
- Dark (black bg / white text) or light (white bg / black text)
- Background opacity slider
- Count uses calendar years/months/days (`java.time.Period`)
- Updates via widget period, `DATE_CHANGED`, boot, and a midnight exact alarm
- MIT license
- No network, no ads, no tracking

## Build

1. Install Android Studio (or SDK + JDK 17).
2. Open this folder as a project.
3. Let Gradle sync, then Build → Build APK / Run on device.
4. After install: long-press home screen → Widgets → Simple YMD.
5. Settings → Apps → Simple YMD → Battery → **Unrestricted**.
6. On Android 12+: allow Alarms if the system asks.

Tap the widget later to edit label/date/theme.

`updatePeriodMillis` is 30 minutes as a backup. The important refresh is midnight + date-changed.

## Why widgets freeze on other apps

Android stops background work. This project schedules an exact alarm for ~00:01 local time and also listens for `DATE_CHANGED`. OEM battery savers can still kill that unless the app is unrestricted.
