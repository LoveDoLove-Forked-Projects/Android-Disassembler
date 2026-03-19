package com.kyhsgeekcode.disassembler.preference

import android.content.Context
import com.kyhsgeekcode.disassembler.MainActivity

object PowerUserModeSettings {
    const val POWER_USER_IMPORT_MODE_KEY = "power_user_import_mode"

    fun isEnabled(context: Context): Boolean {
        return context.getSharedPreferences(MainActivity.SETTINGKEY, Context.MODE_PRIVATE)
            .getBoolean(POWER_USER_IMPORT_MODE_KEY, false)
    }
}
