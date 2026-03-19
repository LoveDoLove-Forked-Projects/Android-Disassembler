package com.kyhsgeekcode.disassembler.project

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProjectDataStorageCachePolicyTest {
    @Test
    fun `shouldCacheFileContent caches small files`() {
        assertTrue(shouldCacheFileContent(1024))
        assertTrue(shouldCacheFileContent(MAX_CACHED_FILE_CONTENT_BYTES))
    }

    @Test
    fun `shouldCacheFileContent skips large files`() {
        assertFalse(shouldCacheFileContent(MAX_CACHED_FILE_CONTENT_BYTES + 1L))
        assertFalse(shouldCacheFileContent(150L * 1024 * 1024))
    }
}
