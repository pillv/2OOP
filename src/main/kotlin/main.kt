/// --- Вложения ---

interface Attachment {
    val type: String
}

// Данные для разных типов вложений
data class Photo(
    val id: Int,
    val ownerId: Int,
    val albumId: Int? = null,
    val userId: Int? = null,
    val text: String? = null,
    val date: Int, // Timestamp
    val photo75: String? = null,
    val photo130: String? = null,
    val photo604: String? = null,
    val photo807: String? = null,
    val photo1280: String? = null,
    val photo2560: String? = null,
    val width: Int? = null,
    val height: Int? = null
)

data class Video(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val description: String? = null,
    val duration: Int, // в секундах
    val photo130: String? = null, // превью
    val photo320: String? = null,
    val photo800: String? = null,
    val date: Int, // Timestamp добавления
    val addingDate: Int? = null, // Timestamp добавления в альбом
    val views: Int = 0,
    val comments: Int = 0,
    val player: String? = null, // URL плеера
    val platform: String? = null,
    val canEdit: Boolean = true,
    val canAdd: Boolean = true,
    val isPrivate: Boolean = false,
    val accessKey: String? = null,
    val processing: Boolean = false,
    val isFavorite: Boolean = false
)

data class Audio(
    val id: Int,
    val ownerId: Int,
    val artist: String,
    val title: String,
    val duration: Int, // в секундах
    val url: String? = null, // ссылка на mp3
    val lyricsId: Int? = null,
    val albumId: Int? = null,
    val genreId: Int? = null,
    val date: Int, // Timestamp
    val noSearch: Boolean = false,
    val isHq: Boolean = false
)

data class DocPreviewPhotoSize(
    val type: String,
    val url: String,
    val width: Int,
    val height: Int
)

data class DocPreviewPhoto(
    val sizes: List<DocPreviewPhotoSize> = emptyList()
)

data class DocPreviewGraffiti(
    val src: String,
    val width: Int,
    val height: Int
)

data class DocPreviewAudioMessage(
    val duration: Int,
    val waveform: List<Int> = emptyList(),
    val linkOgg: String,
    val linkMp3: String
)

data class DocPreview(
    val photo: DocPreviewPhoto? = null,
    val graffiti: DocPreviewGraffiti? = null,
    val audioMessage: DocPreviewAudioMessage? = null
)

data class Doc(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val size: Long, // в байтах
    val ext: String, // расширение файла
    val url: String? = null, // ссылка на файл
    val date: Int, // Timestamp
    val type: Int = 1, // тип документа (1 - текстовый, 2 - архив, и т.д.)
    val preview: DocPreview? = null
)

data class LinkProductPriceCurrency(
    val id: Int,
    val name: String // "RUB"
)

data class LinkProductPrice(
    val amount: Int, // цена в сотых долях единицы валюты (например, 10000 для 100.00 RUB)
    val currency: LinkProductPriceCurrency,
    val text: String // "100 руб."
)

data class LinkProduct(
    val price: LinkProductPrice
)

data class LinkButtonAction(
    val type: String, // "open_url"
    val url: String
)

data class LinkButton(
    val title: String,
    val action: LinkButtonAction
)

data class Link(
    val url: String,
    val title: String,
    val caption: String? = null,
    val description: String? = null,
    val photo: Photo? = null, // Может использовать существующий Photo data class
    val product: LinkProduct? = null,
    val button: LinkButton? = null,
    val previewPage: String? = null, // ID wiki-страницы
    val previewUrl: String? = null
)


// Классы-реализации Attachment
data class PhotoAttachment(val photo: Photo) : Attachment {
    override val type: String = "photo"
}

data class VideoAttachment(val video: Video) : Attachment {
    override val type: String = "video"
}

data class AudioAttachment(val audio: Audio) : Attachment {
    override val type: String = "audio"
}

data class DocAttachment(val doc: Doc) : Attachment {
    override val type: String = "doc"
}

