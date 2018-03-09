package com.bjw.LabDeviceInfo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bjw.Common.DatebaseHelper;
import com.bjw.R;
import com.bjw.Service.DownLoadService;
import com.bjw.bean.LabDevice;
import com.bumptech.glide.Glide;

import java.io.IOException;

import static com.bjw.Common.StaticConfig.LabID;
import static com.bjw.Common.StaticConfig.databaseVersion;
import static com.bjw.Common.StaticConfig.databasename;

/**
 * 创建人：wxdn
 * 创建时间：2017/12/19
 * 功能描述：
 */

public class LabDeviceVideoActivity extends Activity {
    Intent intent1;
    private final String Tag = LabDeviceVideoActivity.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private Surface surface;
    private ImageView videoImage,IvLabDeviceImage;
    private ImageButton btforBack;
    private ImageButton pauseorplay;
    private TextView TvLabDeviceName,TvLabDeviceResource,TvLabDeviceIntroduct;
    private SeekBar seekBar;
    private TextureView textureView;
    private DatebaseHelper myDBHelper;  //本地数据库的帮助者
    private SQLiteDatabase db;
    private int flag=0;
    private int count1=1;
    private int LabDeviceId;
    private String LabDeviceName,LabDeviceImageUrl,LabDeviceSupplier,LabDeviceIntroduct,LabDeviceVideoUrl;
    private LabDevice labDevice;
    private Handler handler=new Handler();
    BroadcastReceiverToPlayLocal broadcastReceiverToPlayLocal;
/*************************************************
 *@description： 时间与播放同步
*************************************************/
    private final Runnable mTicker = new Runnable(){
        public void run() {
            long now = SystemClock.uptimeMillis();
            long next = now + (1000 - now % 1000);
            handler.postAtTime(mTicker,next);//延迟一秒再次执行runnable,就跟计时器一样效果
            if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
                seekBar.setProgress(mMediaPlayer.getCurrentPosition());//更新播放进度
            }
        }
    };
    /*************************************************
     *@description： 处理播放
     *************************************************/
    Handler handlerForPlayLocal = new Handler() {
        public void handleMessage(Message msg) {
            String path=msg.obj.toString();
            Toast.makeText(getBaseContext(), "播放本地视屏", Toast.LENGTH_SHORT).show();
//            final int playprogress=mMediaPlayer.getCurrentPosition();
            final int playprogress=mMediaPlayer.getDuration()/2;
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource("/mnt/internal_sd/班牌视频文件/test.mp4");
                mMediaPlayer.setSurface(surface);//设置渲染画板
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置播放类型
                mMediaPlayer.setOnCompletionListener(onCompletionListener);//播放完成监听
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {//预加载监听
                    @Override
                    public void onPrepared(MediaPlayer mp){//预加载完成
                        videoImage.setVisibility(View.GONE);//隐藏图片
                        mMediaPlayer.start();//开始播放
                        seekBar.setMax(mMediaPlayer.getDuration());//设置总进度
                        seekBar.setProgress(playprogress);
                        mMediaPlayer.seekTo(playprogress);
                        handler.post(mTicker);//更新进度
                    }
                });
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_playvideo);
        findView();
