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

@file:Suppress("UNUSED")

package it.scoppelletti.kb.database.inject

import com.mongodb.MongoClientSettings
import com.mongodb.WriteConcern
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import it.scoppelletti.kb.database.ArticleUseCaseImpl
import it.scoppelletti.kb.database.model.ArticleData
import it.scoppelletti.kb.domain.ArticleUseCase
import it.scoppelletti.mongo.config.MongoConfig
import it.scoppelletti.mongo.config.applyConfig
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider

@Module
abstract class KbDatabaseModule {

    @Binds
    abstract fun bindArticleUseCase(obj: ArticleUseCaseImpl): ArticleUseCase

    @Module
    companion object {

        @Provides
        @JvmStatic
        @Singleton
        fun provideMongoClient(): MongoDatabase {
            val config = MongoConfig.load()

            val codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(
                    PojoCodecProvider.builder()
                        .register(ArticleData::class.java)
                        .build()
                )
            )

            val settings = MongoClientSettings.builder()
                .applyConfig(config)
                .writeConcern(WriteConcern.W1
                    .withWTimeout(2500, TimeUnit.MILLISECONDS))
                .codecRegistry(codecRegistry)
                .build()

            val client = MongoClients.create(settings)
            return client.getDatabase(config.database)
        }
    }
}