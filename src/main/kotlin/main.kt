// Вложенные объекты
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
    val text: String = "",
    val isPinned: Boolean = false,
    val comments: Comments = Comments(),
    val likes: Likes = Likes(),
    val repostsCount: Int = 0,
    val viewsCount: Int = 0,
    val postType: String = "post",
    val canPin: Boolean = true,
    val canDelete: Boolean = true,
    val isFavorite: Boolean = false,
    val isAdvertisment: Boolean = false
)

// Класс для хранения и работы с постами
object WallService {
    private var posts = emptyArray<Post>()
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
                    likes = newPost.likes.copy(),
                    comments = newPost.comments.copy()
                )
                return true
            }
        }
        return false
    }

    fun printPosts() {
        for (post in posts) {
            println(post)
        }
    }
}


fun main() {

    val likes = Likes(count = 5)
    val comments = Comments(count = 3)
    WallService.add(Post(
        id = 1,
        text = "Привет, привет",
        likes = likes,
        comments = comments
    ))
    WallService.add(Post(
        id = 1,
        text = "Hello hello",
        comments = comments
    ))
    WallService.printPosts()
}
