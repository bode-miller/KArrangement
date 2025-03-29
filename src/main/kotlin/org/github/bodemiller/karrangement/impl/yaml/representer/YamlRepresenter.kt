package org.github.bodemiller.karrangement.impl.yaml.representer

import org.github.bodemiller.karrangement.impl.yaml.section.YamlSection
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.DumperOptions.FlowStyle
import org.yaml.snakeyaml.nodes.Node
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Represent
import org.yaml.snakeyaml.representer.Representer

/**
 * @author Bode Miller
 */
class YamlRepresenter(dumperOptions: DumperOptions, val flowStyle: FlowStyle) : Representer(dumperOptions) {

    init {
        // Register out representer
        this.multiRepresenters[YamlSection::class.java]  = YamlSectionRepresenter()
    }

    /**
     * Our Map Representer for Yaml Sections, allowing the Yaml Sections
     * to be dumped/serialized correctly.
     *
     * this is actually a simple copy of the map representer, but allowing us
     * to get the YamlSection#entries field, and use it instead of trying to
     * dump/serialize a YamlSection object.
     */
    private inner class YamlSectionRepresenter : Represent {
        override fun representData(data: Any?): Node? {
            if (data !is YamlSection) {
                return null
            }
            return representMapping(
                getTag(data::class.java, Tag.MAP),
                data.entries,
                flowStyle
            )
        }

    }

}