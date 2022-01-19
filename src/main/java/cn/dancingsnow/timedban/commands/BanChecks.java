package cn.dancingsnow.timedban.commands;

import cn.dancingsnow.timedban.Timedban;
import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;

public class BanChecks {

    @Inject
    public Timedban timedban;

    @Subscribe(order = PostOrder.EARLY)
    public void onPreLogin(PreLoginEvent event) {
        // 如果别的插件阻止了，那就不需要做检查了
        if (!event.getResult().isAllowed()) {
            return;
        }

        String name = event.getUsername();
        if (!timedban.banList.hasPlayer(name)) {
            return;
        }
        else if (timedban.banList.getBanPlayer(name).isBaned()){
            event.setResult(PreLoginEvent.PreLoginComponentResult.denied(timedban.banList.getBanPlayer(name).getBanMsg()));
        }
    }


}
