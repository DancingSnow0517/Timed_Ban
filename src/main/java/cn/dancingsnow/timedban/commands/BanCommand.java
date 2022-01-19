package cn.dancingsnow.timedban.commands;

import cn.dancingsnow.timedban.Timedban;
import cn.dancingsnow.timedban.utils.BanPlayer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;

@Singleton
public class BanCommand {

    @Inject
    private Timedban timedban;

    @Inject
    private ProxyServer server;

    public BanCommand() {
        timedban.server.getEventManager().register(timedban, timedban.injector.getInstance(BanCommand.class));
        timedban.commandManager.register(timedban.injector.getInstance(BanCommand.class).createBrigadierCommand());
    }

    public BrigadierCommand createBrigadierCommand() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("tban").requires(commandSource -> commandSource.hasPermission("timedban.admin"))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("username", StringArgumentType.string())
                        .suggests(this::getUsernameSuggestions)
                        .then(RequiredArgumentBuilder.<CommandSource, Integer>argument("day", IntegerArgumentType.integer())
                                .executes(context -> {
                                    String name = context.getArgument("username", String.class);
                                    Integer day = context.getArgument("day", Integer.class);
                                    BanPlayer banPlayer = new BanPlayer(day, "I don't know");
                                    timedban.banList.addBanPlayer(name, banPlayer);
                                    context.getSource().sendMessage(Component.text("封禁成功").color(NamedTextColor.GREEN));
                                    return 1;
                                })
                                .then(RequiredArgumentBuilder.<CommandSource, String>argument("reason", StringArgumentType.string())
                                        .executes(context -> {
                                            String name = context.getArgument("username", String.class);
                                            String reason = context.getArgument("reason", String.class);
                                            Integer day = context.getArgument("day", Integer.class);
                                            BanPlayer banPlayer = new BanPlayer(day, reason);
                                            timedban.banList.addBanPlayer(name, banPlayer);
                                            context.getSource().sendMessage(Component.text("封禁成功").color(NamedTextColor.GREEN));
                                            return 1;
                                        }))))
                .build();
        return new BrigadierCommand(node);
    }

    public CompletableFuture<Suggestions> getUsernameSuggestions(final CommandContext<CommandSource> context, final SuggestionsBuilder builder) {
        ConcurrentSkipListSet<String> playerSet = new ConcurrentSkipListSet<>();
        server.getAllPlayers().forEach(player -> playerSet.add(player.getUsername()));
        playerSet.forEach(
                username -> {
                    if (username.contains(builder.getRemaining())) {
                        builder.suggest(username);
                    }
                }
        );
        return builder.buildFuture();
    }

}
