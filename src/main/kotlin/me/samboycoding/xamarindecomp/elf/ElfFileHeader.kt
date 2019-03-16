package me.samboycoding.xamarindecomp.elf

import me.samboycoding.xamarindecomp.readAddress
import me.samboycoding.xamarindecomp.readInt16

class ElfFileHeader {
    var littleEndian: Boolean
    var is64Bit: Boolean

    var entryPointAddress: Long
    var programHeaderTableStart: Long
    var sectionHeaderTableStart: Long

    var elfHeaderSize: Short

    var programHeaderTableEntryLength: Short
    var programHeaderTableNumEntries: Short

    var sectionHeaderTableEntryLength: Short
    var sectionHeaderTableNumEntries: Short

    var shStrTabLoc: Short

    val programHeaderTable = ArrayList<ProgramHeaderTableEntry>()
    val sectionHeaderTable = ArrayList<SectionTableEntry>()

    constructor(bytes: ByteArray) {
        println("\t\t===SO File Header===")

        is64Bit = bytes[4].toInt() == 2
        println("\t\tArch: ${if (is64Bit) "64" else "32"}-bit")

        littleEndian = bytes[5].toInt() == 1
        println("\t\tEndianness: ${if (littleEndian) "little" else "big"}")

        entryPointAddress = bytes.readAddress(0x18, is64Bit, littleEndian)
        programHeaderTableStart = bytes.readAddress(if(is64Bit) 0x20 else 0x1C, is64Bit, littleEndian)
        sectionHeaderTableStart = bytes.readAddress(if(is64Bit) 0x28 else 0x20, is64Bit, littleEndian)

        println("\t\tEntry Point Address: 0x${entryPointAddress.toString(16)}")
        println("\t\tHeader Table Start Address: 0x${programHeaderTableStart.toString(16)}")
        println("\t\tSection Header Start Address: 0x${sectionHeaderTableStart.toString(16)}")

        elfHeaderSize = bytes.readInt16(if(is64Bit) 0x32 else 0x28, !littleEndian)
        println("\t\tELF file header length: $elfHeaderSize bytes")

        programHeaderTableEntryLength = bytes.readInt16(if(is64Bit) 0x36 else 0x2A, !littleEndian)
        println("\t\tProgram Header Table Entry Length: $programHeaderTableEntryLength bytes")

        programHeaderTableNumEntries = bytes.readInt16(if(is64Bit) 0x38 else 0x2C, !littleEndian)
        println("\t\tProgram Header Table contains $programHeaderTableNumEntries entries")

        sectionHeaderTableEntryLength = bytes.readInt16(if(is64Bit) 0x3A else 0x2E, !littleEndian)
        println("\t\tSection Header Table Entry Length: $sectionHeaderTableEntryLength bytes")

        sectionHeaderTableNumEntries = bytes.readInt16(if(is64Bit) 0x3C else 0x30, !littleEndian)
        println("\t\tSection Header Table contains $sectionHeaderTableNumEntries entries")

        shStrTabLoc = bytes.readInt16(if(is64Bit) 0x3E else 0x32, !littleEndian)
        println("\t\t.shstrtab is section $shStrTabLoc")

        println("\t\tHeader Table:")
        var currentPos = programHeaderTableStart.toInt()

        for (i in 1..programHeaderTableNumEntries) {
            val entry = ProgramHeaderTableEntry(bytes, currentPos, !littleEndian, is64Bit)
            println("\t\t\t-$entry")
            programHeaderTable.add(entry)
            currentPos += programHeaderTableEntryLength
        }

        println("\t\tSection Table:")
        currentPos = sectionHeaderTableStart.toInt()

        for (i in 1..sectionHeaderTableNumEntries) {
            val entry = SectionTableEntry(bytes, currentPos, !littleEndian, is64Bit)
            println("\t\t\t-$entry")
            sectionHeaderTable.add(entry)
            currentPos += sectionHeaderTableEntryLength
        }
    }

}