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

package it.scoppelletti.kb.app

import it.scoppelletti.kb.app.i18n.AppMessages
import it.scoppelletti.kb.app.model.ArticleForm
import it.scoppelletti.kb.domain.ArticleUseCase
import it.scoppelletti.kb.domain.FileUseCase
import java.io.File
import javax.inject.Inject
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FilenameUtils

class ArticleViewModel(
    private val articleUseCase: ArticleUseCase,
    private val fileUseCase: FileUseCase,
    private val appMessages: AppMessages
) {

    var form = ArticleForm()

    fun saveArticle() {
        val saving = form.toModel(appMessages)
        val saved = if (saving.id.isNullOrBlank())
            articleUseCase.create(saving) else articleUseCase.update(saving)
        form.copy(saved)
    }

    fun readArticle(articleId: String) {
        form.copy(requireNotNull(articleUseCase.read(articleId)) {
            "Article $articleId not found."
        })
    }

    fun deleteArticle() {
        val file = form.file
        if (!file.isNullOrBlank()) {
            fileUseCase.delete(file)
        }

        form.articleId?.let {
            articleUseCase.delete(it)
        }
    }

    fun uploadFile(file: File) {
        val model = form.toModel(appMessages)
        if (model.id.isNullOrBlank()) {
            return
        }

        val oldName = model.file
        if (!oldName.isNullOrBlank()) {
            fileUseCase.delete(oldName);
        }

        val data = buildString {
            append(model.title)
            model.authors.forEach { author ->
                append(author)
            }
            append(model.publishedDate.toString())
        }

        val ext = FilenameUtils.getExtension(file.path)

        val name = buildString {
            append(DigestUtils.sha1Hex(data))
            if (!ext.isNullOrBlank()) {
                append(FilenameUtils.EXTENSION_SEPARATOR)
                append(ext)
            }
        }

        fileUseCase.upload(name, file)

        val saving = model.copy(file = name)
        val saved = articleUseCase.update(saving)
        form.copy(saved)
    }

    fun downloadFile(file: File) {
        val name = form.file
        if (!name.isNullOrBlank()) {
            fileUseCase.download(name, file)
        }
    }

    fun deleteFile() {
        val model = form.toModel(appMessages)
        val name = model.file
        if (model.id.isNullOrBlank() || name.isNullOrBlank()) {
            return
        }

        fileUseCase.delete(name)
        val saving = model.copy(file = null)
        val saved = articleUseCase.update(saving)
        form.copy(saved)
    }
}

class ArticleViewModelFactory @Inject constructor(
    private val articleUseCase: ArticleUseCase,
    private val fileUseCase: FileUseCase,
    private val appMessages: AppMessages
) {

    fun create() = ArticleViewModel(articleUseCase, fileUseCase, appMessages)
}