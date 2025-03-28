package org.github.bodemiller.karrangement.impl.yaml

import com.google.gson.Gson
import org.github.bodemiller.karrangement.Config
import java.io.File
import java.util.logging.Logger

// TODO: Start this.
/**
 * @author Bode Miller
 */
class YamlConfig(
    file: File,
    logger: Logger,
    resourceClazz: Class<*>,
    reloadable: Boolean,
    private val serializer: Gson
) /*: Config(file, logger, resourceClazz, reloadable) {

    override fun getString(path: String): String = entries[path] as String
    override fun getInt(path: String): Int = entries[path] as Int
    override fun getBoolean(path: String): Boolean = entries[path] as Boolean
    override fun <T> getType(path: String): T? = entries[path] as T

    override fun set(path: String, value: Any) {
    }

    override fun save() {

    }

    override fun load() {

    }
}*/