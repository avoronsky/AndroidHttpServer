package com.voronsky.androidhttpserver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.conscrypt.Conscrypt
import timber.log.Timber
import java.security.Security

class MainActivity : AppCompatActivity() {

    private var server: HttpServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        Security.insertProviderAt(Conscrypt.newProvider(), 1)
        if (hasStoragePermission()) {
            createAndStartServer()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                CODE_REQUEST_STORAGE_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CODE_REQUEST_STORAGE_PERMISSION
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            createAndStartServer()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        server?.stop()
    }

    private fun createAndStartServer() {
        server = HttpServer(DemoSslCredentials()).also { it.start() }
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private companion object {
        const val CODE_REQUEST_STORAGE_PERMISSION = 1
    }
}
