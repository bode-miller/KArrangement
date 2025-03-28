import com.google.gson.GsonBuilder
import org.github.bodemiller.karrangement.impl.yaml.YamlConfig
import org.jetbrains.annotations.TestOnly
import org.junit.jupiter.api.Test
import java.io.File
import java.util.logging.Logger

class YamlConfigTest {

    @TestOnly
    @Test
    fun configTest() {
        val config = YamlConfig(
            File("path"),
            Logger.getLogger("Arrangement"),
            JsonConfigTest::class.java,
            reloadable = false,
            GsonBuilder().setPrettyPrinting().create()
        )
        //config.load()
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