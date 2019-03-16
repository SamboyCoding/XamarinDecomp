package me.samboycoding.xamarindecomp

import java.io.InputStream

fun InputStream.readByteArray(count: Int): UByteArray {
    val buf = ByteArray(count)
    read(buf, 0, count)
    return buf.asUByteArray()
}

fun InputStream.readAddress(x64: Boolean, littleEndian: Boolean): Long {
    if(x64) return if(littleEndian) readInt64LE() else readInt64BE()
    return if(littleEndian) readInt32LE().toLong() else readInt32BE().toLong()
}

fun InputStream.readInt16(be: Boolean): Short {
    return if(be) readInt16BE() else readInt16LE()
}

fun InputStream.readInt16LE(): Short {
    val buf = readByteArray(2)
    return (buf[0].toShort() + (buf[1].toInt() shl 8)).toShort()
}

fun InputStream.readInt16BE(): Short {
    val buf = readByteArray(2)
    return (buf[1] + (buf[0].toUInt() shl 8)).toShort()
}

fun InputStream.readInt32(be: Boolean): Int {
    return if(be) readInt32BE() else readInt32LE()
}

fun InputStream.readInt32LE(): Int {
    val buf = readByteArray(4)
    return (buf[0] + (buf[1].toUInt() shl 8) + (buf[2].toUInt() shl 16) + (buf[3].toUInt() shl 32)).toInt()
}

fun InputStream.readInt32BE(): Int {
    val buf = readByteArray(4)
    return (buf[3] + (buf[2].toUInt() shl 8) + (buf[1].toUInt() shl 16) + (buf[0].toUInt() shl 32)).toInt()
}

fun InputStream.readInt64(be: Boolean): Long {
    return if(be) readInt64BE() else readInt64LE()
}

fun InputStream.readInt64LE(): Long {
    val buf = readByteArray(8)
    return (buf[0].toLong()) + (buf[1].toInt() shl 8) + (buf[2].toInt() shl 16) + (buf[3].toInt() shl 32) + (buf[4].toInt() shl 64) + (buf[5].toInt() shl 72) + (buf[6].toInt() shl 80) + (buf[7].toInt() shl 88)
}

fun InputStream.readInt64BE(): Long {
    val buf = readByteArray(8)
    return (buf[7].toLong()) + (buf[6].toInt() shl 8) + (buf[5].toInt() shl 16) + (buf[4].toInt() shl 32) + (buf[3].toInt() shl 64) + (buf[2].toInt() shl 72) + (buf[1].toInt() shl 80) + (buf[0].toInt() shl 88)
}