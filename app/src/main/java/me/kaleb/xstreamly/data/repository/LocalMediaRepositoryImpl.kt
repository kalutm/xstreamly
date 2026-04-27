package me.kaleb.xstreamly.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.kaleb.xstreamly.domain.model.LocalAudio
import me.kaleb.xstreamly.domain.model.LocalVideo
import me.kaleb.xstreamly.domain.repository.LocalMediaRepository

class LocalMediaRepositoryImpl(
    private val contentResolver: ContentResolver
) : LocalMediaRepository {
    override fun observeVideos(): Flow<List<LocalVideo>> = flow {
        val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

            val videos = mutableListOf<LocalVideo>()
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(nameColumn) ?: "Unknown"
                val duration = cursor.getLong(durationColumn)
                val uri = ContentUris.withAppendedId(collection, id)

                videos.add(
                    LocalVideo(
                        id = id,
                        title = title,
                        duration = duration,
                        uri = uri
                    )
                )
            }
            emit(videos)   // send the list to the Flow
        } ?: emit(emptyList())
    }

    override fun observeAudios(): Flow<List<LocalAudio>> = flow {
        // 1. Decide which collection (URI) to query for audio files
        val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        // 2. Define what columns we want (for now we only need the title)
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,   // Title / filename
            MediaStore.Audio.Media.ARTIST,          // Optional: we can use later
            MediaStore.Audio.Media.DURATION
        )

        // 3. Sort by date added (newest first)
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

        // 4. Query the MediaStore
        contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST) // we can use later
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            val audios = mutableListOf<LocalAudio>()

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(nameColumn) ?: "Unknown Audio"
                val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                val duration = cursor.getLong(durationColumn)
                val uri = ContentUris.withAppendedId(collection, id)

                audios.add(
                    LocalAudio(
                        id = id,
                        title = title,
                        artist = artist,
                        duration = duration,
                        uri = uri
                    )
                )
            }

            emit(audios)   // Send the list of LocalAudio's
        } ?: emit(emptyList())   // Return empty list if query fails
    }
}


