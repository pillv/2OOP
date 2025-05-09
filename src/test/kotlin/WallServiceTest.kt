import org.junit.jupiter.api.Test


import org.junit.jupiter.api.Assertions.*

class WallServiceTest {

    @Test
    fun add_PostIdIsNotZero() {
        // Перед тестом очищаем сервис, если используем object
        WallService.clear()

        val post = Post(text = "Тест добавления")
        val addedPost = WallService.add(post)

        assertTrue(addedPost.id != 0, "ID поста должен быть отличен от 0 после добавления")
    }

    @Test
    fun updateExistingPostReturnsTrue() {
        WallService.clear()
        val post = WallService.add(Post(text = "Первый пост"))
        val updatedPost = post.copy(text = "Обновлённый пост")
        val result = WallService.update(updatedPost)
        assertTrue(result, "Обновление существующего поста должно возвращать true")
    }

    @Test
    fun updateNonExistingPostReturnsFalse() {
        WallService.clear()
        val post = Post(id = 999, text = "Не существующий пост")
        val result = WallService.update(post)
        assertFalse(result, "Обновление несуществующего поста должно возвращать false")
    }
}