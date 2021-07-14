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

package it.scoppelletti.kb.domain.model

data class Page<T>(
    val pageIndex: Int,
    val pageSize: Int,
    val pageCount: Int,
    val content: List<T>
) {

    companion object {

        fun <T> empty(pageSize: Int) = Page(
            pageIndex = 0,
            pageSize = pageSize,
            pageCount = 1,
            content = emptyList<T>()
        )

        fun <T> from(pageReq: PageRequest, content: List<T>, itemCount: Long) =
            Page(
                pageIndex = pageReq.pageIndex,
                pageSize = pageReq.pageSize,
                pageCount = Page.pageCount(itemCount, pageReq.pageSize).toInt(),
                content = content
            )

        private fun pageCount(itemCount: Long, pageSize: Int): Long {
            val rem = itemCount % pageSize
            return (itemCount - rem) / pageSize + if (rem == 0L) 0 else 1
        }
    }
}

