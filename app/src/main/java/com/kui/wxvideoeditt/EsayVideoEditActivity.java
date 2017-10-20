package com.kui.wxvideoeditt;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.esay.ffmtool.FfmpegTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.R.attr.start;
import static android.os.Environment.getExternalStorageDirectory;


/**
 * Created by ZBK on 2017/8/11.
 * Describe:仿微信10秒小视频编辑
 */

public class EsayVideoEditActivity extends AppCompatActivity implements RangeBar.OnRangeBarChangeListener {

    public static final String PATH = "path";
    private final int IMAGE_NUM=10;//每一屏图片的数量
    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.rangeBar)
    RangeBar rangeBar;
    @Bind(R.id.fram)
    FrameLayout fram;
    @Bind(R.id.uVideoView)
    VideoView uVideoView;
    private String videoPath;
    private String parentPath;
    private long videoTime;
    private Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int imagCount=0;//整个视频要解码图片的总数量
    FfmpegTool ffmpegTool;

    private int firstItem=0;//recycleView当前显示的第一项
    private int lastItem=0;//recycleView当前显示的最后一项
    private int leftThumbIndex=0;//滑动条的左端
    private int rightThumbIndex=IMAGE_NUM;//滑动条的右端
    private int startTime,endTime=IMAGE_NUM;//裁剪的开始、结束时间
    private String videoResutlDir;//视频裁剪结果的存放目录
    private String videoResutl;
    ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);
        ButterKnife.bind(this);
        videoPath = getIntent().getStringExtra(PATH);
        Log.i("onCreate","videoPath:"+videoPath);
        if (!new File(videoPath).exists()) {
            Toast.makeText(this, "视频文件不存在", Toast.LENGTH_LONG).show();
            finish();
        }
        String str="temp"+System.currentTimeMillis()/1000;
        parentPath= getExternalStorageDirectory().getAbsolutePath()
                +File.separator+"test"+File.separator+str+File.separator;
        videoResutlDir= getExternalStorageDirectory().getAbsolutePath()
                +File.separator+"test"+File.separator+"clicp";
        File file=new File(parentPath);
        if(!file.exists()){
            file.mkdirs();
        }
        rangeBar.setmTickCount(IMAGE_NUM+1);
        videoTime=UIUtil.getVideoDuration(videoPath);
        Log.i("onCreate","videoTime:"+videoTime);
        ffmpegTool=FfmpegTool.getInstance(this);
        ffmpegTool.setImageDecodeing(new FfmpegTool.ImageDecodeing() {
            @Override
            public void sucessOne(String s, int i) {
                adapter.notifyItemRangeChanged(i,1);
            }
        });
        initView();
        initData();
    }

    private void initView(){
        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview.setLayoutManager(linearLayoutManager);
        adapter=new Adapter(this,getDataList(videoTime));
        adapter.setParentPath(parentPath);
        adapter.setRotation(UIUtil.strToFloat(UIUtil.getVideoInf(videoPath)));
        recyclerview.setAdapter(adapter);
        recyclerview.addOnScrollListener(onScrollListener);
        rangeBar.setOnRangeBarChangeListener(this);//设置滑动条的监听
        uVideoView.setVideoPath(videoPath);
        uVideoView.start();
    }

    /**
     * 第一次解码，先解码两屏的图片
     */
    private void initData(){
        File parent=new File(parentPath);
        if (!parent.exists()){
            parent.mkdirs();
        }
        Toast.makeText(this,"第一次解码中，先解码两屏的图片",Toast.LENGTH_SHORT).show();
        runImagDecodTask(0,2 * IMAGE_NUM);
    }

    /**
     * 视频压缩
     * @param view
     */
    public void click2(View view){
        File file=new File(videoResutl);
        if (file.exists()){
            Toast.makeText(this,"开始压缩，过程可能比较漫长",Toast.LENGTH_SHORT).show();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    //压缩后视频的保存路径
                    String path=Environment.getExternalStorageDirectory().getAbsolutePath()
                            +File.separator+"test"+File.separator+"compress";
                    File file1=new File(path);
                    if (!file1.exists()){
                        file1.mkdirs();
                    }
                    ffmpegTool.compressVideo(videoResutl, path+File.separator, 3, new FfmpegTool.VideoResult() {
                        @Override
                        public void clipResult(int i, String s, String s1, boolean b, int i1) {
                            String result="压缩完成";
                            if (!b){
                                result="压缩失败";
                            }
                            Log.i("click2","s:"+s);//压缩前的视频
                            Log.i("click2","s1:"+s1);//压缩后的视频
                            Toast.makeText(EsayVideoEditActivity.this,result,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else {
            Toast.makeText(this,"未找到裁剪后的视频",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 裁剪
     * @param view
     */
    public void onclick(View view){
        uVideoView.stopPlayback();
        File file=new File(videoResutlDir);
        if (!file.exists()){
            file.mkdirs();
        }
        Toast.makeText(this,"开始裁剪视频",Toast.LENGTH_LONG).show();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String video=videoResutlDir+File.separator+"clip"+System.currentTimeMillis()/1000+".mp4";
                ffmpegTool.clipVideo(videoPath, video, startTime, endTime - startTime, 2, new FfmpegTool.VideoResult() {
                    @Override
                    public void clipResult(int i, String s, String s1, boolean b, int i1) {
                        Log.i("clipResult","clipResult:"+s1);
                        String re="裁剪视频完成";
                        if(!b){
                            re="裁剪视频失败";
                        }
                        videoResutl=s1;
                        Toast.makeText(EsayVideoEditActivity.this,re,Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    /**
     * 根据视频的时长，按秒分割成多个data先占一个位置
     * @return
     */
    public  List<Data> getDataList(long videoTime){
        List<Data> dataList=new ArrayList<>();
        int seconds= (int) (videoTime/1000);
        for (imagCount=0;imagCount<seconds;imagCount++){
            dataList.add(new Data(imagCount,"temp"+imagCount+".jpg"));
        }
        return dataList;
    }

    /**
     * rangeBar 滑动改变时监听，重新计算时间
     * @param rangeBar
     * @param leftThumbIndex
     * @param rightThumbIndex
     */
    @Override
    public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
        Log.i("onIndexChange","leftThumbIndex:"+leftThumbIndex+"___rightThumbIndex:"+rightThumbIndex);
        this.leftThumbIndex=leftThumbIndex;
        this.rightThumbIndex=rightThumbIndex;
        calStartEndTime();
    }

    /**
     * 计算开始结束时间
     */
    private void calStartEndTime(){
        int duration=rightThumbIndex-leftThumbIndex;
        startTime=firstItem+leftThumbIndex;
        endTime=startTime+duration;
        //此时可能视频已经结束，若已结束重新start
        if (!uVideoView.isPlaying()){
            uVideoView.start();
        }
        //把视频跳转到新选择的开始时间
        uVideoView.seekTo(startTime*1000);
    }


    private RecyclerView.OnScrollListener onScrollListener=new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Log.i("onScrollStateChanged","onScrollStateChanged :"+newState);
            if (newState==RecyclerView.SCROLL_STATE_IDLE){
                firstItem=linearLayoutManager.findFirstVisibleItemPosition();
                lastItem=linearLayoutManager.findLastVisibleItemPosition();
                List<Data> dataList=adapter.getDataList();
                for(int i=firstItem;i<=lastItem;i++){
                    if (!UIUtil.isFileExist(parentPath+dataList.get(i).getImageName())){
                        Log.i("onScrollStateChanged","not exist :"+i);
                        runImagDecodTask(i,lastItem-i+1);
                        break;
                    }
                }
            }
            calStartEndTime();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    /**
     * 运行一个图片的解码任务
     * @param start 解码开始的视频时间 秒
     * @param count 一共解析多少张
     */
    private void runImagDecodTask(final int start,final int count){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ffmpegTool.decodToImageWithCall(videoPath,parentPath,start,count);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){//获取到图片总的显示范围的大小后，设置每一个图片所占有的宽度
            adapter.setImagWidth(rangeBar.getMeasuredWidth()/IMAGE_NUM);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        uVideoView.pause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        uVideoView.stopPlayback();
        //最后不要忘了删除这个临时文件夹 parentPath
        //不然时间长了会在手机上生成大量不用的图片，该activity销毁后这个文件夹就用不到了
        //如果内存大，任性不删也可以
    }
}
