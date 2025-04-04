package org.github.bodemiller.karrangement

import org.github.bodemiller.karrangement.util.Files
import java.io.File
import java.nio.charset.Charset
import java.util.logging.Logger

/**
 * @author Bode Miller
 */
abstract class Config(
    val file: File,
    val logger: Logger,
    private val resourceClazz: Class<*>,
    private val reloadable: Boolean
) {

    init {
        // Auto check if we can create all directory's
        if (file.parentFile != null && file.parentFile.mkdirs()) {
            logger.info("[Arrangement] Created directory for ${file.name} (${file.absolutePath})")
        }
    }

    abstract fun getString(path: String): String?

    abstract fun getInt(path: String): Int?

    abstract fun getBoolean(path: String): Boolean?

    open fun <T> getType(path: String, clazz: Class<T>): T? { return null }

    open fun <T> getType(path: String): T? { return null }

    fun getStringOrDefault(path: String, default: String): String {
        val string = getString(path)

        if (string == null) {
            set(path, default)
            return default
        }
        return string
    }

    fun getBooleanOrDefault(path: String, default: Boolean): Boolean {
        val bool = getBoolean(path)

        if (bool == null) {
            set(path, default)
            return default
        }
        return bool
    }

    fun getIntOrDefault(path: String, default: Int): Int {
        val int = getInt(path)

        if (int == null) {
            set(path, default)
            return default
        }
        return int
    }

    fun <T> getTypeOrDefault(path: String, clazz: Class<T>, default: T): T {
        val type = getType(path, clazz)

        if (type == null) {
            set(path, default as Any)
            return default
        }
        return type
    }

    // Save default configuration file!
    open fun saveDefault(): Boolean {
        // Do we really want to reset the configuration every restart? No.
        // This is how we combat that!
        if (file.exists()) {
            return false
        }

        kotlin.runCatching {
            val url = resourceClazz.classLoader.getResource(file.name)

            if (url == null) {
                logger.warning("[Arrangement] Failed to find configuration file: ${file.name}")
                return false;
            }

            val connection = url.openConnection()
            connection.useCaches = false
            val lines = connection.getInputStream().reader(Charset.defaultCharset()).readLines()

            if (!file.createNewFile()) {
                logger.warning("[Arrangement] Failed to create file for: ${file.name}")
                return false
            }

            Files.write(lines.formString(), file)
            load() // Configuration shouldn't of been loaded yet.
            save()
            logger.info("[Arrangement] Created file from default for: ${file.name}")
            return true
        }.onFailure {
            logger.warning("[Arrangement] Failed to find configuration file: ${file.name}")
        }
        logger.warning("[Arrangement] Failed to find configuration file: ${file.name}")
        return false
    }

    abstract fun set(path: String, value: Any)

    abstract fun save()

    abstract fun load()

    open fun reload() {
        if (!reloadable) {
            logger.info("[Arrangement] Attempted to reload a configuration that isn't reloadable. (File: ${file.name})")
            return
        }
        load()
    }

    // We use this as using joinToString, adds comma's that we don't
    // need, so we will form a string and then add a new line break so it
    // basically sets it to "pretty" printing, although its really just copying
    // what was in the default file.
    fun List<String>.formString(): String {
        val builder = StringBuilder()

        iterator().forEach {
            builder.append(it + "\n")
        }
        return builder.toString()
    }

}