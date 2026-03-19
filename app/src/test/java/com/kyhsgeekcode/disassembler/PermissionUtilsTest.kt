package com.kyhsgeekcode.disassembler

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test

class PermissionUtilsTest {
    @Test
    fun `storagePermissionsForSdk keeps legacy storage permissions on Android 9 and below`() {
        assertArrayEquals(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            storagePermissionsForSdk(28)
        )
    }

    @Test
    fun `storagePermissionsForSdk requests no legacy storage permissions on Android 10 and above`() {
        assertArrayEquals(emptyArray(), storagePermissionsForSdk(29))
        assertArrayEquals(emptyArray(), storagePermissionsForSdk(35))
    }
}
