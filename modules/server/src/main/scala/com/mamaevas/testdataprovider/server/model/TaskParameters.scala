package com.mamaevas.testdataprovider.server.model

case class TaskParameters(
    uri: String,
    dbName: String,
    collectionName: String,
    bsonField: Option[String],
    outputUri: String,
    setParentUri: Option[String],
    accessToken: String
)
