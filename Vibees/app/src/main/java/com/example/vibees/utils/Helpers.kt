package com.example.vibees.utils

import java.net.URLDecoder
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

fun hashToUUID(input: String): UUID {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(input.toByteArray())

    // Convert the hash bytes to a valid UUID format
    val mostSigBits = ByteBuffer.wrap(hashBytes.sliceArray(0..7)).long
    val leastSigBits = ByteBuffer.wrap(hashBytes.sliceArray(8..15)).long

    return UUID(mostSigBits, leastSigBits)
}
fun extractLastTwoUUIDs(input: String): List<String> {
    val uuidPattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}".toRegex()
    val uuids = uuidPattern.findAll(input).map { it.value }.toList()
    return uuids.takeLast(2)
}
fun decodeValue(encodedValue: String): String {
    return URLDecoder.decode(encodedValue, StandardCharsets.UTF_8.name())
}

fun parseDate(dateTimeString: String): Date {
    val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
    return format.parse(dateTimeString)!!
}
fun formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("dd MMM yy", Locale.ENGLISH)
    return dateFormat.format(date)
}

fun formatTime(date: Date): String {
    val timeFormat = SimpleDateFormat("HH:mm a", Locale.ENGLISH)
    return timeFormat.format(date)
}
