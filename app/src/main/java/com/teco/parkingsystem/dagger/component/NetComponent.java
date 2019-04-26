package com.teco.parkingsystem.dagger.component;



import com.teco.parkingsystem.AbstractFragment;
import com.teco.parkingsystem.BaseActivity;
import com.teco.parkingsystem.dagger.module.AppModule;
import com.teco.parkingsystem.dagger.module.NetModule;
import com.teco.parkingsystem.global.BaseUseCaseImpl;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by agile-01 on 7/6/2016.
 */

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(BaseActivity baseActivity);

    void inject(AbstractFragment abstractFragment);

    void inject(BaseUseCaseImpl baseUseCase);
    // void inject(MyFragment fragment);
    // void inject(ChatService service);
}
