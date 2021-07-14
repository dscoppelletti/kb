Access to MongoDB
=================

The configuration parameters for accessing MongoDB are loaded from a JSON file:

```
{
    "uri" : "mongodb+srv://localhost",
    "userName" : "dario",
    "password" : "********",
    "database" : "kb"
}
```

The JSON file may be named `mongo.json` and inserted in the folder
`.scoppelletti` in your HOME directory. That can be overriden by setting the
path and the name of the file to the environment variable `MONGO_CONFIG_FILE`.