//        注册播放本地的广播
//        registerForPlayLocal();
        //得到有设备主界面传递过来的数据
        Intent intent=getIntent();
        LabDeviceId=intent.getIntExtra("LabDeviceId",Integer.MAX_VALUE);
        LabDeviceName=intent.getStringExtra("LabDeviceName");
        LabDeviceImageUrl=intent.getStringExtra("LabDeviceImageUrl");
        LabDeviceSupplier=intent.getStringExtra("LabDeviceSupplier");
        LabDeviceIntroduct =intent.getStringExtra("LabDeviceIntroduct");
        LabDeviceVideoUrl=intent.getStringExtra("LabDeviceVideoUrl");
        labDevice =new LabDevice(LabID,LabDeviceId,LabDeviceName,LabDeviceImageUrl,LabDeviceSupplier,LabDeviceIntroduct,LabDeviceVideoUrl);
        TvLabDeviceName.setText("设备名称: "+ labDevice.getLab_device_name());
        if(labDevice.getLab_device_supplier().equals("")|| labDevice.getLab_device_supplier()==null)
        {
            TvLabDeviceResource.setText("设备来源: " + "未知");
        }
        else {
            TvLabDeviceResource.setText("设备来源: " + labDevice.getLab_device_supplier());
        }
        if(labDevice.getLab_device_introduct()==null)
        {
            TvLabDeviceIntroduct.setText("设备简介: "+"无");
        }
        else {
            TvLabDeviceIntroduct.setText("设备简介: "+ labDevice.getLab_device_introduct());
        }
        if(LabDeviceImageUrl!=null) {
            Glide.with(getBaseContext()).load(labDevice.getLab_device_image_url()).into(IvLabDeviceImage);
        }
        if(LabDeviceId==Integer.MAX_VALUE)
        {
            Toast.makeText(getBaseContext(), "数据出错了", Toast.LENGTH_SHORT).show();
        }
        myDBHelper = new DatebaseHelper(getBaseContext(), databasename, null, databaseVersion);
        db = myDBHelper.getReadableDatabase();
        //查询数据库里面如果数据库里面有的就可以直接播放,没有就去获取相应的视频
        Cursor c = db.rawQuery("select * from DeciceVideos where lab_device_id=?",new String[]{LabDeviceId+""});
        int count=c.getCount();
        if(count==0)
        {
            //开启下载的服务,需要传递相应的数据到相应服务里面
            intent1=new Intent(getBaseContext(),DownLoadService.class);
            intent1.putExtra("url", labDevice.getLab_device_video_url());
            intent1.putExtra("LabDeviceName",LabDeviceName);
            intent1.putExtra("LabDeviceId",LabDeviceId);
            startService(intent1);
        }
        textureView.setSurfaceTextureListener(surfaceTextureListener);//设置监听函数  重写4个方法
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);//seekbar改变监听
        pauseorplay.setOnClickListener(pauseorplayListener);//播放按钮点击事件的处理，包括点击暂停与播放
        //点击返回的时候销毁当前的Activity
        btforBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    /*************************************************
     *@description： 控件绑定
    *************************************************/
    public void findView()
    {
        btforBack=(ImageButton)findViewById(R.id.im_bt_back_video);
        textureView=(TextureView) findViewById(R.id.textureview);
        videoImage=(ImageView) findViewById(R.id.video_image);
        IvLabDeviceImage=(ImageView) findViewById(R.id.lab_device_img);
        seekBar= (SeekBar) findViewById(R.id.seekbar);
        pauseorplay=(ImageButton)findViewById(R.id.pauseorplay);
        TvLabDeviceName=(TextView)findViewById(R.id.lab_device_name);
        TvLabDeviceResource=(TextView)findViewById(R.id.lab_device_resource);
        TvLabDeviceIntroduct=(TextView)findViewById(R.id.lab_device_introduct);
    }
    /*************************************************
     *@description： 播放界面的监听事件处理
    *************************************************/
    private TextureView.SurfaceTextureListener surfaceTextureListener=new TextureView.SurfaceTextureListener() {
        //SurfaceTexture可用
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            surface=new Surface(surfaceTexture);
            new PlayerVideoThread().start();//开启一个线程去播放视频
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,int height) {//尺寸改变
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {//销毁
            surface=null;
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer=null;
            return true;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {//更新
        }
    };
    /*************************************************
     *@description： 播放的线程
    *************************************************/
    private class PlayerVideoThread extends Thread{
        @Override
        public void run(){
            try {
                mMediaPlayer= new MediaPlayer();
                //去数据库里面查找是否有相应的记录
                Cursor c = db.rawQuery("select * from DeciceVideos where lab_device_id=?",new String[]{LabDeviceId+""});
                int count=c.getCount();
                //没有就直接播放网络视频并且开启下载的线程
                if(count==0)
                {
//              Uri uri = Uri.parse("rtmp://www.gvsun.net:19350/live/nj");
//              mMediaPlayer.setDataSource(LabDeviceVideoActivity.this,uri);
                Uri uri = Uri.parse(labDevice.getLab_device_video_url());
//              Uri uri = Uri.parse("android.resource://com.bjw.ComAssistant/"+R.raw.ansen);
                mMediaPlayer.setDataSource(LabDeviceVideoActivity.this,uri);
//                mMediaPlayer.setDataSource("/mnt/internal_sd/班牌视频文件/test.mp4");
                }
                else//有数据就直接找出响应的本地数据的地址，然后进行相应的播放
                {
                    String lab_device_video_path="";
                    while(c.moveToNext())
                    {
                        int lab_room_id = c.getInt(c.getColumnIndex("lab_room_id"));
                        int lab_device_id = c.getInt(c.getColumnIndex("lab_device_id"));
                        String lab_device_name = c.getString(c.getColumnIndex("lab_device_name"));
                        lab_device_video_path = c.getString(c.getColumnIndex("lab_device_video_path"));
                        Log.i("zxj", lab_device_id+" "+lab_room_id+"名称："+lab_device_name+"地址"+lab_device_video_path);
                    }
                    //播放本地视频
//                    mMediaPlayer.setDataSource("/mnt/internal_sd/班牌视频文件/test.mp4");
                    mMediaPlayer.setDataSource(lab_device_video_path);
                }
                mMediaPlayer.setSurface(surface);//设置渲染画板
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置播放类型
                mMediaPlayer.setOnCompletionListener(onCompletionListener);//播放完成监听
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {//预加载监听
                    @Override
                    public void onPrepared(MediaPlayer mp){//预加载完成
                        videoImage.setVisibility(View.GONE);//隐藏图片
                        mMediaPlayer.start();//开始播放
                        seekBar.setMax(mMediaPlayer.getDuration());//设置总进度
                            handler.post(mTicker);//更新进度
                    }
                });
                mMediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*************************************************
     *@description： 播放与暂停按钮的点击事件的处理
    *************************************************/
    private View.OnClickListener pauseorplayListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                if(count1==1) {
                    pauseorplay.setImageResource(R.drawable.pause);
                    mMediaPlayer.pause();
                    count1++;
                    flag=1;//表示当前处于暂停状态
                }
                if(count1!=1) {
                    if (flag == 1) {
                        pauseorplay.setImageResource(R.drawable.play);
                        mMediaPlayer.start();
                        mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition());
                    }
                    else if(flag==0)
                    {
                        pauseorplay.setImageResource(R.drawable.pause);
                        mMediaPlayer.pause();
                    }
                    if(flag==0)
                    {
                        flag=1;
                    }
                    else if(flag==1)
                    {
                        flag=0;
                    }
                }
        }
    };
     /*************************************************
     *@description： 进度条的监听事件
    *************************************************/
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener=new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {//进度改变
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {//开始拖动seekbar
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {//停止拖动seekbar
            if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){//播放中
                mMediaPlayer.seekTo(seekBar.getProgress());
            }
        }
    };
    /*************************************************
     *@description： 当播放完成时所做的操作
    *************************************************/
    private MediaPlayer.OnCompletionListener onCompletionListener=new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {//播放完成
//            videoImage.setVisibility(View.VISIBLE);
            seekBar.setProgress(0);
            mediaPlayer.start();
            mediaPlayer.seekTo(0);
//            handler.removeCallbacks(mTicker);//删除执行的Runnable 终止计时器
        }
    };

    /*************************************************
     *@description： 注册接收播放本地的广播
     *************************************************/
    public void registerForPlayLocal()
    {
        broadcastReceiverToPlayLocal=new BroadcastReceiverToPlayLocal();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(6);
        filter.addAction("com.BrocastForPlayLocal");
        registerReceiver(broadcastReceiverToPlayLocal, filter);
    }
    /*************************************************
     *@description： 定时接收播放本地广播接收器
     *************************************************/
    public class BroadcastReceiverToPlayLocal extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String path=intent.getStringExtra("BrocastForPlayLocal");
            Message mes=new Message();
            mes.obj=path;
            handlerForPlayLocal.sendMessage(mes);
        }
    }
    @Override
    public void onDestroy()
    {
//        销毁时后需要停止该服务，不然会出现异常状况
//        但是线程没有销毁，所以还是会下载相应的数据
        if(intent1!=null)
        {
        stopService(intent1);
        }
        super.onDestroy();
    }
}
