package com.nowcoder.community.util;


public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_EVENT = "eventqueue";
    private static final String PREFIX_HTML_CACHE = "htmlcache";
    private static final String PREFIX_MIAOSHA_ORDER = "miaoshaorder";
    private static final String PREFIX_MIAOSHA_STOCK = "miaoshastock";
    private static final String PREFIX_MIAOSHA_OVER = "miaoshaover";

    // 某个实体的赞
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体
    // followee:userId:entityType -> zset(entityId,now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // 登录验证码
    // 用户在登录页面时还未登录，无法通过登录凭证标识用户，需要额外给一个owner临时凭证
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    // 登录凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 用户
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

    public static String getEventQueueKey(){
        return PREFIX_EVENT;
    }

    // 页面缓存Key
    public static String getHtmlCacheKey(String path, int pageIndex){
        return PREFIX_HTML_CACHE + SPLIT + path + SPLIT + pageIndex;
    }

    // 秒杀订单缓存Key
    public static String getMiaoshaOrder(int userId, int courseId){
        return PREFIX_MIAOSHA_ORDER + SPLIT + userId + SPLIT + courseId;
    }

    // 秒杀订单缓存批量删除Key
    public static String getMiaoshaOrder(int courseId){
        return PREFIX_MIAOSHA_ORDER + SPLIT + "*" + SPLIT + courseId;
    }

    // 秒杀库存缓存Key
    public static String getMiaoshaStockKey(int miaoshaCourseId){
        return PREFIX_MIAOSHA_STOCK + SPLIT + miaoshaCourseId;
    }

    // 秒杀结束标志key
    public static String getMiaoshaOverKey(int courseId){
        return PREFIX_MIAOSHA_OVER + SPLIT + courseId;
    }
}
