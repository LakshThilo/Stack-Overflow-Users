package com.blpw.pixelex.features.stackUserList.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
//    @Provides
//    @Singleton
//    fun db(@ApplicationContext context: Context): PokemonDatabase =
//        Room.databaseBuilder(context, PokemonDatabase::class.java, "pokemon_database").build()
//
//    @Provides fun pokemonDao(db: PokemonDatabase): PokemonDao = db.pokemonDao()
}