data class LinkAttachment(val link: Link) : Attachment {
    override val type: String = "link"
}


// --- Конец секции вложений ---


// Вложенные объекты для Post (Comments, Likes - без изменений из предыдущего задания)
data class Comments(
    val count: Int = 0,
    val canPost: Boolean = true,
    val groupsCanPost: Boolean = true,
    val canClose: Boolean = false,
    val canOpen: Boolean = false
)

data class Likes(
    val count: Int = 0,
    val userLikes: Boolean = false,
    val canLike: Boolean = true,
    val canPublish: Boolean = true
)

// Основной класс поста
data class Post(
    val id: Int = 0,
    val ownerId: Int = 0,
    val fromId: Int = 0,
    val date: Int = 0,
    val text: String? = null,
    val replyOwnerId: Int? = null,
    val replyPostId: Int? = null,
    val friendsOnly: Boolean? = null,
    val comments: Comments = Comments(),
    val likes: Likes = Likes(),
    val repostsCount: Int = 0,
    val viewsCount: Int = 0,
    val postType: String = "post",
    val signerId: Int? = null,
    val canPin: Boolean = true,
    val canDelete: Boolean = true,
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val isAdvertisment: Boolean = false,
    val attachments: List<Attachment> = emptyList() // Добавлено поле для вложений
)

// Класс для хранения и работы с постами
object WallService {
    var posts = emptyArray<Post>()
    private var lastId = 0

    fun clear() {
        posts = emptyArray()
        lastId = 0
    }

    fun add(post: Post): Post {
        val postToAdd = post.copy(id = ++lastId)
        posts += postToAdd
        return posts.last()
    }

    fun update(newPost: Post): Boolean {
        for ((index, post) in posts.withIndex()) {
            if (post.id == newPost.id) {
                posts[index] = newPost.copy(
                    ownerId = post.ownerId, // Предполагаем, что ownerId и date не меняются при update
                    date = post.date,
                    likes = newPost.likes.copy(),
                    comments = newPost.comments.copy(),
                    // attachments копируется. .toList() создаст новую обертку-список.
                    // Так как Attachment-реализации - data class, их экземпляры неизменяемы (если их поля неизменяемы).
                    attachments = newPost.attachments.toList()
                )
                return true
            }
        }
        return false
    }

    fun printPosts() {
        if (posts.isEmpty()) {
            println("Нет постов на стене.")
            return
        }
        for (post in posts) {
            println("Пост ID: ${post.id}, Владелец ID: ${post.ownerId}, Текст: \"${post.text ?: ""}\"")
            println(" Лайки: ${post.likes.count}, Комментарии: ${post.comments.count}, Просмотры: ${post.viewsCount}")
            if (post.attachments.isNotEmpty()) {
                println(" Вложения (${post.attachments.size}):")
                for (att in post.attachments) {
                    print("    - Тип: ${att.type}, Дата: ")
                    when (att) {
                        is PhotoAttachment -> println("Фото (id=${att.photo.id}, Владелец Id=${att.photo.ownerId}, ссылка=${att.photo.photo604 ?: "N/A"})")
                        is VideoAttachment -> println("Видео(id=${att.video.id}, Заголовок='${att.video.title}', Продолжительность=${att.video.duration}s)")
                        is AudioAttachment -> println("Аудио(id=${att.audio.id}, Исполнитель='${att.audio.artist}', Название='${att.audio.title}')")
                        is DocAttachment -> println("Документ(id=${att.doc.id}, Название='${att.doc.title}', расширение='${att.doc.ext}')")
                        is LinkAttachment -> println("Ссылка(url='${att.link.url}', Название='${att.link.title}')")
                        else -> println("Неизвестный тип")
                    }
                }
            } else {
                println(" Нет вложений.")
            }
        }
        println("===================================")
    }
}


