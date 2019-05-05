package com.akay.fitnass.di;

import com.akay.fitnass.service.FitService;
import com.akay.fitnass.viewmodel.DetailViewModel;
import com.akay.fitnass.viewmodel.MainViewModel;
import com.akay.fitnass.viewmodel.TimerViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, StorageModule.class})
public interface AppComponent {

    void inject(MainViewModel viewModel);

    void inject(TimerViewModel viewModel);

    void inject(DetailViewModel viewModel);

    void inject(FitService service);
}
