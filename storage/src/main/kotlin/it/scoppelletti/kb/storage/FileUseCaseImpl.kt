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

package it.scoppelletti.kb.storage

import it.scoppelletti.kb.domain.FileUseCase
import java.io.File
import javax.inject.Inject
import javax.inject.Named
import software.amazon.awssdk.http.HttpStatusCode
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.S3Exception

class FileUseCaseImpl @Inject constructor(
    private val s3Client: S3Client,

    @Named(KbStorageExt.DEP_BUCKET_NAME)
    private val bucketName: String
) : FileUseCase {

    override fun upload(name: String, file: File) {
        var exists: Boolean = true

        try {
            s3Client.headObject { builder ->
                builder.bucket(bucketName)
                builder.key(name)
            }
        } catch (ex: NoSuchKeyException) {
            exists = false
        }

        if (exists) {
            throw S3Exception.builder()
                .statusCode(HttpStatusCode.BAD_REQUEST)
                .message("Object $name already exists in bucket $bucketName.")
                .build()
        }

        s3Client.putObject({ builder ->
            builder
                .bucket(bucketName)
                .key(name)
        }, file.toPath());
    }

    override fun download(name: String, file: File) {
        s3Client.getObject({ builder ->
            builder
                .bucket(bucketName)
                .key(name)
        }, file.toPath())
    }

    override fun delete(name: String) {
        s3Client.deleteObject { builder -> builder
            .bucket(bucketName)
            .key(name)
        }
    }
}