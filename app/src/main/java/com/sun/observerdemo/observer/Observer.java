package com.sun.observerdemo.observer;

/**
 *
 * Observer for {@link Observable}
 *
 * @author sunxuedian
 *
 * @param <S> The data's type of success callback
 * @param <F> The data's type of fail callback
 * @param <C> The data's type of complete callback
 */
public interface Observer<S, F, C> {

    /**
     * onSuccess
     * @param data
     */
    void onSuccess(S data);

    /**
     * onFail
     * @param data
     */
    void onFail(F data);

    /**
     * onComplete
     * @param c
     */
    void onComplete(C c);
}
