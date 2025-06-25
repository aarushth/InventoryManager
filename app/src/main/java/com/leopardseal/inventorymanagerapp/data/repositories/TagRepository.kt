package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.network.api.TagAPI
import com.leopardseal.inventorymanagerapp.data.responses.Tag
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val api: TagAPI
): BaseRepository() {

    suspend fun getTags() = safeApiCall {
        api.getTags()
    }

    private var cachedTags: List<Tag> = listOf()

    fun setCachedTags(tags: List<Tag>) {
        cachedTags = tags
    }
    fun getCachedTags(): Resource<List<Tag>> {
        if(cachedTags.isEmpty()){
            return Resource.Failure(false, null)
        }else{
            return Resource.Success<List<Tag>>(cachedTags)
        }
    }

    fun updateCachedTag(tag: Tag) {
        val index = cachedTags.indexOfFirst { it.id == tag.id }
        cachedTags = if (index != -1) {
            cachedTags.toMutableList().apply { this[index] = tag }
        } else {
            cachedTags + tag
        }
    }
    fun getCachedTagById(id: Long): Tag? {
        return cachedTags.find { it.id == id }
    }

    suspend fun addTag(tag : String) = safeApiCall  {
        api.addTag(tag)
    }

}