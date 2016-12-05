package tfcard.wintone.ecar.com.ecarwintonetfcard.camera;

import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wintone.plateid.PlateRecognitionParameter;
import com.wintone.plateid.RecogService;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.Bind;
import tfcard.wintone.ecar.R;
import tfcard.wintone.ecar.com.ecarwintonetfcard.BaseActivity;
import tfcard.wintone.ecar.com.ecarwintonetfcard.util.LogUtils;
import tfcard.wintone.ecar.com.ecarwintonetfcard.util.SPKeyUtils;
import tfcard.wintone.ecar.com.ecarwintonetfcard.util.SPUtils;
import tfcard.wintone.ecar.com.tfcardlib.RecogniteHelper4WT;


/**
 * ===============================================
 * <p>
 * 类描述:
 * <p>
 * 创建人: Eric_Huang
 * <p>
 * 创建时间: 2016/9/23 15:53
 * <p>
 * 修改人:Eric_Huang
 * <p>
 * 修改时间: 2016/9/23 15:53
 * <p>
 * 修改备注:
 * <p>
 * ===============================================
 */
public class CameraActivity extends BaseActivity implements Camera.PreviewCallback {

    @Bind(R.id.sv_camera)
    SurfaceView sv_camera;

    /**
     * 前后置摄像头转换
     */
    @Bind(R.id.iv_camera_switch)
    ImageView iv_camera_switch;

    /**
     * 预览之前显示的Layout
     */
    @Bind(R.id.ll_camera_onpreview)
    LinearLayout ll_camera_onpreview;

    /**
     * 拍照之后显示的Layout
     */
    @Bind(R.id.ll_camera_aftertake)
    LinearLayout ll_camera_aftertake;

    /**
     * 闪关灯按钮
     */
    @Bind(R.id.btn_flashmode)
    Button btn_flashMode;

    /**
     * 拍照按钮
     */
    @Bind(R.id.btn_takepic)
    Button btn_takePic;

    /**
     * 保存图片按钮
     */
    @Bind(R.id.btn_savepic)
    TextView btn_savePic;

    /**
     * 取消所拍照片
     */
    @Bind(R.id.btn_cancelpic)
    Button btn_cancel;

    /**
     * 返回按钮
     */
    @Bind(R.id.iv_go_back)
    ImageView btn_go_back;

    /**
     * 背景
     */
    @Bind(R.id.background_fl)
    View background_fl;


    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;

    //0代表前置摄像头，1代表后置摄像头
    private int mCameraPosition = 1;
    //拍照图片数据
    private byte[] mPicData = null;
    //车牌号码
    private String mCarPlate;
    //识别帮助类
    public static RecogniteHelper4WT mRecogHelper;

    //判断是否能继续拍摄
    private boolean IsAction = false;

    //间隔两次识别一次
//    private int mNum = -1;
    private int nRet = -1;
    //声音
    private SoundPool mSoundPool;
    private int mSound;
    //文通序列号
    private String mSerialNum;

    @Override
    public int getLayoutId() {
        return R.layout.camera_activity;
    }

    @Override
    protected void initData() {
        //文通序列号获取
        mSerialNum = (String) SPUtils.get(SPKeyUtils.s_SERIAL_NUM, "");
        LogUtils.i("文通序列号", mSerialNum);
        //公司识别
        initSound();

        mSurfaceHolder = sv_camera.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(mCallback);
    }

    @Override
    public void initView() {

        sv_camera.setFocusable(true);
        sv_camera.setBackgroundColor(Color.TRANSPARENT);
        //显示拍照布局，隐藏保存布局
        ll_camera_onpreview.setVisibility(View.VISIBLE);
        ll_camera_aftertake.setVisibility(View.GONE);
        iv_camera_switch.setVisibility(View.VISIBLE);
        btn_takePic.setClickable(true);
        btn_takePic.setEnabled(true);
        btn_savePic.setClickable(true);
        btn_savePic.setEnabled(true);
    }

