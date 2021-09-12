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

package it.scoppelletti.kb.app.i18n

import javax.inject.Inject
import java.util.ResourceBundle

class AppMessages @Inject constructor() {

    private val res =
        ResourceBundle.getBundle(AppMessages::class.java.canonicalName)

    fun appName(): String = res.getString("appName")

    fun commandAdd(): String = res.getString("cmd.add")

    fun commandArticle(): String = res.getString("cmd.article")

    fun commandClose(): String = res.getString("cmd.close")

    fun commandDelete(): String = res.getString("cmd.delete")

    fun commandDownload(): String = res.getString("cmd.download")

    fun commandFile(): String = res.getString("cmd.file")

    fun commandFind(): String = res.getString("cmd.find")

    fun commandNotSave(): String = res.getString("cmd.notSave")

    fun commandRead(): String = res.getString("cmd.read")

    fun commandSave(): String = res.getString("cmd.save")

    fun commandUpload(): String = res.getString("cmd.upload")

    fun errorAuthorsRequired(): String = res.getString("err.authorsRequired")

    fun errorPublishedDateRequired(): String =
        res.getString("err.publishedDateRequired")

    fun errorTagsRequired(): String = res.getString("err.tagsRequired")

    fun errorTitleRequired(): String = res.getString("err.titleRequired")

    fun errorUrlRequired(): String = res.getString("err.urlRequired")

    fun labelAuthors(): String = res.getString("lbl.authors")

    fun labelFile(): String = res.getString("lbl.file")

    fun labelOptionalTags(): String = res.getString("lbl.optionalTags")

    fun labelPageSize(): String = res.getString("lbl.pageSize")

    fun labelPublishedDate(): String = res.getString("lbl.publishedDate")

    fun labelPublishedDateMax(): String = res.getString("lbl.publishedDateMax")

    fun labelPublishedDateMin(): String = res.getString("lbl.publishedDateMin")

    fun labelRemark(): String = res.getString("lbl.remark")

    fun labelRequiredTags(): String = res.getString("lbl.requiredTags")

    fun labelTags(): String = res.getString("lbl.tags")

    fun labelTitle(): String = res.getString("lbl.title")

    fun labelUrl(): String = res.getString("lbl.url")

    fun promptDeleteArticle(): String = res.getString("prompt.deleteArticle")

    fun promptDeleteFile(): String = res.getString("prompt.deleteFile")

    fun promptSaveChanges(): String = res.getString("prompt.saveChanges")
}