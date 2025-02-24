package space.ranzeplay.geologin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class GeoLogin implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("GeoLogin");
    public static Config CONFIG = new Config();

    public static Cache<String, String> CACHE;

    @Override
    public void onInitialize() {
        var configPath = FabricLoader.getInstance().getConfigDir();
        var configFile = configPath.resolve("geologin.json");

        if(!Files.exists(configFile)){
            try {
                Files.createFile(configFile);

                Files.write(configFile, new Gson().toJson(CONFIG).getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            var config = Files.readString(configFile);
            CONFIG = new Gson().fromJson(config, Config.class);

            CACHE = CacheBuilder.newBuilder()
                    .maximumSize(500)
                    .expireAfterWrite(CONFIG.cacheExpireMinutes, TimeUnit.MINUTES)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("GeoLogin initialized");
    }
}
