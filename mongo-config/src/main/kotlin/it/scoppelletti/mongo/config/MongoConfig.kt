/*
 * Copyright (C) 2021 Dario Scoppelletti, <http://www.scoppelletti.it/>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("RemoveRedundantQualifierName")

package it.scoppelletti.mongo.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import java.io.File
import java.io.IOException
import java.io.InputStream
import mu.KotlinLogging
import okio.BufferedSource
import okio.Source
import okio.buffer
import okio.source
import org.apache.commons.lang3.SystemUtils

@JsonClass(generateAdapter = true)
data class MongoConfig(
    val uri: String,
    val userName: String,

    @JsonPassword
    val password: CharArray,

    val database: String
) {

    internal fun toConnectionString() = ConnectionString(uri)

    internal fun toMongoCredential(): MongoCredential =
        MongoCredential.createCredential(userName, MongoConfig.DB_ADMIN,
            password)

    override fun equals(other: Any?): Boolean {
        val op = other as? MongoConfig ?: return false
        return MongoConfigDelegate.copy(this, false) ==
                MongoConfigDelegate.copy(op, false)
    }

    override fun hashCode(): Int {
        return MongoConfigDelegate.copy(this, false).hashCode()
    }

    override fun toString(): String {
        return MongoConfigDelegate.copy(this, true).toString()
    }

    companion object {

        @Suppress("WeakerAccess")
        const val DB_ADMIN = "admin"

        @Suppress("WeakerAccess")
        const val ENV_CONFIG = "MONGO_CONFIG_FILE"

        @Suppress("WeakerAccess")
        const val FILE_CONFIG = ".scoppelletti/mongo.json"

        private val logger = KotlinLogging.logger { }

        fun load(): MongoConfig {
            val data: MongoConfig
            var stream: InputStream? = null
            var source: Source? = null
            var reader: BufferedSource? = null

            val moshi = Moshi.Builder()
                .add(CharArray::class.java, JsonPassword::class.java,
                    PasswordAdapter())
                .build()
            val adapter = moshi.adapter(MongoConfig::class.java)

            val env = SystemUtils.getEnvironmentVariable(
                MongoConfig.ENV_CONFIG, "")
            val file = if (env.isBlank()) {
                File(SystemUtils.getUserHome(), MongoConfig.FILE_CONFIG)
            } else File(env)

            try {
                stream = file.inputStream()

                source = stream.source()
                stream = null

                reader = source.buffer()
                source = null

                data = adapter.fromJson(reader)
                    ?: throw IOException("Failed to read file ${file}.")
            } catch (ex: Exception) {
                throw IOException("Failed to read file ${file}.", ex)
            } finally {
                stream?.close()
                source?.close()
                reader?.close()
            }

            logger.info { "Load $data from $file." }

            return data
        }
    }
}

fun MongoClientSettings.Builder.applyConfig(
    config: MongoConfig
): MongoClientSettings.Builder = this
    .applyConnectionString(config.toConnectionString())
    .credential(config.toMongoCredential())

private data class MongoConfigDelegate(
    val uri: String,
    val userName: String,
    val password: String,
    val database: String
) {
    companion object {

        fun copy(source: MongoConfig, secured: Boolean) = MongoConfigDelegate(
            source.uri, source.userName,
            if (secured) "********" else source.password.toString(),
            source.database)
    }
}