    @Override
    public void setOnInteractListener() {
        //转换前后置摄像头
        iv_camera_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCamera();
            }
        });

        //点击开启关闭闪光灯
        btn_flashMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrCloseFlash();
            }
        });

        //拍照
        btn_takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (IsAction) {
                    return;
                }
                takePicture();
            }
        });

        //保存图片
        btn_savePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePic();
                btn_savePic.setClickable(false);
                btn_savePic.setEnabled(false);
            }
        });

        //取消当前所拍照片
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏对应Layout
                IsAction = false;
                ll_camera_aftertake.setVisibility(View.GONE);
                ll_camera_onpreview.setVisibility(View.VISIBLE);
                iv_camera_switch.setVisibility(View.VISIBLE);
                if (mCamera != null) {
                    mCamera.startPreview();
                    mCamera.setPreviewCallback(CameraActivity.this);
                }
            }
        });

        //回退按钮
        btn_go_back.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               finish();

                                               //点击背景可聚焦
                                               background_fl.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View view) {
                                                       if (mCamera == null)
                                                           return;

                                                       mCamera.autoFocus(new Camera.AutoFocusCallback() {
                                                           @Override
                                                           public void onAutoFocus(boolean success, Camera camera) {
                                                               if (success) {
                                                                   initCamera();//实现相机的参数初始化
                                                               }
                                                           }

                                                       });
                                                   }
                                               });
                                           }
                                       }

        );
    }

    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            //当surfaceView关闭时，关闭预览并释放资源
            releaseResource();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (null == mCamera) {
                mCamera = Camera.open();
                //文通识别服务绑定
                mRecogHelper = RecogniteHelper4WT.getInstance(CameraActivity.this, mCamera);
                mRecogHelper.bindRecogService();
                try {
                    mCamera.setPreviewDisplay(mSurfaceHolder);
                    initCamera();
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (holder == null) {
                return;
            }
            //实现自动对焦
            if (mCamera != null) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            initCamera();//实现相机的参数初始化
                        }
                    }

                });
            }
        }
    };

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        //判断是否为拍照，IsAction为true则说明是拍照，则停止预览
        if (IsAction) {
            IsAction = false;
            mCamera.stopPreview();
            useWTRecognition(data, camera);

        } else {
//            mNum++;
//            if (mNum == 1) {
//                mNum = -1;
                useWTRecognition(data, camera);
//            }
        }
    }


    /**
     * 文通识别
     *
     * @param data   字节数组(图片)
     * @param camera 照相机
     */
    private void useWTRecognition(byte[] data, Camera camera) {
        if (mRecogHelper.isServiceIsConnected()) {
            nRet = mRecogHelper.getRecogBinder() != null ? mRecogHelper.getRecogBinder().getnRet() : nRet;

            int initPlateIDSDK = mRecogHelper.getInitPlateIDSDK();
            if (initPlateIDSDK == 0) {

                //识别参数设置
                PlateRecognitionParameter mPlateRecParam = new PlateRecognitionParameter();
                mPlateRecParam.picByte = data;
                mPlateRecParam.devCode = mSerialNum;

                //设置识别区域
                mPlateRecParam.height = mPreHeight;
                mPlateRecParam.width = mPreWidth;
                //初始化参数
                mPlateRecParam.plateIDCfg.bRotate = 1;
                mPlateRecParam.plateIDCfg.left = 0;
                mPlateRecParam.plateIDCfg.right = 0;
                mPlateRecParam.plateIDCfg.top = 0;
                mPlateRecParam.plateIDCfg.bottom = 0;

                Log.d(TAG, mPlateRecParam.devCode);

                //识别开始
                RecogService.MyBinder lBinder = mRecogHelper.getRecogBinder();
                String[] mFieldValue = lBinder.doRecogDetail(mPlateRecParam);
                if (nRet != 0) {
                    String[] str = {"" + nRet};
                    mRecogHelper.getResult(str, camera, data, (RecogniteHelper4WT.OnResult) new Geted());
                } else {
                    mRecogHelper.getResult(mFieldValue, camera, data, (RecogniteHelper4WT.OnResult) new Geted());
                }
            }
        }
    }

    /**
     * 相机参数的初始化设置
     */
    private void initCamera() {
        mParameters = mCamera.getParameters();
        mParameters.setPictureFormat(ImageFormat.JPEG);
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        setCameraSize(mParameters);
//        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);//1连续对焦
//        // 设置JPG照片的质量
        mParameters.setJpegQuality(85);

        setDisplay(mParameters, mCamera);
        mCamera.setParameters(mParameters);
        mCamera.setPreviewCallback(this);
//        camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
    }

    /**
     * 拍照
     */
    private void takePicture() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            mSoundPool.play(mSound, 1, 1, 0, 0, 1);
        }
        IsAction = true;
        ll_camera_aftertake.setVisibility(View.VISIBLE);
        ll_camera_onpreview.setVisibility(View.GONE);
        iv_camera_switch.setVisibility(View.GONE);
    }

    /**
     * 控制图像的正确显示方向
     */
    private void setDisplay(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }
    }

    /**
     * 实现的图像的正确显示
     */
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", int.class);
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存图片
     */
    private void savePic() {
        if (mPicData != null) {
            Toast.makeText(this, mCarPlate, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * 释放资源
     */
    public void releaseResource() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            mSurfaceHolder.removeCallback(mCallback);
            mSurfaceHolder = null;
        }
    }

    /**
     * 初始化声音
     */
    private void initSound() {
        if (mSoundPool == null) {
            mSoundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
            mSound = mSoundPool.load(Uri.parse("file:///system/media/audio/ui/camera_click.ogg").getPath(), 1);
        }
    }

    /**
     * 改变摄像头
     */
    private void changeCamera() {
        //摄像头个数
        int lCameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo lCameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < lCameraCount; i++) {
            Camera.getCameraInfo(i, lCameraInfo);//得到每一个摄像头的信息
            if (mCameraPosition == 1) {
                //转前置摄像头
                if (lCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    closeFlash();
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.setPreviewCallback(null);
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceView显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    mCamera.setDisplayOrientation(90);
                    mCameraPosition = 0;
                    mCamera.setPreviewCallback(this);
                    break;
                }
            } else {
                //转后置摄像头
                if (lCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCamera.stopPreview();
                    mCamera.setPreviewCallback(null);
                    mCamera.release();
                    mCamera = null;
                    mCamera = Camera.open(i);
                    try {
                        mCamera.setPreviewDisplay(mSurfaceHolder);//通过surfaceView显示取景画面
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    mCamera.setDisplayOrientation(90);
                    mCameraPosition = 1;
                    mCamera.setPreviewCallback(this);
                    break;
                }
            }
        }
    }

    /**
     * 打开或者关闭闪光灯
     */
    private void openOrCloseFlash() {
        if (mCamera != null && mParameters != null) {
            Camera.Parameters p = mCamera.getParameters();
            String lFlashMode = p.getFlashMode();//获取闪光灯的状态
            //闪光灯状态转换
            if (lFlashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                closeFlash();
            } else if (lFlashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
                openFlash();
            }
        }
    }

    /**
     * 关闭闪光灯
     */
    private void closeFlash() {
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mParameters.setExposureCompensation(-1);
        btn_flashMode.setText("打开闪光灯");
        if (mCamera != null)
            mCamera.setParameters(mParameters);
    }

    /**
     * 打开闪光灯
     */
    private void openFlash() {
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mParameters.setExposureCompensation(0);

        btn_flashMode.setText("关闭闪光灯");
        if (mCamera != null)
            mCamera.setParameters(mParameters);
    }

    /**
     * 获取车牌号后的回调类
     */
    class Geted implements RecogniteHelper4WT.OnResult {

        @Override
        public void onGeted(String fileName, String carPlate) {
            Log.i(TAG, "fileName=" + fileName + "\n" + "number=" + carPlate);
            mCarPlate = carPlate;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                mSoundPool.play(mSound, 1, 1, 0, 0, 1);
            }
            savePic();
        }

        @Override
        public String saveImage(byte[] data) {
            mPicData = data;
            return "";
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////设置预览区域////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean isFatty;
    private int mPreWidth;
    private int mPreHeight;

    /**
     * 设置预览扫描区域尺寸
     */
    private void setCameraSize(Camera.Parameters parameters) {
        //获取手机支持分辨率
        List<Camera.Size> list = parameters.getSupportedPreviewSizes();
        Camera.Size size;

        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);

        int width = metric.widthPixels;
        int height = metric.heightPixels;
        if (width * 3 == height * 4) {
            isFatty = true;
        }

        int length = list.size();
        int previewWidth = 480;
        int previewHeight = 640;
        int second_previewWidth;
        int second_previewheight;
        if (length == 1) {
            //如果只有一种预览分辨率，则直接赋值
            size = list.get(0);
            previewWidth = size.width;
            previewHeight = size.height;

        } else {
            for (int i = 0; i < length; i++) {
                size = list.get(i);
                if (isFatty) {
                    if (size.height <= 960 || size.width <= 1280) {

                        second_previewWidth = size.width;
                        second_previewheight = size.height;

                        previewWidth = second_previewWidth;
                        previewHeight = second_previewheight;
                    }
                } else {
                    if (size.height <= 960 || size.width <= 1280) {
                        second_previewWidth = size.width;
                        second_previewheight = size.height;
                        if (previewWidth <= second_previewWidth) {
                            previewWidth = second_previewWidth;
                            previewHeight = second_previewheight;
                        }
                    }
                }
            }
        }
        mPreWidth = previewWidth;
        mPreHeight = previewHeight;
        parameters.setPreviewSize(mPreWidth, mPreHeight);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecogHelper.unbindService();
    }
}
