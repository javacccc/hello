package com.bjw.ComAssistant;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.text.method.TextKeyListener;
import android.text.method.TextKeyListener.Capitalize;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bjw.R;
import com.bjw.bean.Serial;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import android_serialport_api.SerialPortFinder;
/*************************************************
 *@date：2017/10/31
 *@author：  zxj
 *@description： 串口与摄像头的总控类
*************************************************/
public class ComAssistantActivity extends Activity {
	//摄像头的参数
	private TextureView tv;
	private String mCameraId = "0";//摄像头id（通常0代表后置摄像头，1代表前置摄像头）
	private final int RESULT_CODE_CAMERA = 1;//判断是否有拍照权限的标识码
	private CameraDevice cameraDevice;
	private CameraCaptureSession mPreviewSession;
	private CaptureRequest.Builder mCaptureRequestBuilder, captureRequestBuilder;
	private CaptureRequest mCaptureRequest;
	private ImageReader imageReader;
	private int height = 0, width = 0;
	private Size previewSize;
	private ImageView iv;
	private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
	static {
		ORIENTATIONS.append(Surface.ROTATION_0, 90);
		ORIENTATIONS.append(Surface.ROTATION_90, 0);
		ORIENTATIONS.append(Surface.ROTATION_180, 270);
		ORIENTATIONS.append(Surface.ROTATION_270, 180);
	}
	//    串口的参数
	TextView editTextRecDisp;
	TextView editTextLines;
	CheckBox checkBoxAutoClear;
	Button ButtonClear;
	TextView TextViewCOMB;
	TextView TextViewBaudRateCOMB;
	RadioButton radioButtonTxt, radioButtonHex;
	SerialControl ComB;
	DispQueueThread DispQueue;//刷新显示线程
	SerialPortFinder mSerialPortFinder;//串口设备搜索
	int iRecLines = 0;//接收区行数
	//其余参数
	Button btnforexit;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serial_operate);
		DispQueue = new DispQueueThread();
		DispQueue.start();
		setControls();
		ComB = new SerialControl();
		OpenComPort(ComB);
		btnforexit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
	//----------------------------------------------------
	private void setControls() {
		setTitle("读卡模块");
		iv = (ImageView) findViewById(R.id.iv);
		editTextRecDisp = (TextView) findViewById(R.id.editTextRecDisp);
		editTextLines = (TextView) findViewById(R.id.editTextLines);
		checkBoxAutoClear = (CheckBox) findViewById(R.id.checkBoxAutoClear);
		ButtonClear = (Button) findViewById(R.id.ButtonClear);
		tv = (TextureView) findViewById(R.id.tv);
		tv.setSurfaceTextureListener(surfaceTextureListener);
		TextViewCOMB = (TextView) findViewById(R.id.TextViewCOMB);
		TextViewBaudRateCOMB = (TextView) findViewById(R.id.TextViewBaudRateCOMB);
		radioButtonTxt = (RadioButton) findViewById(R.id.radioButtonTxt);
		radioButtonHex = (RadioButton) findViewById(R.id.radioButtonHex);
		radioButtonTxt.setOnClickListener(new radioButtonClickEvent());
		radioButtonHex.setOnClickListener(new radioButtonClickEvent());
		ButtonClear.setOnClickListener(new ButtonClickEvent());
		TextViewCOMB.setText("/dev/ttyS4");
		TextViewBaudRateCOMB.setText("9600");
		btnforexit=(Button)findViewById(R.id.ButtonforExit);
		mSerialPortFinder = new SerialPortFinder();
	}
	//----------------------------------------------------Txt、Hex模式选择
	class radioButtonClickEvent implements RadioButton.OnClickListener {
		public void onClick(View v) {
			if (v == radioButtonTxt) {
				KeyListener TxtkeyListener = new TextKeyListener(Capitalize.NONE, false);
			} else if (v == radioButtonHex) {
				KeyListener HexkeyListener = new NumberKeyListener() {
					public int getInputType() {
						return InputType.TYPE_CLASS_TEXT;
					}
					@Override
					protected char[] getAcceptedChars() {
						return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
								'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F'};
					}
				};
			}
		}
	}
/*************************************************
 *@description： 清除按钮
*************************************************/
     class ButtonClickEvent implements View.OnClickListener {
		public void onClick(View v) {
			if (v == ButtonClear) {
				editTextRecDisp.setText("");
			}
		}
	}
/*************************************************
 *@description： 串口控制类
*************************************************/
      private class SerialControl extends SerialHelper {
		public SerialControl() {}
		protected void onDataReceived(final Serial ComRecData) {
			DispQueue.AddQueue(ComRecData);//线程定时刷新显示
		}
	}
/*************************************************
 *@description： 刷新显示线程
*************************************************/
	private class DispQueueThread extends Thread {
		private Queue<Serial> QueueList = new LinkedList<Serial>();
		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				final Serial ComData;
				while ((ComData = QueueList.poll()) != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							DispRecData(ComData);
						}
					});
					break;
				}
			}
		}
		public synchronized void AddQueue(Serial ComData) {
			QueueList.add(ComData);
		}
	}
