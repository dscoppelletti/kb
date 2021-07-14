Schema
======

```
> db.getCollectionInfos();
[
    {
        "name" : "articles",
        "type" : "collection",
        "options" : {
            "validator" : {
                "$jsonSchema" : {
                    "bsonType" : "object",
                    "required" : [
                        "title",
                        "authors",
                        "publishedDate",
                        "url",
                        "tags"
                    ],
                    "properties" : {
                        "title" : {
                            "bsonType" : "string"
                        },
                        "authors" : {
                            "bsonType" : "array",
                            "items" : {
                                "bsonType" : "string"
                            }
                        },
                        "publishedDate" : {
                            "bsonType" : "date"
                        },
                        "url" : {
                            "bsonType" : "string"
                        },
                        "file" : {
                            "bsonType" : "string"
                        },
                        "remark" : {
                            "bsonType" : "string"
                        },
                        "tags" : {
                            "bsonType" : "array",
                            "items" : {
                                "bsonType" : "string"
                            }
                       }
                    }
                }
            },
            "validationLevel" : "strict",
            "validationAction" : "error",
            "collation" : {
                "locale" : "en",
                "caseLevel" : false,
                "caseFirst" : "off",
                "strength" : 1,
                "numericOrdering" : false,
                "alternate" : "non-ignorable",
                "maxVariable" : "punct",
                "normalization" : false,
                "backwards" : false,
                "version" : "57.1"
            }
        },
        "info" : {
            "readOnly" : false,
            "uuid" : UUID("7a86fbee-2ee5-4127-9bcd-d58d98a6dd46")
        },
        "idIndex" : {
            "v" : 2,
            "key" : {
                "_id" : 1
            },
            "name" : "_id_",
            "collation" : {
                "locale" : "en",
                "caseLevel" : false,
                "caseFirst" : "off",
                "strength" : 1,
                "numericOrdering" : false,
                "alternate" : "non-ignorable",
                "maxVariable" : "punct",
                "normalization" : false,
                "backwards" : false,
                "version" : "57.1"
            }
        }
    }
]
```