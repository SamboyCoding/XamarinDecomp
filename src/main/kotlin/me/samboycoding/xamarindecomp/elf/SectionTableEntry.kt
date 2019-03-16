package me.samboycoding.xamarindecomp.elf

import me.samboycoding.xamarindecomp.getSafe
import me.samboycoding.xamarindecomp.readAddress
import me.samboycoding.xamarindecomp.readInt32

data class SectionTableEntry(
    var name: String,
    val nameOffset: Int,
    val type: SectionTableType?,
    val flags: Long,
    val indexInRam: Long,
    val indexInFile: Long,
    val sizeInFile: Long,
    val linkedSectionIndex: Int,
    val info: Int,
    val alignment: Long,
    val entrySize: Long
) {
    constructor(bytes: ByteArray, index: Int, be: Boolean, x64: Boolean) : this(
        "",
        bytes.readInt32(index, be), SectionTableType.values().getSafe(bytes.readInt32(index + 4, be)),
        bytes.readAddress(index + 8, x64, !be), bytes.readAddress(index + if (x64) 16 else 12, x64, !be),
        bytes.readAddress(index + if (x64) 24 else 16, x64, !be), bytes.readAddress(index + if (x64) 32 else 20, x64, !be),
        bytes.readInt32(index + if (x64) 40 else 24, be), bytes.readInt32(index + if (x64) 44 else 28, be),
        bytes.readAddress(index+ if (x64) 48 else 32, x64, !be), bytes.readAddress(index + if (x64) 56 else 36, x64, !be)
    )

    enum class SectionTableType {
        NULL_EMPTY_TABLE_TYPE,
        PROGRAM_DATA,
        SYMBOL_TABLE,
        STRING_TABLE,
        RELOCATION_WITH_ADDENDS,
        HASH_TABLE,
        DYNAMIC_LINKING_INFO,
        NOTES,
        NO_BITS,
        RELOCATION_NO_ADDENDS,
        RESERVED,
        DYNAMIC_LINKING_SYMBOL_TABLE,
        CONSTRUCTORS,
        DESTRUCTORS,
        PRE_CONSTRUCTORS,
        SECTION_GROUP,
        EXTENDED_SECTION_INDICES,
        DEFINED_TYPES_NUMBER
    }
}
