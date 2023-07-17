package com.example.vibees.utils

import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.UUID

fun hashToUUID(input: String): UUID {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(input.toByteArray())

    // Convert the hash bytes to a valid UUID format
    val mostSigBits = ByteBuffer.wrap(hashBytes.sliceArray(0..7)).long
    val leastSigBits = ByteBuffer.wrap(hashBytes.sliceArray(8..15)).long

    return UUID(mostSigBits, leastSigBits)
}