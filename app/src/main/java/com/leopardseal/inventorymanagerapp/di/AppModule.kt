package com.leopardseal.inventorymanagerapp.di

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.API.ImageAPI
import com.leopardseal.inventorymanagerapp.data.network.API.InviteAPI
import com.leopardseal.inventorymanagerapp.data.network.API.ItemAPI
import com.leopardseal.inventorymanagerapp.data.network.API.BoxAPI
import com.leopardseal.inventorymanagerapp.data.network.API.LoginAPI
import com.leopardseal.inventorymanagerapp.data.network.API.OrgAPI
import com.leopardseal.inventorymanagerapp.data.network.ServerComms
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    fun provideLoginApi(userPreferences : UserPreferences, serverComms: ServerComms) : LoginAPI{
        return serverComms.buildApi(LoginAPI::class.java, userPreferences)
    }

    @Provides
    fun provideInviteApi(userPreferences : UserPreferences, serverComms: ServerComms) : InviteAPI{
        return serverComms.buildApi(InviteAPI::class.java, userPreferences)
    }

    @Provides
    fun provideOrgApi(userPreferences : UserPreferences, serverComms: ServerComms) : OrgAPI{
        return serverComms.buildApi(OrgAPI::class.java, userPreferences)
    }

    @Provides
    fun provideItemApi(userPreferences : UserPreferences, serverComms: ServerComms) : ItemAPI {
        return serverComms.buildApi(ItemAPI::class.java, userPreferences)
    }

    @Provides
    fun provideBoxApi(userPreferences : UserPreferences, serverComms: ServerComms) : BoxAPI {
        return serverComms.buildApi(BoxAPI::class.java, userPreferences)
    }

    @Provides
    fun provideImageApi(serverComms: ServerComms) : ImageAPI {
        return serverComms.buildImageApi(ImageAPI::class.java)
    }

}