package cn.dancingsnow.timedban.commands;

import cn.dancingsnow.timedban.utils.BanPlayer;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TestCommand implements SimpleCommand {
    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("command.test");
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        BanPlayer test = new BanPlayer( 7, "123123");
        source.sendMessage(test.getBanMsg());


    }
}
