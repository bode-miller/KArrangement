package org.github.bodemiller.karrangement.util

import java.io.*
import java.net.URLDecoder

/**
 * @author Bode Miller
 */
object Files {
    fun read(file: File, lambda: (BufferedReader) -> Unit) {
        lambda.invoke(BufferedReader(FileReader(file)))
    }

    fun write(str: String, file: File) {
        val writer = BufferedWriter(FileWriter(file))
        writer.write(str)
        writer.close()
    }

    fun copy(copy: File, copyTo: File) {
        read(copy) {
            write(it.readLines().joinToString("\n"), copyTo)
        }
    }

    fun getAllResources(path: String): List<File> {
        val classLoader = Thread.currentThread().contextClassLoader
        val resources = classLoader.getResources(path)
        val files = mutableListOf<File>()

        while (resources.hasMoreElements()) {
            val resource = resources.nextElement()
            val file = File(URLDecoder.decode(resource.file, "UTF-8"))

            if (file.isDirectory) {
                files.addAll(file.walk().toList())
            } else {
                files.add(file)
            }
        }
        return files
    }
}