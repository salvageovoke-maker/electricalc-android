package com.electricalc.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoritesPersistenceTest {
    @Test fun encodesAndDecodesFavoriteIds() {
        val ids = listOf("ohms", "threepower", "conv_voltage")
        assertEquals(ids, decodeFavoriteIds(encodeFavoriteIds(ids)))
    }

    @Test fun removesDuplicateFavoriteIdsWhenEncoding() {
        assertEquals(listOf("ohms", "parallel"), decodeFavoriteIds(encodeFavoriteIds(listOf("ohms", "ohms", "parallel"))))
    }

    @Test fun ignoresBlankIds() {
        assertEquals(listOf("ohms"), decodeFavoriteIds(encodeFavoriteIds(listOf("ohms", "", "  "))))
    }

    @Test fun invalidStoredDataReturnsEmptyFavorites() {
        assertTrue(decodeFavoriteIds("not-valid-json").isEmpty())
        assertTrue(decodeFavoriteIds(null).isEmpty())
    }
}
