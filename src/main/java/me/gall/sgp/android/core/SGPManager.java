
package me.gall.sgp.android.core;

import java.util.Map;

import me.gall.sgp.android.common.DeviceInfo;
import me.gall.sgp.android.common.NetworkInfo;
import me.gall.sgp.sdk.entity.Server;
import me.gall.sgp.sdk.entity.User;
import me.gall.sgp.sdk.service.AchievementService;
import me.gall.sgp.sdk.service.AnnouncementService;
import me.gall.sgp.sdk.service.BlacklistService;
import me.gall.sgp.sdk.service.BossService;
import me.gall.sgp.sdk.service.CampaignService;
import me.gall.sgp.sdk.service.CheckinBoardService;
import me.gall.sgp.sdk.service.CheckinService;
import me.gall.sgp.sdk.service.DailyTaskService;
import me.gall.sgp.sdk.service.DelegateDidService;
import me.gall.sgp.sdk.service.FileStorageService;
import me.gall.sgp.sdk.service.FriendshipExtraService;
import me.gall.sgp.sdk.service.FriendshipService;
import me.gall.sgp.sdk.service.GachaBoxService;
import me.gall.sgp.sdk.service.GiftCodeService;
import me.gall.sgp.sdk.service.InvitationCodeService;
import me.gall.sgp.sdk.service.LeaderBoardService;
import me.gall.sgp.sdk.service.MailService;
import me.gall.sgp.sdk.service.NotificationService;
import me.gall.sgp.sdk.service.PlayerExtraService;
import me.gall.sgp.sdk.service.PrivateChannelService;
import me.gall.sgp.sdk.service.PublicChannelService;
import me.gall.sgp.sdk.service.RouterService;
import me.gall.sgp.sdk.service.SgpPlayerService;
import me.gall.sgp.sdk.service.StoreService;
import me.gall.sgp.sdk.service.StructuredDataService;
import me.gall.sgp.sdk.service.TicketService;
import me.gall.sgp.sdk.service.TimestampService;
import me.gall.sgp.sdk.service.UserService;
import me.gall.sgt.java.core.PlatformNetworkStateListener;
import me.gall.sgt.java.core.SGTContext;
import me.gall.sgt.java.core.SGTManager;
import me.gall.sgt.java.core.SGTServiceInterface;

import org.slf4j.Logger;

import android.content.Context;
import ch.qos.logback.classic.android.BasicLogcatConfigurator;

public final class SGPManager implements SGTServiceInterface, PlatformNetworkStateListener {

    static {
        BasicLogcatConfigurator.configureDefaultContext();
    }

    private SGTManager sgtManager;

    public static SGPManager getInstance(Context context, String appId,
            boolean sandbox) {
        return getInstance(context, appId, sandbox, false);
    }

    public static SGPManager getInstance(Context context, SGTContext sgtContext) {
        SGTManager sgtManager = SGTManager.getInstance(sgtContext);
        SGPManager sgpManager = new SGPManager();
        sgpManager.sgtManager = sgtManager;
        sgpManager.setContext(context);
        sgtManager.setPlatformNetworkStateListener(sgpManager);
        return sgpManager;
    }

    public static SGPManager getInstance(Context context, String appId,
            boolean sandbox, boolean offlineMode) {
        return getInstance(context, appId, null, null, sandbox, offlineMode);
    }

    public static SGPManager getInstance(Context context, String appId, String appKey, String appSecret,
            boolean sandbox, boolean offlineMode) {
        return getInstance(context, SGTContext.constructSGTContext(appId, appKey, appSecret, sandbox, offlineMode));
    }

    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public User quickLogin() throws Throwable {
        getSgtContext().getLogger().debug("Start quick login.");
        User user = null;
        String imei = DeviceInfo.getDeviceIMEI(context);
        String iccid = DeviceInfo.getDeviceICCID(context);
        String mac = DeviceInfo.getMacAddress(context);
        getSgtContext().getLogger().debug("imei" + imei + "iccid" + iccid + "mac" + mac);
        if (imei.equals("") && iccid.equals("")) {
            getSgtContext().getLogger().debug(
                    "This is a tablet device without 3g module. Use serialno as iccid.");
            String serialno = DeviceInfo.getSerialNo();
            getSgtContext().getLogger().debug("serialno" + serialno);
            user = getUserService().register(null, serialno, mac);
        } else {
            getSgtContext().getLogger().debug("This is a normal device.");
            user = getUserService().register(imei, iccid, mac);
        }
        getSgtContext().setCurrentUser(user);
        return user;
    }

