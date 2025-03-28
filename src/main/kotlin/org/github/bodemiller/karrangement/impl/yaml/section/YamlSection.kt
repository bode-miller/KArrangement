package org.github.bodemiller.karrangement.impl.yaml.section

/**
 * @author Bode Miller
 */
class YamlSection {
    /**
     * Holds entries to more values, can even hold a YamlSection.
     * Essentially a Yaml Section is the group values after like lets say:
     *
     * settings:
     *  type-speed: 500
     *  random-responses: false
     *  extra:
     *      type-list: ["Are you sure?", "Are you positive?"]
     *
     * as shown, settings is a section, everything indented under it will be a new value in
     * the entries map, while extra is an entry it's a new YamlSection, as its extra entries.
     */
    val entries = LinkedHashMap<String, Any?>()

    fun populateEntries(map: Map<*, *>) {
        for ((key, value) in map) {
            val keyAsString = key.toString()

            // Checking a for a Map<*, *> As we're going to
            // consider it as a YamlSection, and run a recursive
            // method which basically runs this method again,
            // in the new section, and will run again, if there
            // is more YamlSections, making it run until all values
            // have been found, now this is a little heavy but shouldn't
            // be that bad, as this should be populated on an Applications
            // first start up, or on a reload.
            if (value is Map<*, *>) {
                val newSection = YamlSection()
                newSection.populateEntries(value)
                entries[keyAsString] = newSection
            } else {
                entries[keyAsString] = value
            }
        }
    }

    /**
     * Override and use the entry Map#toString
     */
    override fun toString(): String {
        return entries.toString()
    }

}