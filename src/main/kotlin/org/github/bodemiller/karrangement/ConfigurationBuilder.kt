package org.github.bodemiller.karrangement

import org.github.bodemiller.karrangement.type.ConfigurationType
import java.io.File
import java.util.logging.Logger

/**
 * @author Bode Miller
 *
 * TODO:
 *  - Create a better way of automatically building configuration types...
 *  - Use a mappable values to automatically map values and change them from the builder side. (Not sure to pursue)
 */
class ConfigurationBuilder(val type: ConfigurationType) {

    private var fileLocation: File? = null
    private var logger: Logger? = Logger.getAnonymousLogger() // use anonymous logger by default if one is not provided.
    private var resourceClazz: Class<*>? = null
    private var reloadable: Boolean = false // files are not reloadable by default

    companion object {
        fun of(type: ConfigurationType): ConfigurationBuilder {
            return ConfigurationBuilder(type)
        }
    }

    fun withLocation(file: File): ConfigurationBuilder = this.also { this.fileLocation = file }
    fun withLogger(logger: Logger): ConfigurationBuilder = this.also { this.logger = logger }
    fun withResourceClass(resourceClazz: Class<*>): ConfigurationBuilder = this.also { this.resourceClazz = resourceClazz }
    fun isReloadable(reloadable: Boolean): ConfigurationBuilder = this.also { this.reloadable = reloadable }

    /**
     * Builds a configuration from given information, all configuration files will follow
     * pattern of, File, Logger, Class, and Boolean. No matter what, any extra options need to be declared by
     * checking if the type is that File configuration then casting to edit the options.
     */
    fun build(): Config {
        assert(fileLocation != null) { "Configuration file must have a file location" }
        assert(resourceClazz != null) { "There must be an assigning class to gather resources from" }

        val constructor = type.clazz.getConstructor(
            File::class.java,
            Logger::class.java,
            Class::class.java,
            Boolean::class.java
        )

        return constructor.newInstance(fileLocation, logger, resourceClazz, reloadable)
    }

}