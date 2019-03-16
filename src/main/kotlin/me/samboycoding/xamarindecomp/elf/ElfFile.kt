package me.samboycoding.xamarindecomp.elf

import me.samboycoding.xamarindecomp.readString

class ElfFile
    (bytes: ByteArray) {

    val header: ElfFileHeader

    val sectionHeaderStringTableHeader: SectionTableEntry
    val primaryStringTableHeader: SectionTableEntry
    val symbolTableHeader: SectionTableEntry

    val symbolTable32 = ArrayList<SymbolTableEntry32>()
    val symbolTable64 = ArrayList<SymbolTableEntry64>()

    val ELF_HEADER_MAGIC = arrayOf<Byte>(0x7F, 0x45, 0x4c, 0x46)

    val bytes: ByteArray

    init {
        if (!bytes.sliceArray(IntRange(0, 3)).contentEquals(ELF_HEADER_MAGIC.toByteArray())) {
            throw IllegalArgumentException("Elf File magic number does not match!")
        }

        this.bytes = bytes;

        header = ElfFileHeader(bytes)
        sectionHeaderStringTableHeader = header.sectionHeaderTable[header.shStrTabLoc.toInt()]
        primaryStringTableHeader =
            header.sectionHeaderTable.asSequence().find { s -> bytes.readString((sectionHeaderStringTableHeader.indexInFile + s.nameOffset).toInt()) == ".strtab" }
                ?: throw IllegalStateException("Unable to find primary string table!")

        println("\t\t-Mapping section names...")

        for (sectionTableEntry in header.sectionHeaderTable) {
            sectionTableEntry.name = bytes.readString((sectionHeaderStringTableHeader.indexInFile + sectionTableEntry.nameOffset).toInt())
            println("\t\t\t-$sectionTableEntry")
        }

        symbolTableHeader = header.sectionHeaderTable.asSequence().filter { entry -> entry.type != null && entry.type == SectionTableEntry.SectionTableType.SYMBOL_TABLE }.firstOrNull()
            ?: throw IllegalStateException("Unable to locate symbol table!")

        //And find the name for it
        println("\t\t-Symbol table located, name is ${bytes.readString((sectionHeaderStringTableHeader.indexInFile + symbolTableHeader.nameOffset).toInt())} and it is at offset 0x${symbolTableHeader.indexInFile.toString(16)}")

        println("\t\t-Symbol Table:")
        if(!header.is64Bit) {
            var currentPos = symbolTableHeader.indexInFile.toInt()

            for (i in 1..(symbolTableHeader.sizeInFile / symbolTableHeader.entrySize)) {
                val entry = SymbolTableEntry32(bytes, currentPos, !header.littleEndian)
                entry.name = bytes.readString((primaryStringTableHeader.indexInFile + entry.nameAddress).toInt())
                symbolTable32.add(entry)
                currentPos += symbolTableHeader.entrySize.toInt()
            }

            symbolTable32.sortBy { entry -> entry.startAddress }
            for (symbolTableEntry32 in symbolTable32) {
                println("\t\t\t-$symbolTableEntry32")
            }
        } else {
            var currentPos = symbolTableHeader.indexInFile.toInt()

            for (i in 1..(symbolTableHeader.sizeInFile / symbolTableHeader.entrySize)) {
                val entry = SymbolTableEntry64(bytes, currentPos, !header.littleEndian)
                entry.name = bytes.readString((primaryStringTableHeader.indexInFile + entry.nameAddress).toInt())
                symbolTable64.add(entry)
                currentPos += symbolTableHeader.entrySize.toInt()
            }

            symbolTable64.sortBy { entry -> entry.value }
            for (symbolTableEntry64 in symbolTable64) {
                println("\t\t\t-$symbolTableEntry64")
            }
        }
    }
}