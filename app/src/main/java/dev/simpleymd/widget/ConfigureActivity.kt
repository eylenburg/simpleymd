package dev.simpleymd.widget

import android.app.DatePickerDialog
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import dev.simpleymd.widget.databinding.ActivityConfigureBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ConfigureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigureBinding
    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var pickedDate: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        binding = ActivityConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        widgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        Prefs.load(this, widgetId)?.let { existing ->
            binding.inputLabel.setText(existing.label)
            pickedDate = existing.date
            binding.switchDark.isChecked = existing.dark
            binding.seekOpacity.progress = existing.opacity
        }

        refreshDateButton()
        refreshOpacityLabel()

        binding.btnDate.setOnClickListener { showDatePicker() }
        binding.seekOpacity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                refreshOpacityLabel()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.btnSave.setOnClickListener { saveAndFinish() }
    }

    private fun refreshDateButton() {
        val fmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        binding.btnDate.text = "Date: ${pickedDate.format(fmt)}"
    }

    private fun refreshOpacityLabel() {
        val pct = (binding.seekOpacity.progress * 100 / 255)
        binding.opacityLabel.text = "Background opacity: $pct%"
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, day ->
                pickedDate = LocalDate.of(year, month + 1, day)
                refreshDateButton()
            },
            pickedDate.year,
            pickedDate.monthValue - 1,
            pickedDate.dayOfMonth
        ).show()
    }

    private fun saveAndFinish() {
        val label = binding.inputLabel.text?.toString()?.trim().orEmpty().ifEmpty { "Since" }
        Prefs.save(
            this,
            widgetId,
            WidgetConfig(
                label = label,
                date = pickedDate,
                dark = binding.switchDark.isChecked,
                opacity = binding.seekOpacity.progress
            )
        )
        val mgr = AppWidgetManager.getInstance(this)
        SinceWidgetProvider.updateOne(this, mgr, widgetId)
        UpdateScheduler.scheduleNextMidnight(this)
        setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
        finish()
    }
}
