# KArrangement
Kotlin Arrangement/Configuration System, used to create easy configuration files, 
currently supporting Json and soon to be supporting Yaml.

### How to use
Firstly add the library to your maven or gradle for your project.\
\
To create your first configuration file for Kotlin use something like:

```
    val config by lazy {
        JsonConfig(
            File(
                configurationDirectory.toFile(), "config.json"
            ), logger, reloadable = false,
            resourceClazz = this::class.java,
            serializer = Serializers.gson
        )
    }
```