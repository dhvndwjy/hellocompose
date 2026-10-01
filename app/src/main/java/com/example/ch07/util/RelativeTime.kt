package com.example.ch07.util
fun formatRelativeTime(now: Long, then: Long): String {
    val diffMillis = now - then
    if (diffMillis <= 0)
        return "baru saja"

    val seconds = diffMillis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val months = days / 30
    val years = days / 365

    return when{
        minutes < 1 -> "baru saja"
        hours < 1 -> "$minutes menit lalu"
        days < 1 -> "$hours jam lalu"
        days < 30 -> "$days hari lalu"
        days < 365 -> "$months bulan lalu"
        else -> "$years tahun lalu"
    }
}