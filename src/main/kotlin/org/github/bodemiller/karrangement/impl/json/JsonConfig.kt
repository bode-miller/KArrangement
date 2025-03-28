package org.github.bodemiller.karrangement.impl.json

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.github.bodemiller.karrangement.Config
import org.github.bodemiller.karrangement.util.Files
import java.io.File
import java.util.logging.Logger

/**
 * @author Bode Miller
 *
 * TODO:
 *  - Add options to not store already searched elements in a map.
 *      Since some people won't wanna use more memory to keep elements
 *      stored in more memory and rather use the overhead on the
 *      JsonConfig#findValue method, then let it be.
 */
class JsonConfig(
    file: File,
    logger: Logger,
    resourceClazz: Class<*>,
    reloadable: Boolean,
    private val serializer: Gson,
) : Config(file, logger, resourceClazz, reloadable) {

    private lateinit var config: JsonObject
    private val elementAt = hashMapOf<String, JsonElement>()

    override fun getString(path: String): String? {
        val element = findElement(path) ?: return null
        return element.asString
    }

    override fun getInt(path: String): Int? {
        val element = findElement(path) ?: return null
        return element.asInt
    }

    override fun getBoolean(path: String): Boolean? {
        val element = findElement(path) ?: return null
        return element.asBoolean
    }

    override fun <T> getType(path: String, clazz: Class<T>): T? {
        val element = findElement(path) ?: return null
        return serializer.fromJson(element.asJsonObject, clazz)
    }

    override fun set(path: String, value: Any) {
        val cachedElement = elementAt[path]

        // Don't allow us to set if we already have
        // a cached value of same value.
        if (cachedElement != null && cachedElement == value) {
            return
        }

        val pathArray = path.split(".").toMutableList()
        val valueField = pathArray.removeAt(pathArray.size - 1) // Remove value field.

        var currentMember: JsonElement? = null

        // This looks kinda weird... right? anyways, runs through the path array to find the next object/member
        // until we reach the final member. so we can add the new property.
        for (member in pathArray) {
            if (currentMember == null) {
                var currentMemberElement = config.get(member)

                if (currentMemberElement == null) {
                    config.add(member, JsonObject())
                    currentMemberElement = config.get(member)
                }
                currentMember = currentMemberElement
                continue
            }

            // Lets say someone uses this:
            // application -> true
            // but then also asks for this:
            // application.debug -> true
            // This will cause a big problem as application won't be a Json Object
            // meaning application.debug is unreachable, if you wanna use this
            // do:
            // application.enabled -> true
            // application.debug -> true
            // This is the correct format.
            if (!currentMember.isJsonObject) {
                logger.info("[Arrangement] You cannot use a route that has a variable already!")
                return
            }

            var currentMemberElement = currentMember.asJsonObject.get(member)

            if (currentMemberElement == null) {
                currentMember.asJsonObject.add(member, JsonObject())
                currentMemberElement = currentMember.asJsonObject.get(member)
            }
            currentMember = currentMemberElement
        }

        // This would be weird for it to happen, but is possible.
        if (currentMember == null) {
            logger.info("[Arrangement] Failed to get to final member, couldn't set value. (Path: $path | Value: $value)")
            return
        }
        val valueElement = serializer.toJsonTree(value)
        currentMember.asJsonObject.add(valueField, valueElement)
        elementAt[path] = valueElement
    }

    // This method is the way we will find fields for the configuration in Json
    // You will input a path such as gamer.really.a.gamer which will read in json as:

    /**
     * {
     * "gamer": {
     *      "really": {
     *          "a": {
     *              "gamer": true
     *          }
     *      }
     *  }
     * }
     */
    private fun findElement(path: String): JsonElement? {
        // Use a cached element instead of running a
        // loop to find the exact member...
        val cachedElement = elementAt[path]

        if (cachedElement != null) {
            return cachedElement
        }

        // We use the typed array here since we
        // don't need to splice or remove any members in this array.
        val pathArray = path.split(".").toTypedArray()
        var currentMember: JsonElement? = null

        for (member in pathArray) {
            // We will have a null member at first!!!
            if (currentMember == null) {
                currentMember = config.get(member)
                continue
            }
            currentMember = currentMember.asJsonObject.get(member)
        }

        // Only cache elements that aren't null silly
        if (currentMember != null) {
            elementAt[path] = currentMember
        }
        return currentMember
    }

    override fun save() {
        Files.write(serializer.toJson(config), file)
    }

    override fun load() {
        Files.read(file) {
            // Throw in a try catch so it doesn't error the command
            // line with alot of big errors. it's not parsing the config is the
            // least of my problems lol. the config will just go to default settings
            // which isn't that bad, could be in some situations but if its bad enough that the config
            // needed to be the exact settings it was before, then stop the server and
            // restart with the correct format. really this can go with anything, IP addresses can be wrong
            // just restart the application.
            try {
                val element = JsonParser.parseReader(it)

                if (element == null) {
                    logger.info("[Arrangement] Failed to parse ${file.name} Json as it appears it isn't in json format.")
                    config = JsonObject()
                    return@read
                }

                if (!element.isJsonObject) {
                    logger.info("[Arrangement] Failed to get the correct format for configuration, the file must be in a Json Object.")
                    config = JsonObject()
                    return@read
                }

                // YAY!!! we got a Json object
                config = element.asJsonObject
                logger.info("[Arrangement] Loaded ${file.name} configuration")
            } catch (ex: Exception) {
                logger.info("[Arrangement] Failed to parse ${file.name} Json as it appears it isn't in json format.")
                config = JsonObject()
            }
        }
    }

    override fun reload() {
        super.reload()
        elementAt.clear()
    }

}