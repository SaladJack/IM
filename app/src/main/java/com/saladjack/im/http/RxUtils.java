package com.saladjack.im.http;





import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by saladjack on 17/1/25.
 */

public class RxUtils {
    public static <T> Observable.Transformer<T,T>normalSchedulers(){
        return source -> source.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static<S> S createService(Class<S> serviceClass) {
        return RetrofitMananger.getInstance().getRetrofit().create(serviceClass);
    }
}
