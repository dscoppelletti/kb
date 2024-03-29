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

package it.scoppelletti.mongo.config

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

class PasswordAdapter : JsonAdapter<CharArray>() {

    override fun fromJson(reader: JsonReader): CharArray? {
        if (reader.peek() == JsonReader.Token.NULL) {
            return reader.nextNull()
        }

        val value = reader.nextString()
        return if (value.isBlank()) null else
            value.toCharArray()
    }

    override fun toJson(writer: JsonWriter, value: CharArray?) {
        throw SecurityException("Security policy violation.")
    }
}

@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonPassword
