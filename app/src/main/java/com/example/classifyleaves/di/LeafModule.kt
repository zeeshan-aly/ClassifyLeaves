package com.example.classifyleaves.di

import android.app.Application
import com.example.classifyleaves.LeafClassifier
import com.example.classifyleaves.LeafClassifierViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val leafModule = module {
    single { LeafClassifier(androidContext()) }
    viewModel { LeafClassifierViewModel(get()) }
} 