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

package it.scoppelletti.kb.database.model

import it.scoppelletti.kb.domain.model.ArticleModel
import java.time.LocalDate
import org.bson.BsonType
import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.codecs.pojo.annotations.BsonRepresentation

data class ArticleData @BsonCreator constructor(

    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    val id: String?,

    @BsonProperty("title")
    val title: String,

    @BsonProperty("authors")
    val authors: List<String>,

    @BsonProperty("publishedDate")
    val publishedDate: LocalDate,

    @BsonProperty("url")
    val url: String,

    @BsonProperty("file")
    val file: String?,

    @BsonProperty("remark")
    val remark: String?,

    @BsonProperty("tags")
    val tags: List<String>
) {

    fun toModel() = ArticleModel(
        id = id,
        title = title,
        authors = authors,
        publishedDate = publishedDate,
        url = url,
        file = file,
        remark = remark,
        tags = tags
    )

    companion object {
        const val Collection = "articles"

        fun copy(source: ArticleModel) = ArticleData(
            id = source.id,
            title = source.title,
            authors = source.authors,
            publishedDate = source.publishedDate,
            url = source.url,
            file = source.file,
            remark = source.remark,
            tags = source.tags
        )
    }
}
