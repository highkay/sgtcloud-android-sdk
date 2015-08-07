package me.gall.sgp.android.common;

import java.lang.reflect.Method;
import java.util.UUID;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceInfo {

	public static final String LOG_TAG = DeviceInfo.class.getSimpleName();

	public static WifiManager getWifiManager(Context context) {
		// TODO Auto-generated method stub
		return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	public static String getMacAddress(Context context) {
		String mac = null;
		if (context
				.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {

			try {
				mac = getWifiManager(context).getConnectionInfo()
						.getMacAddress();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(LOG_TAG, "mac=" + mac);
		}
		if (mac == null) {
			mac = "";
		}
		return mac;
	}

	public static String getDeviceICCID(Context context) {
		String iccid = null;
		if (context
				.checkCallingOrSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

			try {
				iccid = getTelephonyManager(context).getSimSerialNumber();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(LOG_TAG, "iccid=" + iccid);
		}
		if (iccid == null) {
			iccid = "";
		}
		return iccid;
	}

	private static TelephonyManager getTelephonyManager(Context context) {
		return (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
	}

	public static String getDeviceIMEI(Context context) {
		String imei = null;
		if (context
				.checkCallingOrSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
			try {
				imei = getTelephonyManager(context).getDeviceId();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(LOG_TAG, "imei=" + imei);
		}
		if (imei == null)
			imei = "";
		return imei;
	}

	private static String getDeviceIDHash(Context context) {
		String androidId = null;
		try {
			androidId = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
		} catch (Exception e) {
			try {
				Class<?> c = Class.forName("android.os.SystemProperties");
				Method get = c.getMethod("get", String.class);
				androidId = (String) get.invoke(c, "ro.serialno");
			} catch (Exception ignored) {
			}
		}
		Log.d(LOG_TAG, "androidId=" + androidId);
		if (androidId != null && !"9774d56d682e549c".equals(androidId)) {
			androidId = null;
		}
		return androidId;
	}

	private static final String LOCAL_UID = "LOCAL_UID";

	public static String getLocalGenerateUUID(Context context) {
		if (!Configuration.getSetting(context).contains(LOCAL_UID)) {
			Configuration.getSetting(context).edit()
					.putString(LOCAL_UID, UUID.randomUUID().toString())
					.commit();
		}
		return Configuration.getSetting(context).getString(LOCAL_UID, "");
	}

	public static String getDevicePhonenumber(Context context) {
		String phoneNumber = null;
		if (context
				.checkCallingOrSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

			try {
				phoneNumber = getTelephonyManager(context).getLine1Number();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d(LOG_TAG, "phoneNumber=" + phoneNumber);
		}
		if (phoneNumber == null) {
			phoneNumber = "";
		}
		return phoneNumber;
	}

	public static boolean isSimReady(Context context, int pos) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		Object sim_state = null;
		try {
			Method state = TelephonyManager.class.getMethod(
					"getSimStateGemini", new Class[] { int.class });
			sim_state = state.invoke(telephonyManager,
					new Object[] { new Integer(pos) });
			return sim_state.toString().equals("5");
		} catch (Exception e) {
			try {
				Method state = TelephonyManager.class.getMethod("getSimState",
						new Class[] { int.class });
				sim_state = state.invoke(telephonyManager,
						new Object[] { new Integer(pos) });
				return sim_state.toString().equals("5");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return false;
	}

	public static String getDeviceIMSI(Context context, int pos) {
		String imsi = null;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			Object sim_state = null;
			Object sim_imsi = null;
			Method state = TelephonyManager.class.getMethod(
					"getSimStateGemini", new Class[] { int.class });
			sim_state = state.invoke(telephonyManager,
					new Object[] { new Integer(pos) });
			Method imei = TelephonyManager.class.getMethod(
					"getSubscriberIdGemini", new Class[] { int.class });
			sim_imsi = imei.invoke(telephonyManager,
					new Object[] { new Integer(pos) });
			if (sim_state.toString().equals("5")) {
				Log.d(LOG_TAG, "IMSI of sim " + pos + " is " + imsi);
				imsi = sim_imsi.toString();
			} else {
				Log.d(LOG_TAG, "Sim " + pos + " is not ready.");
			}
		} catch (Exception e) {
			try {
				Object sim_state = null;
				Object sim_imsi = null;
				Method state = TelephonyManager.class.getMethod("getSimState",
						new Class[] { int.class });
				sim_state = state.invoke(telephonyManager,
						new Object[] { new Integer(pos) });
				Method imei = TelephonyManager.class.getMethod(
						"getSubscriberId", new Class[] { int.class });
				sim_imsi = imei.invoke(telephonyManager,
						new Object[] { new Integer(pos) });
				if (sim_state.toString().equals("5")) {
					Log.d(LOG_TAG, "IMSI of sim " + pos + " is " + imsi);
					imsi = sim_imsi.toString();
				} else {
					Log.d(LOG_TAG, "Sim " + pos + " is not ready.");
				}
			} catch (Exception e1) {
				Log.d(LOG_TAG, "It may not be a dual sim device." + e);
			}
		}
		return imsi;
	}

	public static final String getStandardDeviceIMSI(Context context) {
		String imsi = null;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			imsi = telephonyManager.getSubscriberId();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "Couldn't get the operator.");
		}
		Log.d(LOG_TAG, "imsi=" + imsi);
		return imsi;
	}

	public static final String getDeviceIMSI(Context context) {
		String imsi = null;
		if (isDualSim()) {
			try {
				int pos = getReadySim(context);
				imsi = getDeviceIMSI(context, pos);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			try {
				if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY
						|| telephonyManager.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN) {
					Log.d(LOG_TAG, "Sim state ready.");
					imsi = telephonyManager.getSubscriberId();
				} else
					throw new Exception("Sim card is not ready yet.");
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Couldn't get the operator.");
			}
		}
		if (imsi == null) {
			imsi = "";
		}
		Log.d(LOG_TAG, "imsi=" + imsi);
		return imsi;
	}

	/**
	 * 得到当前可以计费的sim卡编号
	 * 
	 * @return
	 * @version 1.0
	 * @author 黄承开 update 2013-3-6 下午8:32:26
	 */
	public static int getReadySim(Context context) {
		boolean sim_0 = isSimReady(context, 0);
		boolean sim_1 = isSimReady(context, 1);
		if (sim_0)
			return 0;
		else if (sim_1)
			return 1;
		else
			throw new IllegalStateException("None sim is ready.");
	}

	/**
	 * 判断当前设备是否为双卡设备，需要在init方法以后才会正确返回
	 * 
	 * @return 是否双卡设备
	 * @version 1.0
	 * @author 黄承开 update 2013-3-6 下午8:31:51
	 */
	public static boolean isDualSim() {
		boolean dualSim = true;
		try {
			TelephonyManager.class.getMethod("getSimStateGemini",
					new Class[] { int.class });
			Log.d(LOG_TAG, "It is a mediatek device.");
		} catch (Exception e) {
			try {
				TelephonyManager.class.getMethod("getSimState",
						new Class[] { int.class });
				Log.d(LOG_TAG, "It is a mstar device.");
			} catch (Exception e1) {
				dualSim = false;
			}
		}
		return dualSim;
	}

	public static String getOSVersion() {
		return "android-" + android.os.Build.VERSION.SDK_INT;
	}

	public static String getSerialNo() throws Exception {
		Class<?> c = Class.forName("android.os.SystemProperties");
		Method get = c.getMethod("get", String.class, String.class);
		return (String) (get.invoke(c, "ro.serialno", "unknown"));
	}

}
