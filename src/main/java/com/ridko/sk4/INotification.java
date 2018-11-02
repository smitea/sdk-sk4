package com.ridko.sk4;

import com.ridko.sk4.entity.ChannelValue;
import com.ridko.sk4.listenter.ConnectEvent;
import com.ridko.sk4.listenter.ErrorEvent;
import com.ridko.sk4.listenter.IListenter;
import com.ridko.sk4.listenter.ITagListenter;

/**
 * 通知器
 * @author smitea
 * @since 2018-11-01
 */
public interface INotification {
  /** 设置连接状态监听器 */
  public void setConnectEventIListenter(IListenter<ConnectEvent> connectEventIListenter);

  /** 设置标签巡查监听器 */
  public void setTagListenter(ITagListenter tagListenter);

  /** 设置防盗门进出监听器 */
  @Deprecated
  public void setChannelValueIListenter(IListenter<ChannelValue> channelValueIListenter);

  /** 设置错误信息处理监听器 */
  public void setErrorEventIListenter(IListenter<ErrorEvent> errorEventIListenter);
}
