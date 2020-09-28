package com.voronsky.androidhttpserver

import android.os.Environment
import java.io.File
import java.security.KeyStore

class DemoSslCredentials : SslCredentials {

    override fun getKeyStoreFile(): File {
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "demo_keystore.bks"
        )
    }

    override fun getKeyStore(): KeyStore {
        return KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            getKeyStoreFile().inputStream().use {
                load(it, getKeyPassword().toCharArray())
            }
        }
    }

    override fun getKeyAlias(): String {
        return ALIAS
    }

    override fun getKeyPassword(): String {
        return PASSWORD
    }

    override fun getAliasPassword(): String {
        return PASSWORD
    }

    private companion object {
        const val ALIAS = "demo_alias"
        const val PASSWORD = "demo_password"
    }
}
