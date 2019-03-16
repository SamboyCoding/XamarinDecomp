@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.samboycoding.xamarindecomp

import me.samboycoding.xamarindecomp.elf.ElfFile
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.ZipException
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

fun extractSoFile(file: File): File? {
    val workingDir = file.parentFile.resolve(file.nameWithoutExtension + "_out")

    if (!workingDir.exists()) workingDir.mkdir()

    try {
        println("-Decompiling ${file.canonicalPath}")
        ZipFile(file).use { zipFile ->
            val theSo = zipFile.entries().asSequence()
                .filter { e -> e.name.startsWith("lib") && e.name.endsWith("libmonodroid_bundle_app.so") }
                .firstOrNull()

            if (theSo == null) {
                println("\tFATAL: -APK does not appear to be compiled using Xamarin.Android (missing libmonodroid_bundle_app.so)")
                return null
            }

            println("\t-Located: ${theSo.name}")

            ZipInputStream(FileInputStream(file)).use { stream ->
                while (stream.nextEntry?.name != theSo.name);
                return try {
                    FileOutputStream(workingDir.resolve("bundle.so")).use { out ->
                        stream.copyTo(out)
                    }
                    println("\t-Extracted: bundle.so")

                    workingDir.resolve("bundle.so")
                } catch (iox: IOException) {
                    println("\tFATAL: -Couldn't create intermediary file: " + workingDir.resolve("bundle.so").canonicalPath)
                    null
                }
            }
        }
    } catch (ex: ZipException) {
        println("\tFATAL: -File is not a zip file. Ideally you provide the path to the apk itself.")
        return null
    } catch (ex: IOException) {
        println("\tFATAL: Error reading the file: ${ex.localizedMessage}")
        return null
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Must specify path to file.")
        return
    }

    val file = File(args[0])
    if (!file.exists()) {
        println("Specified file doesn't exist")
        return
    }

    if (!file.isFile) {
        println("Path does not resolve to a file.")
        return
    }

    val soFile = extractSoFile(file) ?: return

    println("\t-Commencing teardown: ${soFile.canonicalPath}")
    var bytes = ByteArray(0)
    FileInputStream(soFile).use { inputStream ->
        bytes = inputStream.readBytes()
    }

    val elf = ElfFile(bytes)

    if(!elf.header.is64Bit) {
        val dlls = elf.symbolTable32.asSequence().filter { entry -> entry.name.startsWith("assembly_data") }.toList()

        println("\t-Located ${dlls.size} DLLs within symbol table.")

        val dllsDir = file.parentFile.resolve(file.nameWithoutExtension + "_out").resolve("DLLs")
        if(!dllsDir.exists()) dllsDir.mkdir()

        val gzippedDir = dllsDir.resolve("gzipped")
        if(!gzippedDir.exists()) gzippedDir.mkdir()

        println("\t-Ripping DLLs:")
        for (dll in dlls) {
            val gzippedBytes = elf.bytes.sliceArray(IntRange(dll.startAddress, dll.startAddress + dll.size - 1))
            val gzOutFile = gzippedDir.resolve(dll.name.replace("assembly_data_", "").replace("_", ".") + ".gz")

            FileOutputStream(gzOutFile).use { outStream ->
                outStream.write(gzippedBytes)
                println("\t\t-Wrote dll.gz: ${gzOutFile.canonicalPath}")
            }

            val dllOutFile = dllsDir.resolve(dll.name.replace("assembly_data_", "").replace("_", "."))

            ByteArrayInputStream(gzippedBytes).use { bais ->
                GZIPInputStream(bais).use { gzipDecode ->
                    FileOutputStream(dllOutFile).use { outStream ->
                        gzipDecode.copyTo(outStream)
                        println("\t\t-Decompressed and wrote dll file: ${dllOutFile.canonicalPath}")
                    }
                }
            }
        }
    }
}
