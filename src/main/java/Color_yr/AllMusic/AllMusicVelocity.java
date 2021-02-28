package Color_yr.AllMusic;

import Color_yr.AllMusic.Command.CommandVelocity;
import Color_yr.AllMusic.Event.EventVelocity;
import Color_yr.AllMusic.Side.SideVelocity.MetricsVelocity;
import Color_yr.AllMusic.Side.SideVelocity.SideVelocity;
import Color_yr.AllMusic.Side.SideVelocity.VelocityLog;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(id = "allmusic", name = "AllMusic", version = AllMusic.Version,
        url = "https://github.com/HeartAge/AllMusic_P", description = "全服点歌插件", authors = {"Color_yr"})
public class AllMusicVelocity {
    public static AllMusicVelocity plugin;
    public final ProxyServer server;
    public final Path dataDirectory;
    private final Logger logger;
    private final MetricsVelocity.Factory metricsFactory;
    public ChannelIdentifier channel;

    @Inject
    public AllMusicVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, MetricsVelocity.Factory metricsFactory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        plugin = this;
        AllMusic.log = new VelocityLog(logger);
        new AllMusic().init(dataDirectory.toFile());
        CommandMeta meta = server.getCommandManager().metaBuilder("music")
                // Specify other aliases (optional)
                .aliases("allmusic")
                .build();
        channel = () -> AllMusic.channel;
        AllMusic.Side = new SideVelocity();
//        server.getChannelRegistrar().register(channel);
        server.getCommandManager().register(meta, new CommandVelocity());
        server.getEventManager().register(this, new EventVelocity());
        metricsFactory.make(this, 6720);

        AllMusic.start();
    }

    @Subscribe
    public void onStop(ProxyShutdownEvent event) {
        AllMusic.stop();
    }
}
