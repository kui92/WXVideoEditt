package com.kui.wxvideoeditt;

import android.content.Context;
import android.media.ExifInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

/**
 * Created by ZBK on 2017/10/13.
 * Describe:
 */

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    private Context context;
    private List<Data> dataList;
    private int imagWidth=-1;
    private String parentPath;
    /**
     * 视频的旋转角度,手机拍摄的视频有时候会有一个旋转角度，因此解析出的图片也有一个角度
     */
    private float rotation=0;

    public Adapter(Context context,List<Data> dataList){
        this.context=context;
        this.dataList=dataList;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_per_image,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (imagWidth>0){
            ViewGroup.LayoutParams layoutParams=holder.imageView.getLayoutParams();
            layoutParams.width=imagWidth;
            holder.imageView.setLayoutParams(layoutParams);
        }
        Glide.with(context).load(parentPath+dataList.get(position).getImageName()).transform( new RotateTransformation( context, rotation ))
                .into(holder.imageView);
    }

    public List<Data> getDataList() {
        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getItemCount() {
        return dataList==null?0:dataList.size();
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }


    public int getImagWidth() {
        return imagWidth;
    }

    public void setImagWidth(int imagWidth) {
        this.imagWidth = imagWidth;
    }


    class Holder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public Holder(View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.img);
        }
    }

}
