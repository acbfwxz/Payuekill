package com.example.payukiller

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.*
import java.io.File
import java.util.concurrent.Executors
import java.util.zip.ZipFile

class MainActivity : Activity() {
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var status: TextView
    private lateinit var results: TextView
    private lateinit var progress: ProgressBar

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        setContentView(R.layout.activity_main)
        status = findViewById(R.id.score)
        results = findViewById(R.id.results)
        progress = findViewById(R.id.progress)

        findViewById<Button>(R.id.scanButton).setOnClickListener { scanEverything() }
        findViewById<Button>(R.id.appsButton).setOnClickListener { scanApps() }
        findViewById<Button>(R.id.filesButton).setOnClickListener { scanFiles() }
        findViewById<Button>(R.id.settingsButton).setOnClickListener { openAllFilesAccess() }
    }

    private fun busy(on: Boolean, msg: String) {
        runOnUiThread {
            progress.visibility = if (on) ProgressBar.VISIBLE else ProgressBar.GONE
            status.text = msg
        }
    }

    private fun scanEverything() {
        busy(true, "⚡ กำลังสแกนแอป + ไฟล์...")
        executor.execute {
            val appReport = inspectApps()
            val fileReport = inspectFiles(Environment.getExternalStorageDirectory())
            runOnUiThread {
                busy(false, "สแกนเสร็จ")
                results.text = appReport + "\n\n" + fileReport +
                    "\n\n⚠️ ผลนี้เป็น heuristic scanner ไม่ใช่การยืนยันมัลแวร์ 100%"
            }
        }
    }

    // เพิ่มฟังก์ชันนี้
    private fun scanFiles() {
        busy(true, "กำลังตรวจไฟล์ทั้งหมด...")
        executor.execute {
            val report = inspectFiles(Environment.getExternalStorageDirectory())
            runOnUiThread {
                busy(false, "ตรวจไฟล์เสร็จ")
                results.text = report
            }
        }
    }

    private fun scanApps() {
        busy(true, "กำลังตรวจแอปทั้งหมด...")
        executor.execute {
            val report = inspectApps()
            runOnUiThread { busy(false, "ตรวจแอปเสร็จ"); results.text = report }
        }
    }

    private fun inspectApps(): String {
        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val suspicious = mutableListOf<String>()
        val keywords = listOf("virus","malware","trojan","spyware","keylogger","crack","hack","modmenu")
        for (a in apps) {
            if ((a.flags and ApplicationInfo.FLAG_SYSTEM) != 0) continue
            val label = pm.getApplicationLabel(a).toString()
            val pkg = a.packageName.lowercase()
            val name = label.lowercase()
            if (keywords.any { name.contains(it) || pkg.contains(it) }) {
                suspicious += "⚠️ $label\n$pkg"
            }
        }
        return "📱 แอปที่ตรวจ: ${apps.size}\n" +
            if (suspicious.isEmpty()) "ไม่พบชื่อแอปที่เข้าข่ายจากกฎเบื้องต้น"
            else "รายการที่ควรตรวจสอบ:\n\n" + suspicious.joinToString("\n\n")
    }

    private fun inspectFiles(root: File): String {
        if (!Environment.isExternalStorageManager()) {
            return "📁 ยังไม่มีสิทธิ์เข้าถึงไฟล์ทั้งหมด\nกดปุ่ม “เปิดสิทธิ์เข้าถึงไฟล์ทั้งหมด” แล้วสแกนอีกครั้ง"
        }
        var count = 0
        val hits = mutableListOf<String>()
        val suspiciousNames = listOf("keylogger","stealer","rat","trojan","malware","payload","crack","hacktool")
        fun walk(f: File, depth: Int) {
            if (depth > 18 || count > 50000) return
            val list = try { f.listFiles() } catch (_: Exception) { null } ?: return
            for (x in list) {
                if (count > 50000) return
                count++
                val n = x.name.lowercase()
                if (suspiciousNames.any { n.contains(it) }) hits += "⚠️ ${x.absolutePath}"
                if (x.isDirectory && !x.isHidden) walk(x, depth + 1)
                else if (x.isFile && n.endsWith(".apk")) {
                    try {
                        ZipFile(x).use { z ->
                            val names = z.entries().asSequence().take(2000).map { it.name.lowercase() }
                            if (names.any { it.contains("payload") || it.contains("keylogger") || it.contains("stealer") })
                                hits += "⚠️ APK น่าสงสัย: ${x.absolutePath}"
                        }
                    } catch (_: Exception) {}
                }
            }
        }
        walk(root, 0)
        return "📁 ไฟล์/โฟลเดอร์ที่ตรวจ: $count\n" +
            if (hits.isEmpty()) "ไม่พบรายการที่เข้าข่ายจากกฎเบื้องต้น"
            else "รายการที่ควรตรวจสอบ:\n\n" + hits.take(100).joinToString("\n\n")
    }

    private fun openAllFilesAccess() {
        try {
            startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.parse("package:$packageName")))
        } catch (_: Exception) {
            startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
        }
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
