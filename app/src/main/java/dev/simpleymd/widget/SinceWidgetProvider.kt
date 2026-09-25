package dev.simpleymd.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.RemoteViews

class SinceWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { updateOne(context, appWidgetManager, it) }
        UpdateScheduler.scheduleNextMidnight(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_BOOT_COMPLETED,
            UpdateScheduler.ACTION_MIDNIGHT -> {
                val mgr = AppWidgetManager.getInstance(context)
                val ids = mgr.getAppWidgetIds(ComponentName(context, SinceWidgetProvider::class.java))
                onUpdate(context, mgr, ids)
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        appWidgetIds.forEach { Prefs.delete(context, it) }
    }

    companion object {
        fun updateOne(context: Context, mgr: AppWidgetManager, id: Int) {
            try {
                val cfg = Prefs.load(context, id)
                val views = RemoteViews(context.packageName, R.layout.widget_since)
                if (cfg == null) {
                    views.setTextViewText(R.id.widget_label, "Tap to configure")
                    views.setTextViewText(R.id.widget_value, "0Y 0M 0D")
                } else {
                    views.setTextViewText(R.id.widget_label, cfg.label)
                    views.setTextViewText(R.id.widget_value, DateMath.formatSince(cfg.date))
                    val alpha = cfg.opacity.coerceIn(0, 255)
                    val bg = if (cfg.dark) Color.argb(alpha, 0, 0, 0) else Color.argb(alpha, 255, 255, 255)
                    val fg = if (cfg.dark) Color.WHITE else Color.BLACK
                    val line = if (cfg.dark) Color.argb(102, 255, 255, 255) else Color.argb(102, 0, 0, 0)
                    views.setInt(R.id.widget_root, "setBackgroundColor", bg)
                    views.setInt(R.id.widget_divider, "setBackgroundColor", line)
                    views.setTextColor(R.id.widget_label, fg)
                    views.setTextColor(R.id.widget_value, fg)
                }
                val cfgIntent = Intent(context, ConfigureActivity::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                val pi = PendingIntent.getActivity(
                    context,
                    id,
                    cfgIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pi)
                mgr.updateAppWidget(id, views)
            } catch (e: Exception) {
                Log.e("SimpleYMD", "widget update failed", e)
            }
        }
    }
}