fun main() {
    WallService.clear()

    // Создаем объекты данных для вложений
    val photoData1 = Photo(
        id = 1,
        ownerId = 101,
        date = 1678886000,
        photo604 = "https://example.com/photo1.jpg",
        text = "Красивый закат"
    )
    val videoData1 =
        Video(id = 1, ownerId = 101, title = "Смешные котики", duration = 120, date = 1678886100, views = 1000)
    val audioData1 =
        Audio(id = 1, ownerId = 101, artist = "Известный Артист", title = "Хит лета", duration = 180, date = 1678886200)
    val docData1 =
        Doc(id = 1, ownerId = 101, title = "Отчет по проекту.docx", size = 204800, ext = "docx", date = 1678886300)
    val linkData1 = Link(
        url = "https://netology.ru",
        title = "Нетология - онлайн-университет",
        description = "Обучение востребованным профессиям"
    )

    // Создаем экземпляры Attachment
    val photoAttachment = PhotoAttachment(photoData1)
    val videoAttachment = VideoAttachment(videoData1)
    val audioAttachment = AudioAttachment(audioData1)
    val docAttachment = DocAttachment(docData1)
    val linkAttachment = LinkAttachment(linkData1)

    // 1. Добавляем первый пост с несколькими вложениями
    WallService.add(
        Post(
            ownerId = 101,
            fromId = 101,
            date = 1678886400,
            text = "Всем привет! Посмотрите мои новые фото и видео.",
            likes = Likes(count = 10, userLikes = true),
            comments = Comments(count = 2),
            attachments = listOf(photoAttachment, videoAttachment)
        )
    )
    println("  ")
    WallService.printPosts()

    // 2. Добавляем второй пост с другими вложениями
    WallService.add(
        Post(
            ownerId = 102,
            fromId = 102,
            date = 1678887400,
            text = "Делюсь полезным документом и классной музыкой.",
            attachments = listOf(docAttachment, audioAttachment)
        )
    )
    println("  ")
    WallService.printPosts()

    // 3. Добавляем третий пост только со ссылкой
    val post3 = WallService.add(
        Post(
            ownerId = 103,
            text = "Интересная статья:",
            attachments = listOf(linkAttachment)
        )
    )
    println("  ")
    WallService.printPosts()

    // 4. Обновляем первый пост: меняем текст и одно из вложений
    // Создаем новое фото для обновления
    val updatedPhotoData =
        photoData1.copy(id = 2, text = "Обновленное фото заката", photo604 = "https://example.com/updated_photo.jpg")
    val updatedPhotoAttachment = PhotoAttachment(updatedPhotoData)

    val postToUpdate = WallService.posts.first() // Берем первый пост для обновления (ID=1)
    val updatedPost1 = postToUpdate.copy(
        text = "Обновленный текст первого поста! И новое фото.",
        likes = postToUpdate.likes.copy(count = postToUpdate.likes.count + 5), // Увеличиваем лайки
        // Заменяем старое фото на новое, видео оставляем
        attachments = listOf(updatedPhotoAttachment, postToUpdate.attachments.find { it.type == "video" }!!)
    )

    if (WallService.update(updatedPost1)) {
        println("   ID: ${updatedPost1.id}")
        WallService.printPosts()
    } else {
        println("Ошибка ID: ${updatedPost1.id}")
    }

    // 5. Обновляем третий пост (ID=3), добавляя ему еще одно вложение (видео)
    val updatedPost3 = post3.copy(
        text = post3.text + " И еще видео!",
        attachments = post3.attachments + listOf(videoAttachment) // Добавляем видео к существующим вложениям
    )
    if (WallService.update(updatedPost3)) {
        println("ID: ${updatedPost3.id}")
        WallService.printPosts()
    } else {
        println("Ошибка ID: ${updatedPost3.id}")
    }

    // 6. Добавляем пост без вложений
    WallService.add(
        Post(
            ownerId = 104,
            text = "Этот пост без каких-либо вложений."
        )
    )
    println("")
    WallService.printPosts()
}