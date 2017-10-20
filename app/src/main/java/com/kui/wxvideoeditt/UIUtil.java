package com.kui.wxvideoeditt;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;

import static android.content.Context.WINDOW_SERVICE;

/**
 * ================================================
 * 作    者：顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * 版    本：
 * 创建日期：2017/4/8-下午3:48
 * 描    述：
 * 修订历史：
 * ================================================
 */

public class UIUtil {

    /**
     * 获取视频的时间长度
     * @param path
     * @return
     */
    public static long getVideoDuration(String path){
        try {
            File file=new File(path);
            if (!file.exists())return 0;
            MediaMetadataRetriever mMetadataRetriever=new MediaMetadataRetriever();
            mMetadataRetriever.setDataSource(path);
            String time=mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            mMetadataRetriever.release();
            return stringToLong(time);
        }catch (Exception e){

        }
        return 0;
    }

    /**
     * 获取视频的旋转角度
     * @param videoPath
     */
    public static String getVideoInf(String videoPath){
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(videoPath);
        String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION); // 视频旋转方向
        Log.i("getVideoInf","rotation:"+rotation);
        retr.release();
        return rotation;
    }

    /**
     * 判断文件是否存在
     * @param path
     * @return
     */
    public static boolean isFileExist(String path){
        boolean exist=false;
        try {
            File file=new File(path);
            exist=file.exists();
        }catch (Exception e){

        }
        return exist;
    }


    /**
     * string转float
     * @param str
     * @return
     */
    public static float strToFloat(String str){
        float value=0;
        try {
            Float aFloat=Float.parseFloat(str);
            value=aFloat;
        }catch (Exception e){

        }
        return value;
    }


    /**
     * 字符串转为long
     * @param str
     * @return
     */
    public static long stringToLong(String str){
        long num = -1;
        try {
            num = Long.valueOf(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return  num;
    }


    public static int dip2px(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((float) dip * scale + 0.5F);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }
}
