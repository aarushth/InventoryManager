package com.leopardseal.inventorymanagerapp.di

import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.api.ImageAPI
import com.leopardseal.inventorymanagerapp.data.network.api.InviteAPI
import com.leopardseal.inventorymanagerapp.data.network.api.ItemAPI
import com.leopardseal.inventorymanagerapp.data.network.api.BoxAPI
import com.leopardseal.inventorymanagerapp.data.network.api.LoginAPI
import com.leopardseal.inventorymanagerapp.data.network.api.OrgAPI
import com.leopardseal.inventorymanagerapp.data.network.ServerComms
import com.leopardseal.inventorymanagerapp.data.network.api.LocationAPI
import com.leopardseal.inventorymanagerapp.data.network.api.ManageOrgAPI
import com.leopardseal.inventorymanagerapp.data.network.api.SearchAPI
import com.leopardseal.inventorymanagerapp.data.network.api.SettingAPI
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
    fun provideLocationApi(userPreferences : UserPreferences, serverComms: ServerComms) : LocationAPI {
        return serverComms.buildApi(LocationAPI::class.java, userPreferences)
    }

    @Provides
    fun provideImageApi(serverComms: ServerComms) : ImageAPI {
        return serverComms.buildImageApi(ImageAPI::class.java)
    }

    @Provides
    fun provideSettingApi(userPreferences : UserPreferences, serverComms: ServerComms) : SettingAPI {
        return serverComms.buildApi(SettingAPI::class.java, userPreferences)
    }

    @Provides
    fun provideSearchApi(userPreferences : UserPreferences, serverComms: ServerComms) : SearchAPI {
        return serverComms.buildApi(SearchAPI::class.java, userPreferences)
    }
    
    @Provides
    fun provideManageOrgApi(userPreferences : UserPreferences, serverComms : ServerComms) : ManageOrgAPI {
        return serverComms.buildApi(ManageOrgAPI::class.java, userPreferences)
    }

}