package com.sun.observerdemo.observer;

/**
 * 线程模式
 * @author sunxuedian
 */
public enum ThreadModel {

    /**
     * 当前线程
     */
    CURRENT_THREAD,

    /**
     * UI线程
     */
    MAIN_THREAD,

    /**
     * 工程线程
     */
    WORK_THREAD

}
