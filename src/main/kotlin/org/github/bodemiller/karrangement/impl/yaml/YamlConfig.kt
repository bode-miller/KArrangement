package org.github.bodemiller.karrangement.impl.yaml

import com.google.gson.Gson
import org.github.bodemiller.karrangement.Config
import org.github.bodemiller.karrangement.impl.yaml.representer.YamlRepresenter
import org.github.bodemiller.karrangement.impl.yaml.section.YamlSection
import org.github.bodemiller.karrangement.util.Files
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.logging.Logger
import kotlin.math.log

/**
 * @author Bode Miller
 *
 * TODO:
 *  - Allow dumper options to be provided, and loader options.
 *  - Allow comments to be added to fields?
 */
class YamlConfig(
    file: File,
    logger: Logger,
    resourceClazz: Class<*>,
    reloadable: Boolean,
    private val serializer: Gson
) : Config(file, logger, resourceClazz, reloadable) {

    // We use snakeyaml to load and save all
    // our saved values, with our system.
    private val yaml = Yaml(YamlRepresenter(), DumperOptions());

    // Current Yaml Section, Basically is the master
    // section for us to handle with, everything, all values
    // are ran through this section.
    private val section: YamlSection = YamlSection();

    override fun getString(path: String): String? {
        val foundValue = findValue(path) ?: return null
        return foundValue as String
    }

    override fun getInt(path: String): Int? {
        val foundValue = findValue(path) ?: return null
        return foundValue as Int
    }

    override fun getBoolean(path: String): Boolean? {
        val foundValue = findValue(path) ?: return null
        return foundValue as Boolean
    }

    // This will show unchecked in IDE.
    override fun <T> getType(path: String): T? {
        val foundValue = findValue(path) ?: return null
        return foundValue as T
    }

    override fun set(path: String, value: Any) {
        val pathArray = path.split(".").toMutableList()
        val valueField = pathArray.removeAt(pathArray.size - 1)

        var valuesSection: YamlSection? = null

        // Loop the array of path members and find the
        // next YamlSection
        for (member in pathArray) {
            if (valuesSection == null) {
                valuesSection = findNextSection(section, member)
                continue
            }
            valuesSection = findNextSection(valuesSection, member)
        }

        if (valuesSection == null) {
            logger.warning("[Arrangement] Failed to set $path to its set value of $value because no section was found!")
            return
        }
        valuesSection.entries[valueField] = value
    }

    private fun findNextSection(section: YamlSection, path: String): YamlSection {
        val foundEntry = section.entries[path]

        if (foundEntry == null) {
            val newSection = YamlSection();
            section.entries[path] = newSection
            return newSection
        }

        // Throw an exception if something is not found correctly.
        if (foundEntry !is YamlSection) {
            throw UnsupportedOperationException(
                "Arrangement attempt to search for " +
                    "a section for $path but " +
                    "found because the path $path already has a value to it."
            )
        }
        return foundEntry
    }

    private fun findValue(path: String): Any? {
        val pathArray = path.split(".").toMutableList()
        val valueField = pathArray.removeAt(pathArray.size - 1)

        var currentSection: YamlSection? = null

        for (member in pathArray) {
            if (currentSection == null) {
                currentSection = findNextSection(section, member)
                continue
            }
            currentSection = findNextSection(currentSection, member)
        }

        if (currentSection != null) {
            return currentSection.entries[valueField]
        }
        return null
    }

    override fun save() {
        println()
        Files.write(yaml.dump(section), file)
    }

    override fun load() {
        kotlin.runCatching {
            // Populate our entries from the Yaml file.
            this.section.populateEntries(
                yaml.load<Map<String, Any>>(file.reader())
            )
        }.onFailure {
            logger.warning("[Arrangement] Failed to load Yaml Section entries. Backup and check file for corruption. (${file.name})")
        }
    }

    override fun reload() {
        // Get rid of our memory we're reloading!
        section.entries.clear()
        super.reload()
    }

}