    @Override
    public User signup(String username, String password) throws Throwable {
        User user = new User();
        user.setUserName(username);
        user.setPassword(password);
        String imei = DeviceInfo.getDeviceIMEI(context);
        String iccid = DeviceInfo.getDeviceICCID(context);
        String mac = DeviceInfo.getMacAddress(context);
        getSgtContext().getLogger().debug("imei:" + imei + "iccid:" + iccid + "mac:" + mac);
        if (imei.equals("") && iccid.equals("")) {
            getSgtContext().getLogger().debug(
                    "This is a tablet device without 3g module. Use serialno as iccid.");
            String serialno = DeviceInfo.getSerialNo();
            getSgtContext().getLogger().debug("serialno:" + serialno);
            user.setIccid(serialno);
            user.setMac(mac);
        } else {
            getSgtContext().getLogger().debug("This is a normal device.");
            user.setImei(imei);
            user.setIccid(iccid);
            user.setMac(mac);
        }
        user.setRegistryType(User.MANUAL);
        user = getUserService().register(user);
        // 废弃的实现
        // User user = getUserService().register(username, password);
        getSgtContext().setCurrentUser(user);
        return user;
    }

    @Override
    public SGTContext getSgtContext() {
        // TODO Auto-generated method stub
        return sgtManager.getSgtContext();
    }

    @Override
    public void setSgtContext(SGTContext sgtContext) {
        // TODO Auto-generated method stub
        sgtManager.setSgtContext(sgtContext);
    }

    @Override
    public String getAppId() {
        // TODO Auto-generated method stub
        return sgtManager.getAppId();
    }

    @Override
    public boolean isOfflineMode() {
        // TODO Auto-generated method stub
        return sgtManager.isOfflineMode();
    }

    @Override
    public int getRequestTimeout() {
        // TODO Auto-generated method stub
        return sgtManager.getRequestTimeout();
    }

    @Override
    public Server getCurrentServer() {
        // TODO Auto-generated method stub
        return sgtManager.getCurrentServer();
    }

    @Override
    public User getCurrentUser() {
        // TODO Auto-generated method stub
        return sgtManager.getCurrentUser();
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        sgtManager.init();
    }

    @Override
    public RouterService getRouterService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getRouterService();
    }

    @Override
    public UserService getUserService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getUserService();
    }

    @Override
    public AnnouncementService getAnnouncementService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getAnnouncementService();
    }

    @Override
    public SgpPlayerService getSgpPlayerService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getSgpPlayerService();
    }

    @Override
    public LeaderBoardService getLeaderBoardService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getLeaderBoardService();
    }

    @Override
    public MailService getMailService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getMailService();
    }

    @Override
    public FriendshipService getFriendshipService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getFriendshipService();
    }

    @Override
    public CheckinService getCheckinService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getCheckinService();
    }

    @Override
    public CampaignService getCampaignService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getCampaignService();
    }

    @Override
    public BossService getBossService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getBossService();
    }

    @Override
    public GachaBoxService getGachaBoxService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getGachaBoxService();
    }

    @Override
    public BlacklistService getBlacklistService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getBlacklistService();
    }

    @Override
    public StructuredDataService getStructuredDataService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getStructuredDataService();
    }

    @Override
    public TicketService getTicketService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getTicketService();
    }

    @Override
    public DelegateDidService getDelegateDidService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getDelegateDidService();
    }

    @Override
    public PlayerExtraService getPlayerExtraService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getPlayerExtraService();
    }

    @Override
    public PrivateChannelService getPrivateChannelService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getPrivateChannelService();
    }

    @Override
    public PublicChannelService getPublicChannelService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getPublicChannelService();
    }

    @Override
    public StoreService getStoreService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getStoreService();
    }

    @Override
    public User login(String username, String password) throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.login(username, password);
    }

    @Override
    public Server updateRouting(Map<String, String> params) throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.updateRouting(params);
    }

    @Override
    public void setCurrentUser(User user) {
        // TODO Auto-generated method stub
        sgtManager.setCurrentUser(user);
    }

    @Override
    public <T> T getService(Class<T> clazz) throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getService(clazz);
    }

    @Override
    public Server routeByChannel(String channel) throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.routeByChannel(channel);
    }

    @Override
    public boolean hasInternetConnection() {
        // TODO Auto-generated method stub
        return NetworkInfo.isConnectedOrConnecting(context);
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
        sgtManager.destroy();
    }

    @Override
    public Logger getLogger() {
        // TODO Auto-generated method stub
        return sgtManager.getLogger();
    }

    @Override
    public AchievementService getAchievementService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getAchievementService();
    }

    @Override
    public DailyTaskService getDailyTaskService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getDailyTaskService();
    }

    @Override
    public FileStorageService getFileStorageService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getFileStorageService();
    }

    @Override
    public FriendshipExtraService getFriendshipExtraService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getFriendshipExtraService();
    }

    @Override
    public GiftCodeService getGiftCodeService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getGiftCodeService();
    }

    @Override
    public NotificationService getNotificationService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getNotificationService();
    }

    @Override
    public TimestampService getTimestampService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getTimestampService();
    }

    @Override
    public CheckinBoardService getCheckinBoardService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getCheckinBoardService();
    }

    @Override
    public InvitationCodeService getInvitationCodeService() throws Throwable {
        // TODO Auto-generated method stub
        return sgtManager.getInvitationCodeService();
    }

}
