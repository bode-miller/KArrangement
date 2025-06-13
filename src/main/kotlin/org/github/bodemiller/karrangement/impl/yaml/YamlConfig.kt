package org.github.bodemiller.karrangement.impl.yaml

import org.github.bodemiller.karrangement.Config
import org.github.bodemiller.karrangement.impl.yaml.representer.YamlRepresenter
import org.github.bodemiller.karrangement.impl.yaml.section.YamlSection
import org.github.bodemiller.karrangement.util.Files
import org.github.bodemiller.karrangement.util.ValueConverter
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.DumperOptions.FlowStyle
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.logging.Logger

/**
 * @author Bode Miller
 */
class YamlConfig(
    file: File,
    logger: Logger,
    resourceClazz: Class<*>,
    reloadable: Boolean
) : Config(file, logger, resourceClazz, reloadable) {

    private var dumperOptions = DumperOptions() // Our Yaml's dumper options.
    private var representer = YamlRepresenter(dumperOptions, FlowStyle.BLOCK) // our representer for YamlSection's

    private val storedValues = HashMap<String, Any>()

    // We use snakeyaml to load and save all
    // our saved values, with our system.
    private var yaml = Yaml(representer, dumperOptions)

    // Current Yaml Section, Basically is the master
    // section for us to handle with, everything, all values
    // are ran through this section.
    private val section: YamlSection = YamlSection();

    init {
        dumperOptions.isProcessComments = true
    }

    override fun getString(path: String): String? {
        val foundValue = findValue(path) ?: return null
        return foundValue as String
    }

    override fun getInt(path: String): Int? {
        val foundValue = findValue(path) ?: return null
        return ValueConverter.convertInt(foundValue)
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
        storedValues[path] = value
        valuesSection.entries[valueField] = value
    }

    /**
     * Finds the next section possible
     */
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
        if (storedValues.containsKey(path)) {
            return storedValues[path]
        }

        val pathArray = path.split(".").toMutableList()
        val valueField = pathArray.removeAt(pathArray.size - 1)

        var currentSection: YamlSection? = null

        // Loop the array of path members and find the
        // next YamlSection
        for (member in pathArray) {
            if (currentSection == null) {
                currentSection = findNextSection(section, member)
                continue
            }
            currentSection = findNextSection(currentSection, member)
        }

        if (currentSection != null) {
            val value = currentSection.entries[valueField]
            storedValues[path] = value!!
            return value
        }
        return null
    }

    override fun save() {
        Files.write(this.dump(), file)
    }

    override fun load() {
        kotlin.runCatching {
            // Populate our entries from the Yaml file.
            this.section.populateEntries(
                yaml.load<Map<String, Any>>(file.reader())
            )

            logger.info("[Arrangement] Loaded ${file.name} configuration")
        }.onFailure {
            logger.warning("[Arrangement] Failed to load Yaml Section entries. Backup and check file for corruption. (${file.name})")
        }
    }

    override fun reload() {
        // Get rid of our memory we're reloading!
        section.entries.clear()
        this.storedValues.clear()
        super.reload()
    }

    /**
     * Creates a String of all dumped information, this method is used to
     * save the configuration file.
     */
    fun dump(): String {
        return this.yaml.dump(section)
    }

    /**
     * Changes the flow style the representer uses, defaults to BLOCK.
     */
    fun flowStyle(style: FlowStyle): YamlConfig = this.apply {
        this.representer = YamlRepresenter(dumperOptions, style)
        this.yaml = Yaml(representer, dumperOptions)
    }

    /**
     * Allows edits to be done to the dumper allowing changes of indents sizes etc.
     */
    fun dumperOptions(dumperOptions: DumperOptions): YamlConfig = this.apply {
        this.dumperOptions =  dumperOptions
        this.dumperOptions.isProcessComments = true
        this.representer = YamlRepresenter(dumperOptions, representer.flowStyle)
        this.yaml = Yaml(representer, dumperOptions)
    }

}