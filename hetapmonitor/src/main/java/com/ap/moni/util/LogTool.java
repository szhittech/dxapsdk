package com.het.websocket.log;

import com.het.log.Logc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogTool {
    public static String TAG = "";
    private Thread thread;
    static Thread  cacheThread,clsCaThread;
    private ILogNotify logNotify;

    public LogTool(ILogNotify notify) {
        this.logNotify = notify;
    }

    public void startLiveLogThread() {
        if (thread != null) {
            thread.interrupt();
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader bufferedReader = null; //将捕获内容转换为BufferedReader
                Process process = null;
                try {
                    String cmd = "logcat -v time";
                    Logc.i("+===*********************startLiveLogThread "+cmd);
                    process = Runtime.getRuntime().exec(cmd);
                    bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while (((line = bufferedReader.readLine()) != null)) {
                        //清理日志，如果你这里做了system print，那么你输出的内容也会被记录，就会出现问题
                        if (line != null && line.contains(TAG)) {
                            if (logNotify!=null){
                                logNotify.notify(line);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Logc.i("+===*********************2"+e.getMessage());
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (process!=null){
                        process.destroy();
                    }
                    Logc.i("+===*****************finally*****3");
                }

            }
        });
        thread.start();
    }

    public void stopLiveLogThread() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    public static void getBufferLog(final ILogNotify notify) {
        if (cacheThread != null)
            return;
        cacheThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader bufferedReader = null; //将捕获内容转换为BufferedReader
                Process process = null;
                try {
                    String cmd = "logcat -d time";
                    //Runtime.getRuntime().exec("logcat -c").waitFor();
                    process = Runtime.getRuntime().exec(cmd);
                    bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    //  Runtime.runFinalizersOnExit(true);
                    String line;
                    while (((line = bufferedReader.readLine()) != null)) {
                        //清理日志，如果你这里做了system print，那么你输出的内容也会被记录，就会出现问题
                        if (line != null && line.contains(TAG)) {
                            if (notify!=null){
                                notify.notify(line);
                            }
                        }

                        Thread.sleep(1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (process!=null){
                        process.destroy();
                    }
                    cacheThread = null;
                }

            }
        });
        cacheThread.start();
    }

    public static void clearBufferLog(final ILogNotify notify) {
        if (clsCaThread!=null)
            return;
        clsCaThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader bufferedReader = null; //将捕获内容转换为BufferedReader
                StringBuffer sb = new StringBuffer();
                Process process = null;
                try {
                    String cmd = "logcat -c time";
                    //Runtime.getRuntime().exec("logcat -c").waitFor();
                    process = Runtime.getRuntime().exec(cmd);
                    bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    //  Runtime.runFinalizersOnExit(true);
                    String line;
                    while (((line = bufferedReader.readLine()) != null)) {
                        //清理日志，如果你这里做了system print，那么你输出的内容也会被记录，就会出现问题
                        if (line != null && line.contains(TAG)) {
                            sb.append(line);
                        }
                        Thread.sleep(1);
                    }
                    if (notify!=null){
                        notify.notify("############清空缓冲区==============================\r\n"+sb.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (process!=null){
                        process.destroy();
                    }
                    clsCaThread = null;
                }

            }
        });
        clsCaThread.start();
    }

    public interface ILogNotify {
        void notify(String text);
    }

}