/*************************************************
 *@description： 显示接收数据
*************************************************/
	private void DispRecData(Serial ComRecData) {
			if (ComRecData.flag == 8) {
			editTextRecDisp.setText("");
			String cardnum = ComRecData.totalnum.substring(4, 12);
			editTextRecDisp.setText(cardnum);
			editTextRecDisp.setSaveEnabled(false);
			iRecLines++;
			editTextLines.setText(String.valueOf(iRecLines));
			takePicture();
		}
	}
	/*************************************************
	 *@description： 关闭串口
	*************************************************/
	private void CloseComPort(SerialHelper ComPort) {
		ComPort.close();
	}
/*************************************************
 *@description：打开串口
*************************************************/
	public void OpenComPort(SerialHelper ComPort) {
		try {
			ComPort.open();
		} catch (SecurityException e) {
			ShowMessage("打开串口失败:没有串口读/写权限!");
		} catch (IOException e) {
			ShowMessage("打开串口失败:未知错误!");
		} catch (InvalidParameterException e) {
			ShowMessage("打开串口失败:参数错误!");
		}
	}
	/*************************************************
	 *@description： 显示消息
	*************************************************/
	private void ShowMessage(String sMsg) {
		Toast.makeText(this, sMsg, Toast.LENGTH_SHORT).show();
	}

	/*************************************************
	 *@date：2017/9/28
	 *@author： zxj
	 *@description： 调用摄像头的操作
	 *************************************************/
	/*************************************************
	 *@description：TextureView的监听
	 *************************************************/
	private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			ComAssistantActivity.this.width = width;
			ComAssistantActivity.this.height = height;
			openCamera();
		}
		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

		}
		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			stopCamera();
			return true;
		}
		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		}
	};
	/*************************************************
	 *@description：打开摄像头
	 *************************************************/
	private void openCamera() {
		CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		//设置摄像头特性
		setCameraCharacteristics(manager);
		try {
			if (ActivityCompat.checkSelfPermission(ComAssistantActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				//提示用户开户权限
				String[] perms = {"android.permission.CAMERA"};
				ActivityCompat.requestPermissions(ComAssistantActivity.this, perms, RESULT_CODE_CAMERA);

			} else {
				manager.openCamera(mCameraId, stateCallback, null);
			}

		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	/*************************************************
	 *@description： 设置摄像头的参数
	 *************************************************/
	private void setCameraCharacteristics(CameraManager manager) {
		try {
			// 获取指定摄像头的特性
			CameraCharacteristics characteristics
					= manager.getCameraCharacteristics(mCameraId);
			// 获取摄像头支持的配置属性
			StreamConfigurationMap map = characteristics.get(
					CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
			// 获取摄像头支持的最大尺寸
			Size largest = Collections.max(
					Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
			// 创建一个ImageReader对象，用于获取摄像头的图像数据
			imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
					ImageFormat.JPEG, 2);
			//设置获取图片的监听
			imageReader.setOnImageAvailableListener(imageAvailableListener, null);
			// 获取最佳的预览尺寸
			previewSize = chooseOptimalSize(map.getOutputSizes(
					SurfaceTexture.class), width, height, largest);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
		}
	}

	/*************************************************
	 *@description： 选择预览的尺寸
	 *************************************************/
	private static Size chooseOptimalSize(Size[] choices
			, int width, int height, Size aspectRatio) {
		// 收集摄像头支持的大过预览Surface的分辨率
		List<Size> bigEnough = new ArrayList<>();
		int w = aspectRatio.getWidth();
		int h = aspectRatio.getHeight();
		for (Size option : choices) {
			if (option.getHeight() == option.getWidth() * h / w &&
					option.getWidth() >= width && option.getHeight() >= height) {
				bigEnough.add(option);
			}
		}
		// 如果找到多个预览尺寸，获取其中面积最小的
		if (bigEnough.size() > 0) {
			return Collections.min(bigEnough, new CompareSizesByArea());
		} else {
			//没有合适的预览尺寸
			return choices[0];
		}
	}
	/*************************************************
	 *@description： 为Size定义一个比较器Comparator
	 *************************************************/
	static class CompareSizesByArea implements Comparator<Size> {
		@Override
		public int compare(Size lhs, Size rhs) {
			// 强转为long保证不会发生溢出
			return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
					(long) rhs.getWidth() * rhs.getHeight());
		}
	}

	/*************************************************
	 *@description： 摄像头状态的监听
	 *************************************************/
	private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
		// 摄像头被打开时触发该方法
		@Override
		public void onOpened(CameraDevice cameraDevice) {
			ComAssistantActivity.this.cameraDevice = cameraDevice;
			// 开始预览
			takePreview();
		}
		/*************************************************
		 *@description： 摄像头断开连接时触发该方法
		 *************************************************/
		@Override
		public void onDisconnected(CameraDevice cameraDevice) {
			ComAssistantActivity.this.cameraDevice.close();
			ComAssistantActivity.this.cameraDevice = null;
		}
		/*************************************************
		 *@description： 打开摄像头出现错误时触发该方法
		 *************************************************/
		@Override
		public void onError(CameraDevice cameraDevice, int error) {
			cameraDevice.close();
		}
	};

	/*************************************************
	 *@description： 开始预览
	 *************************************************/
	private void takePreview() {
		SurfaceTexture mSurfaceTexture = tv.getSurfaceTexture();
		//设置TextureView的缓冲区大小
		mSurfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
		//获取Surface显示预览数据
		Surface mSurface = new Surface(mSurfaceTexture);
		try {
			//创建预览请求
			mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
			// 设置自动对焦模式
			mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
			//设置Surface作为预览数据的显示界面
			mCaptureRequestBuilder.addTarget(mSurface);
			//创建相机捕获会话，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
			cameraDevice.createCaptureSession(Arrays.asList(mSurface, imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
				@Override
				public void onConfigured(CameraCaptureSession session) {
					try {
						//开始预览
						mCaptureRequest = mCaptureRequestBuilder.build();
						mPreviewSession = session;
						//设置反复捕获数据的请求，这样预览界面就会一直有数据显示
						mPreviewSession.setRepeatingRequest(mCaptureRequest, null, null);
					} catch (CameraAccessException e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onConfigureFailed(CameraCaptureSession session) {

				}
			}, null);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	/*************************************************
	 *@description： 拍照
	 *************************************************/
	private void takePicture() {
		try {
			if (cameraDevice == null) {
				Toast.makeText(getBaseContext(), "cameraDevice", Toast.LENGTH_SHORT).show();
				return;
			}
			// 创建拍照请求
			captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
			// 设置自动对焦模式
			captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
			// 将imageReader的surface设为目标
			captureRequestBuilder.addTarget(imageReader.getSurface());
			// 获取设备方向
			int rotation = getWindowManager().getDefaultDisplay().getRotation();
			// 根据设备方向计算设置照片的方向
			captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION
					, ORIENTATIONS.get(rotation));
			// 停止连续取景
			mPreviewSession.stopRepeating();
			//拍照
			CaptureRequest captureRequest = captureRequestBuilder.build();
			//设置拍照监听
			mPreviewSession.capture(captureRequest, captureCallback, null);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	/*************************************************
	 *@description： 监听拍照结果
	 *************************************************/
	private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
		// 拍照成功
		@Override
		public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
			// 重设自动对焦模式
			captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
			// 设置自动曝光模式
			captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
			try {
				//重新进行预览
				mPreviewSession.setRepeatingRequest(mCaptureRequest, null, null);
			} catch (CameraAccessException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
			super.onCaptureFailed(session, request, failure);
		}
	};
	/*************************************************
	 *@description： 监听拍照的图片
	 *************************************************/
	private ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
		// 当照片数据可用时激发该方法
		@Override
		public void onImageAvailable(ImageReader reader) {
			//先验证手机是否有sdcard
			String status = Environment.getExternalStorageState();
			if (!status.equals(Environment.MEDIA_MOUNTED)) {
				Toast.makeText(getApplicationContext(), "你的sd卡不可用。", Toast.LENGTH_SHORT).show();
				return;
			}
			// 获取捕获的照片数据
			Image image = reader.acquireNextImage();
			ByteBuffer buffer = image.getPlanes()[0].getBuffer();
			byte[] data = new byte[buffer.remaining()];
			buffer.get(data);
			//手机拍照都是存到这个路径
			String filePath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
			String picturePath = System.currentTimeMillis() + ".jpg";
			File file = new File(filePath, picturePath);
			try {
				//存到本地相册
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(data);
				fileOutputStream.close();
				//显示图片
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
				iv.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				Toast.makeText(getBaseContext(), "当前文件夹不存在", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				image.close();
			}
		}


	};

	/*************************************************
	 *@description： 动态赋予拍照权限
	 *************************************************/
	@Override
	public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
		switch (permsRequestCode) {
			case RESULT_CODE_CAMERA:
				boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
				if (cameraAccepted) {
					//授权成功之后，调用系统相机进行拍照操作等
					openCamera();
				} else {
					//用户授权拒绝之后，友情提示一下就可以了
					Toast.makeText(ComAssistantActivity.this, "请开启应用拍照权限", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
	/*************************************************
	 *@description： 启动拍照
	 *************************************************/
	private void startCamera() {
		if (tv.isAvailable()) {
			if (cameraDevice == null) {
				openCamera();
				Toast.makeText(getBaseContext(), "startCamera", Toast.LENGTH_SHORT).show();
			}
		} else {
			tv.setSurfaceTextureListener(surfaceTextureListener);
		}
	}
	/*************************************************
	 *@description： 停止拍照释放资源
	 *************************************************/
	private void stopCamera() {
		if (cameraDevice != null) {
			cameraDevice.close();
			cameraDevice = null;
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		if (cameraDevice != null) {
			stopCamera();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		startCamera();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		CloseComPort(ComB);
	}
}
