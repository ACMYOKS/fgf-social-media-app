package com.acapp1412.socialmedia.network.di

import com.acapp1412.socialmedia.network.AppNetworkMonitor
import com.acapp1412.socialmedia.network.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NetworkMonitorModule {
    @Binds
    fun providesNetworkMonitor(appNetworkMonitor: AppNetworkMonitor): NetworkMonitor
}