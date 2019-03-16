package me.samboycoding.xamarindecomp.elf

import me.samboycoding.xamarindecomp.getSafe
import me.samboycoding.xamarindecomp.readAddress
import me.samboycoding.xamarindecomp.readInt32

data class ProgramHeaderTableEntry(
    val type: PHEntryType?,
    val flagsOrPos: Int,
    val offsetInFile: Long,
    val offsetInRam: Long,
    val sizeInFile: Long,
    val sizeInRam: Long,
    val posOrFlags: Int,
    val alignment: Long
) {
    constructor(bytes: ByteArray, index: Int, be: Boolean, x64: Boolean) : this(
        PHEntryType.values().getSafe(bytes.readInt32(index, be)), bytes.readInt32(index + 4, be),
        bytes.readAddress(index + 8, x64, !be), bytes.readAddress(index + if (x64) 16 else 12, x64, !be),
        bytes.readAddress(index + if (x64) 24 else 16, x64, !be), bytes.readAddress(index + if (x64) 32 else 20, x64, !be),
        bytes.readInt32(index + if (x64) 40 else 24, be), bytes.readAddress(index + if (x64) 44 else 28, x64, !be)
    )

    enum class PHEntryType {
        NULL_UNUSED,
        LOADABLE,
        DYNAMIC_LINKING_INFO,
        INTERPRETER_INFO,
        AUX_INFO,
        RESERVED,
        PROGRAM_HEADER_TABLE
    }
}