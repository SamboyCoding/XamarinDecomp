package me.samboycoding.xamarindecomp.elf

import me.samboycoding.xamarindecomp.getSafe
import me.samboycoding.xamarindecomp.readInt16
import me.samboycoding.xamarindecomp.readInt32
import me.samboycoding.xamarindecomp.readInt64
import kotlin.experimental.and

data class SymbolTableEntry64(
    val nameAddress: Int,
    val info: Byte,
    val visibility: Byte,
    val sectionHeaderIndex: Short,
    val value: Long,
    val size: Long,
    val type: STEType? = STEType.values().getSafe((info and 0xF).toInt()),
    var name: String = ""
) {
    constructor(bytes: ByteArray, index: Int, be: Boolean) : this(
        bytes.readInt32(index, be),
        bytes[index + 4], bytes[index + 5],
        bytes.readInt16(index + 6, be),
        bytes.readInt64(index + 8, be), bytes.readInt64(index + 16, be)
    )

    enum class STEType {
        LOCAL,
        GLOBAL,
        WEAK
    }
}