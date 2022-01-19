package cn.dancingsnow.timedban.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Calendar;

public class BanPlayer {
    private final String reason;
    private final Calendar calendar;
    private final int sec;

    public BanPlayer(int day, String reason) {
        this.calendar = Calendar.getInstance();
        this.sec = day * 24 * 60 * 60;
        this.reason = reason;
    }

    public Boolean isBaned() {
        Calendar now = Calendar.getInstance();
        if ((calendar.get(Calendar.SECOND) + sec) <= now.get(Calendar.SECOND)) {
            return Boolean.TRUE;
        }
        else return Boolean.FALSE;
    }

    public String getReason() {
        return reason;
    }

    private String getTimeStr(Calendar calendar) {
        String ret = "";

        String hour;
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
            hour = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        }else {
            hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        }
        String minute;
        if (calendar.get(Calendar.MINUTE) < 10) {
            minute = "0" + calendar.get(Calendar.MINUTE);
        }else {
            minute = String.valueOf(calendar.get(Calendar.MINUTE));
        }

        String second;
        if (calendar.get(Calendar.SECOND) < 10) {
            second = "0" + calendar.get(Calendar.SECOND);
        }else {
            second = String.valueOf(calendar.get(Calendar.SECOND));
        }
        ret += calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日 " + hour + ":" + minute + ":" + second;
        return ret;
    }

    public String getUnBanTimeStr() {
        Calendar unBanTime = (Calendar) calendar.clone();
        unBanTime.add(Calendar.SECOND, sec);
        return getTimeStr(unBanTime);
    }

    public Component getBanMsg() {
        return Component.text("你已经被封禁").color(NamedTextColor.RED)
                .append(Component.text("\n").append(Component.text("解禁时间：" + getUnBanTimeStr()).color(NamedTextColor.AQUA)));
    }
}
