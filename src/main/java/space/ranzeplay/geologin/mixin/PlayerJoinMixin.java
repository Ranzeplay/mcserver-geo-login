package space.ranzeplay.geologin.mixin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.ranzeplay.geologin.GeoLogin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Mixin(PlayerManager.class)
public class PlayerJoinMixin {
    @Inject(at = @At("TAIL"), method = "onPlayerConnect")
    private void playerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        var ipString = connection.getAddressAsString(true);
        var ip = ipString.substring(1, ipString.lastIndexOf(":"));
        GeoLogin.LOGGER.info("Player {} connected from {}", player.getName().getString(), ip);

        if(GeoLogin.CONFIG.isInWhitelist(ip)) {
            GeoLogin.LOGGER.info("Address of player {} is in IP whitelist", player.getName().getString());
            return;
        }

        boolean isAccept;
        String countryCode = GeoLogin.CACHE.getIfPresent(ip);
        if(countryCode != null) {
            GeoLogin.LOGGER.info("Cache hit for IP: {}", ip);
        }
        else {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("https://api.iplocation.net/?ip=" + ip))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();

                countryCode = jsonResponse.get("country_code2").getAsString();
                GeoLogin.CACHE.put(ip, countryCode);

                // Process the JSON response as needed
                GeoLogin.LOGGER.debug("IP Location Data: {}", jsonResponse);
            } catch (Exception e) {
                GeoLogin.LOGGER.error("Failed to get IP location data", e);
            }
        }

        if(GeoLogin.CONFIG.useWhitelist){
            isAccept = GeoLogin.CONFIG.countries.contains(countryCode);
        }else {
            isAccept = !GeoLogin.CONFIG.countries.contains(countryCode);
        }

        if(!isAccept) {
            connection.disconnect(Text.translatable("multiplayer.disconnect.not_whitelisted"));
            GeoLogin.LOGGER.info("Player {} from {} was kicked by GeoLogin policy", player.getName().getString(), countryCode);
        }
    }
}
