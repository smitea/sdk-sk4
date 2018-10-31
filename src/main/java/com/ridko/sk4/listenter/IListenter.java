package com.ridko.sk4.listenter;

/**
 * 监听器
 * @author smitea
 * @since 2018-10-30
 */
public interface IListenter<Event> {
    public void notify(Event event);
}
