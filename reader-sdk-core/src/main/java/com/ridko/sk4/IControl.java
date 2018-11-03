package com.ridko.sk4;

import com.ridko.sk4.promise.Future;

/**
 * 控制接口
 *
 * @author smitea
 * @since 2018-10-30
 */
interface IControl {

  public void start();

  public Future<Void> stop();
}
