package dev.simpleymd.widget

import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import dev.simpleymd.widget.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var askedAlarmsThisProcess = false

    private val createFile = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@registerForActivityResult
        try {
            contentResolver.openOutputStream(uri)?.use { out ->
                out.write(Backup.exportJson(this).toByteArray(Charsets.UTF_8))
            }
            Toast.makeText(this, "Exported", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private val openFile = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@registerForActivityResult
        try {
            val text = contentResolver.openInputStream(uri)?.use {
                it.readBytes().toString(Charsets.UTF_8)
            } ?: return@registerForActivityResult
            val n = Backup.importJson(this, text)
            Toast.makeText(this, "Imported onto $n widget(s)", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnExport.setOnClickListener {
            createFile.launch("simpleymd-backup.json")
        }
        binding.btnImport.setOnClickListener {
            openFile.launch(arrayOf("application/json", "text/plain", "*/*"))
        }
    }

    override fun onResume() {
        super.onResume()
        requestExactAlarmsIfNeeded()
    }

    private fun requestExactAlarmsIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val am = getSystemService(AlarmManager::class.java) ?: return
        if (am.canScheduleExactAlarms()) return
        if (askedAlarmsThisProcess) return
        askedAlarmsThisProcess = true
        startActivity(
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:$packageName")
            }
        )
    }
}
