package org.github.bodemiller.karrangement.type

import org.github.bodemiller.karrangement.Config
import org.github.bodemiller.karrangement.impl.json.JsonConfig
import org.github.bodemiller.karrangement.impl.yaml.YamlConfig

/**
 * @author Bode Miller
 */
enum class ConfigurationType(val clazz: Class<out Config>) {

    JSON(JsonConfig::class.java),
    YAML(YamlConfig::class.java)

}