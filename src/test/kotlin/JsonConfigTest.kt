import com.google.gson.GsonBuilder
import org.github.bodemiller.karrangement.impl.json.JsonConfig
import org.jetbrains.annotations.TestOnly
import org.junit.jupiter.api.Test
import java.io.File
import java.util.logging.Logger

class JsonConfigTest {

    @TestOnly
    @Test
    fun configTest() {
        val config = JsonConfig(
            File(""),
            Logger.getLogger("Arrangement"),
            JsonConfigTest::class.java,
            reloadable = false,
            GsonBuilder().setPrettyPrinting().create()
        )

        config.saveDefault()
        config.set("arrangement.reload", true)
        config.save()
        println("Reloadable: ${config.getBoolean("arrangement.reload")}")
    }

}