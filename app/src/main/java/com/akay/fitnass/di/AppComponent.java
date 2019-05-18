package com.akay.fitnass.di;

import com.akay.fitnass.service.FitService;
import com.akay.fitnass.viewmodel.BaseViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, StorageModule.class})
public interface AppComponent {

    void inject(BaseViewModel viewModel);

    void inject(FitService service);
}
