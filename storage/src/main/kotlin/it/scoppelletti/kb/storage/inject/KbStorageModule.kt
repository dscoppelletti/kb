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

@file:Suppress("RemoveRedundantQualifierName", "UNUSED")

package it.scoppelletti.kb.storage.inject

import dagger.Binds
import dagger.Module
import dagger.Provides
import it.scoppelletti.kb.domain.FileUseCase
import it.scoppelletti.kb.storage.FileUseCaseImpl
import it.scoppelletti.kb.storage.KbStorageExt
import javax.inject.Named
import javax.inject.Singleton
import org.apache.commons.lang3.SystemUtils
import software.amazon.awssdk.services.s3.S3Client

@Module
abstract class KbStorageModule {

    @Binds
    abstract fun bindFileUseCase(obj: FileUseCaseImpl): FileUseCase

    @Module
    companion object {

        @Suppress("WeakerAccess")
        const val DEF_BUCKET = "it-scoppelletti-articles"

        @Suppress("WeakerAccess")
        const val ENV_BUCKET = "KB_BUCKET"

        @Provides
        @JvmStatic
        @Singleton
        fun provideS3Client(): S3Client = S3Client.create()

        @Provides
        @JvmStatic
        @Singleton
        @Named(KbStorageExt.DEP_BUCKET_NAME)
        fun provideBucketName(): String =
            SystemUtils.getEnvironmentVariable(KbStorageModule.ENV_BUCKET,
                KbStorageModule.DEF_BUCKET)
    }
}