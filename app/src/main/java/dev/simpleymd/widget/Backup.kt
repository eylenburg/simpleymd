package com.github.eylenburg.simpleymd

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object Backup {
    fun exportJson(context: Context): String {
        val mgr = AppWidgetManager.getInstance(context)
        val ids = mgr.getAppWidgetIds(ComponentName(context, SinceWidgetProvider::class.java))
        val arr = JSONArray()
        ids.sorted().forEach { id ->
            val cfg = Prefs.load(context, id) ?: return@forEach
            arr.put(
                JSONObject()
                    .put("label", cfg.label)
                    .put("date", cfg.date.toString())
                    .put("dark", cfg.dark)
                    .put("opacity", cfg.opacity)
            )
        }
        return arr.toString(2)
    }

    fun importJson(context: Context, text: String): Int {
        val arr = JSONArray(text.trim())
        val mgr = AppWidgetManager.getInstance(context)
        val ids = mgr.getAppWidgetIds(ComponentName(context, SinceWidgetProvider::class.java)).sorted()
        var applied = 0
        for (i in 0 until minOf(arr.length(), ids.size)) {
            val obj = arr.getJSONObject(i)
            val cfg = WidgetConfig(
                label = obj.optString("label", "Since"),
                date = java.time.LocalDate.parse(obj.getString("date")),
                dark = obj.optBoolean("dark", true),
                opacity = obj.optInt("opacity", 204)
            )
            Prefs.save(context, ids[i], cfg)
            SinceWidgetProvider.updateOne(context, mgr, ids[i])
            applied++
        }
        return applied
    }
}
