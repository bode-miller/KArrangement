import com.google.gson.GsonBuilder
import org.github.bodemiller.karrangement.ConfigurationBuilder
import org.github.bodemiller.karrangement.impl.yaml.YamlConfig
import org.github.bodemiller.karrangement.type.ConfigurationType
import org.jetbrains.annotations.TestOnly
import org.junit.jupiter.api.Test
import org.yaml.snakeyaml.DumperOptions
import java.io.File
import java.util.logging.Logger

class YamlConfigTest {

    @TestOnly
    @Test
    fun configTest() {
        val config = ConfigurationBuilder.of(ConfigurationType.YAML)
            .withLocation(File("C:\\Users\\s1025\\Desktop\\dev\\Personal\\KArrangement\\src\\test\\kotlin\\impls\\yaml-arrangement.yaml"))
            .withLogger(Logger.getAnonymousLogger())
            .withResourceClass(this::class.java)
            .build()

        config.load()
        if (config is YamlConfig) {
            println(config.dump())
            config.flowStyle(DumperOptions.FlowStyle.FLOW)
            println(config.dump())
        }
        //config.set("gaming.settings.gamer", true)
        //println("Gamers Game LOL?? : ${config.getBoolean("3")}")
        //config.save()
        /*config.set("gamer.lol.gamer", true)
        config.set("boom", false)
        config.set("gameing.lol.daf", 1)
        config.set("gamer.lol.boom", false)
        val arrangement: Boolean = config.getBoolean("arrangement")*/
        //config.save()
        //println(arrangement)
        //println(config.getBoolean("lol"))
    }

}