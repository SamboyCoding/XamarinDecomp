package me.samboycoding.xamarindecomp.elf

import me.samboycoding.xamarindecomp.getSafe
import me.samboycoding.xamarindecomp.readInt16
import me.samboycoding.xamarindecomp.readInt32
import kotlin.experimental.and

data class SymbolTableEntry32(
    val nameAddress: Int,
    val startAddress: Int,
    val size: Int,
    val info: Byte,
    val visibility: Byte,
    val sectionHeaderIndex: Short,
    val type: STEType? = STEType.values().getSafe((info and 0xF).toInt()),
    var name: String = ""
) {
    constructor(bytes: ByteArray, index: Int, be: Boolean) : this(
        bytes.readInt32(index, be), bytes.readInt32(index + 4, be), bytes.readInt32(index + 8, be),
        bytes[index + 12], bytes[index + 13], bytes.readInt16(index + 14, be)
    )

    enum class STEType {
        LOCAL,
        GLOBAL,
        WEAK
    }
}