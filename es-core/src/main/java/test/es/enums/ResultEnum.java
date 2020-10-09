package test.es.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: nobody
 * @Date: 2020-07-31 14:08
 * @Desc:
 */
@Getter
@AllArgsConstructor
public enum ResultEnum {

    ERROR(0,"服务器繁忙，请稍后再试。"),
    SUCCESS(1,"OK"),
    ACTIVITY_NOT_START(1001,"活动尚未开始，敬请期待吧！"),
    ACTIVITY_IS_END(1002,"活动已结束，感谢参与！"),
    ACTIVITY_OFF_LINE(1003,"活动已下线，感谢参与！"),
    LACK_OF_PARAMETER(1004,"{}参数缺失！"),
    CHANCE_NOT_ENOUGH(1010,"很抱歉，您的{}机会不足！"),
    UNSUPPORTED_OPERATION(1011, "暂不支持该操作"),
    PRIZE_RECORD_NOT_FOUND(1012, "很抱歉，系统未找到对应的获奖记录。"),
    RUN_OUT(1013,"今日{}已用完，请明天再来吧~~"),
    NOT_EXIST(1014,"{}不存在！"),
    ALREADY_EXIST(1015,"{}已存在！"),
    NOT_SUPPORT(1015,"很抱歉，暂时不支持{}。"),
    PRODUCT_NOT_SUPPORTED(3001,"很抱歉，系统暂时不支持该产品的订购！");

    private Integer code;
    private String msg;
}
