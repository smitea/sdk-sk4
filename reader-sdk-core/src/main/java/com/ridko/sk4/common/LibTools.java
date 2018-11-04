package com.ridko.sk4.common;

/**
 * 动态库加载工具
 *
 * @author smitea
 * @since 2018-10-30
 */
public class LibTools {
  private static final String[] WIN_64_PATH = new String[]{"win-64/rxtxParallel.dll", "win-64/rxtxSerial.dll"};
  private static final String[] WIN_86_PATH = new String[]{"win-x86/rxtxParallel.dll", "win-x86/rxtxSerial.dll"};
  private static final String[] LINUX_86_64_PATH = new String[]{"linux-x86_64/rxtxParallel.dll", "linux-x86_64/rxtxSerial.dll"};
  private static final String[] LINUX_I386_PATH = new String[]{"linux-i386/rxtxParallel.dll", "linux-i386/rxtxSerial.dll"};
  private static final String[] MACOS_PATH = new String[]{"mac-os/librxtxSerial.jnilib"};

  public static void load() throws RuntimeException {
    String arch = System.getProperty("os.arch");
    String system = System.getProperty("os.name");

    String[] libs = null;
    if (arch.contains("64") && system.contains("Windows")) {
      libs = WIN_64_PATH;
    } else if (arch.contains("86") && system.contains("Windows")) {
      libs = WIN_86_PATH;
    } else if (arch.contains("i386") && system.contains("Linux")) {
      libs = LINUX_I386_PATH;
    } else if (arch.contains("x86_64") && system.contains("Linux")) {
      libs = LINUX_86_64_PATH;
    }

    if(libs!=null) {
      for (String lib : libs) {
        System.load(LibTools.class.getResource("/").getPath() + lib);
      }
    }else{
      throw new RuntimeException("无法识别当前系统，请手动加载 [rxtxParallel.dll,rxtxSerial.dll] 串口驱动.");
    }
  }
}
