package com.github.eylenburg.simpleymd

import android.content.Context
import java.time.LocalDate

data class WidgetConfig(
    val label: String,
    val date: LocalDate,
    val dark: Boolean,
    val opacity: Int
)

object Prefs {
    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences("widgets", Context.MODE_PRIVATE)

    fun save(ctx: Context, appWidgetId: Int, cfg: WidgetConfig) {
        prefs(ctx).edit()
            .putString(key(appWidgetId, "label"), cfg.label)
            .putString(key(appWidgetId, "date"), cfg.date.toString())
            .putBoolean(key(appWidgetId, "dark"), cfg.dark)
            .putInt(key(appWidgetId, "opacity"), cfg.opacity)
            .apply()
    }

    fun load(ctx: Context, appWidgetId: Int): WidgetConfig? {
        val p = prefs(ctx)
        val dateStr = p.getString(key(appWidgetId, "date"), null) ?: return null
        return WidgetConfig(
            label = p.getString(key(appWidgetId, "label"), "") ?: "",
            date = LocalDate.parse(dateStr),
            dark = p.getBoolean(key(appWidgetId, "dark"), true),
            opacity = p.getInt(key(appWidgetId, "opacity"), 204)
        )
    }

    fun delete(ctx: Context, appWidgetId: Int) {
        prefs(ctx).edit()
            .remove(key(appWidgetId, "label"))
            .remove(key(appWidgetId, "date"))
            .remove(key(appWidgetId, "dark"))
            .remove(key(appWidgetId, "opacity"))
            .apply()
    }

    private fun key(id: Int, field: String) = "w_${id}_$field"
}
