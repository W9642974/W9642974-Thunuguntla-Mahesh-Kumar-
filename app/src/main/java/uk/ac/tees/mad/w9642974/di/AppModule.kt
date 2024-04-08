package uk.ac.tees.mad.w9642974.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.ac.tees.mad.w9642974.data.apiService.ApiService
import uk.ac.tees.mad.w9642974.data.repository.ApiRepository
import uk.ac.tees.mad.w9642974.data.repository.ApiRepositoryImpl
import uk.ac.tees.mad.w9642974.data.repository.AuthRepository
import uk.ac.tees.mad.w9642974.data.repository.AuthRepositoryImpl
import uk.ac.tees.mad.w9642974.data.repository.FirestoreRepository
import uk.ac.tees.mad.w9642974.data.repository.FirestoreRepositoryImpl
import uk.ac.tees.mad.w9642974.utils.QUOTES_BASE_URL
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providesRepo(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): AuthRepository =
        AuthRepositoryImpl(firebaseAuth, firebaseFirestore)

    @Provides
    @Singleton
    fun provideFirestore(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): FirestoreRepository = FirestoreRepositoryImpl(firebaseAuth, firebaseFirestore)

    @Singleton
    @Provides
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Singleton
    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(QUOTES_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun providesApiRepo(apiService: ApiService): ApiRepository = ApiRepositoryImpl(apiService)
}