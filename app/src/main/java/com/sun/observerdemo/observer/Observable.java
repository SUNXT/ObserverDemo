package com.sun.observerdemo.observer;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * 通过子类实现 run 方法，把主要在线程池中实现
 *
 * @author sunxuedian
 * @param <S> 成功回调的数据类型
 * @param <F> 失败回调的数据类型
 * @param <C> 完成回调的数据类型
 */
public abstract class Observable<S, F, C> implements Runnable{

    /**
     * 线程池，执行线程任务
     */
    private static ExecutorService sExecutor = Executors.newFixedThreadPool(3);

    /**
     * 存对应的观察者
     */
    private final ArrayList<Observer<S, F, C>> mObservers = new ArrayList<>();

    /**
     * 订阅的线程
     */
    private ThreadModel mSubscribeThread = ThreadModel.CURRENT_THREAD;

    /**
     * 注册观察者
     * @param observer 需要注册的观察者, 需要非空且未注册
     * @return this
     */
    public Observable<S, F, C> registerObserver(Observer<S, F, C> observer){
        if (observer == null){
            throw new IllegalArgumentException("The observer is null. ");
        }

        synchronized (mObservers){
            if (mObservers.contains(observer)){
                throw new IllegalStateException("The observer " + observer + " is already registered. ");
            }
            mObservers.add(observer);
        }
        return this;
    }

    /**
     * 移除一个观察者
     * @param observer 已注册的观察者, 需要非空且已注册
     * @return this
     */
    public Observable<S, F, C> unregisterObserver(Observer<S, F, C> observer){
        if (observer == null){
            throw new IllegalArgumentException("The observer is null. ");
        }

        synchronized (mObservers){
            int index = mObservers.indexOf(observer);
            if (index == -1){
                throw new IllegalStateException("The observer " + observer + " is not registered. ");
            }
            mObservers.remove(index);
        }
        return this;
    }

    /**
     * 移除所有观察者, 可以作为取消任务作用
     * @return this
     */
    public Observable<S, F, C> unregisterAll(){
        synchronized (mObservers){
            mObservers.clear();
        }
        return this;
    }

    /**
     * 执行任务
     * @return this
     */
    public Observable<S, F, C> execute(){
        sExecutor.execute(this);
        return this;
    }

    /**
     * 点阅者回调的线程，即观察者收到回调的线程设置
     * @param thread 订阅的线程
     * @return this
     */
    public Observable<S, F, C> subscribeOn(ThreadModel thread){
        mSubscribeThread = thread;
        return this;
    }

    /**
     * 供子类调用，处理业务逻辑的成功回调
     * @param data 回调的数据
     */
    protected void handleSuccess(S data){
        notifyObservers(new HandleObserver<>(data, CallbackType.SUCCESS));
    }

    /**
     * 供子类调用，处理业务逻辑的失败回调
     * @param data
     */
    protected void handleFail(F data){
        notifyObservers(new HandleObserver<>(data, CallbackType.FAIL));
    }

    /**
     * 供子类调用，处理业务逻辑的完成任务回调
     * @param data
     */
    protected void handleComplete(C data){
        notifyObservers(new HandleObserver<>(data, CallbackType.COMPLETE));
    }

    /**
     * 通知观察者
     * @param handleObserver
     */
    private void notifyObservers(HandleObserver handleObserver){

        switch (mSubscribeThread){
            case MAIN_THREAD:
                ThreadUtils.postUIThread(handleObserver);
                break;
            case WORK_THREAD:
                ThreadUtils.postWorkThread(handleObserver);
                break;

                default:
                    handleObserver.run();
        }
    }

    /**
     * 处理观察者的runnable类
     * @param <T>
     */
    private class HandleObserver<T> implements Runnable{

        T mValue;
        CallbackType mCallbackType;

        HandleObserver(T value, CallbackType type){
            mValue = value;
            mCallbackType = type;
        }


        @Override
        public void run() {

            for (Observer observer: mObservers){

                switch (mCallbackType){

                    case SUCCESS:
                        observer.onSuccess(mValue);
                        break;
                    case FAIL:
                        observer.onFail(mValue);
                        break;
                    case COMPLETE:
                        observer.onComplete(mValue);
                        break;

                        default:
                            break;
                }
            }
        }


    }

    /**
     * 回调的类型
     */
    private enum CallbackType {
        SUCCESS,
        FAIL,
        COMPLETE
    }

}
