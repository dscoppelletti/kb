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

package it.scoppelletti.kb.database

import com.mongodb.MongoException
import javax.inject.Inject
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Field
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Sorts
import it.scoppelletti.kb.domain.ArticleUseCase
import it.scoppelletti.kb.domain.model.PageRequest
import it.scoppelletti.kb.domain.model.ArticleModel
import it.scoppelletti.kb.database.model.ArticleData
import it.scoppelletti.kb.domain.model.ArticleFindModel
import it.scoppelletti.kb.domain.model.Page
import mu.KotlinLogging
import org.bson.Document
import org.bson.conversions.Bson
import org.bson.types.ObjectId

class ArticleUseCaseImpl @Inject constructor(
    private val db: MongoDatabase
) : ArticleUseCase {

    override fun create(model: ArticleModel): ArticleModel {
        val articles =
            db.getCollection(ArticleData.Collection, ArticleData::class.java)

        val result = articles.insertOne(ArticleData.copy(model))
        val data = articles.find(Document("_id", result.insertedId)).first()
        return data?.toModel() ?: throw MongoException(
            "Failed to insert article.")
    }

    override fun read(articleId: String): ArticleModel? {
        val articles =
            db.getCollection(ArticleData.Collection, ArticleData::class.java)

        return articles.find(Document("_id", ObjectId(articleId))).map {
            it.toModel()
        }.first()
    }

    override fun update(model: ArticleModel): ArticleModel {
        val articles =
            db.getCollection(ArticleData.Collection, ArticleData::class.java)

        val options = FindOneAndReplaceOptions()
            .returnDocument(ReturnDocument.AFTER)
            .upsert(true)
        val data = articles.findOneAndReplace(Document("_id",
            ObjectId(model.id)), ArticleData.copy(model), options)
        return data?.toModel() ?: throw MongoException(
            "Failed to update article.")
    }

    override fun delete(articleId: String) {
        val articles =
            db.getCollection(ArticleData.Collection, ArticleData::class.java)

        if (articles.findOneAndDelete(Document("_id",
                ObjectId(articleId))) == null) {
            logger.warn { "Article id=$(articleId) not found." }
        }
    }

    override fun list(
        findModel: ArticleFindModel,
        pageReq: PageRequest
    ): Page<ArticleModel> {
        val articles =
            db.getCollection(ArticleData.Collection, ArticleData::class.java)

        val filters = mutableListOf<Bson>()
        if (findModel.authors.isNotEmpty()) {
            filters.add(Filters.`in`("authors", findModel.authors))
        }
        findModel.publishedDateMin?.let {
            filters.add(Filters.gte("publishedDate", it))
        }
        findModel.publishedDateMax?.let {
            filters.add(Filters.lte("publishedDate", it))
        }
        if (findModel.requiredTags.isNotEmpty()) {
            filters.add(Filters.all("tags", findModel.requiredTags))
        }

        val filter = if (filters.isEmpty()) Document() else Filters.and(filters)
        val itemCount = articles.countDocuments(filter)

        val pipeline = listOf(
            Aggregates.match(filter),
            Aggregates.addFields(
                Field("authorScore", Document("\$size",
                    Document("\$setIntersection",
                        listOf("\$authors", findModel.authors)))),
                Field("tagScore", Document("\$size",
                    Document("\$setIntersection",
                        listOf("\$tags", findModel.optionalTags))))
            ),
            Aggregates.sort(
                Sorts.orderBy(Sorts.descending("authorScore"),
                    Sorts.descending("tagScore"),
                    Sorts.descending("publishedDate"), Sorts.ascending("title"),
                    Sorts.ascending("id"))),
            Aggregates.skip(pageReq.skip),
            Aggregates.limit(pageReq.pageSize))

        val list = mutableListOf<ArticleModel>()
        articles.aggregate(pipeline, ArticleData::class.java)
            .map { it.toModel() }
            .into(list)

        return Page.from(pageReq, list, itemCount)
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}