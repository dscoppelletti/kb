db.createCollection("articles", {
    validator : {
        $jsonSchema: {
            bsonType: 'object',
            required: [
                'title',
                'authors',
                'publishedDate',
                'url',
                'tags'
            ],
            properties: {
                url: {
                    bsonType: 'string'
                },
                file: {
                    bsonType: 'string'
                },
                remark: {
                    bsonType: 'string'
                },
                tags: {
                    bsonType: 'array',
                    items: {
                        bsonType: 'string'
                }
                },
                title: {
                    bsonType: 'string'
                },
                authors: {
                    bsonType: 'array',
                    items: {
                        bsonType: 'string'
                    }
                },
                publishedDate: {
                    bsonType: 'date'
                }
            }
        }
    },
    validationLevel: "strict",
    validationAction: "error"
});

db.createView("authors", "articles",
    [{
        $project: {
            _id: 0,
            authors: 1
        }}, {$unwind: {
            path: '$authors'
        }}, {$group: {
            _id: '$authors'
        }}, {$sort: {
            _id: 1
        }
    }]
);

db.createView("tag_cloud", "articles",
   [{
        $unwind: {
            path: '$tags'
        }
    },
    {
        $group: {
            _id: '$tags',
            count: {
                $sum: 1
            }
        }},
    {
        $sort: {
            count: -1,
            _id: 1
        }
    }]
);
