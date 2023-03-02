package com.joojn.meteoraddon.modules;

import com.joojn.meteoraddon.MeteorClientUtils;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.PacketUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.Packet;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;

import static com.joojn.meteoraddon.MeteorClientUtils.LOGGER;

public class PacketLogger extends Module {

    private final SettingGroup sgClient = settings.createGroup("Client");
    private final SettingGroup sgServer = settings.createGroup("Server");

    private final Setting<Boolean> clientPackets = sgClient.add(new BoolSetting.Builder()
            .name("log-sent-packets")
            .description("Log packets sent by client.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Set<Class<? extends Packet<?>>>> c2sPackets = sgClient.add(new PacketListSetting.Builder()
            .name("whitelist-packets")
            .description("Client-to-server packets to log.")
            .filter(aClass -> PacketUtils.getC2SPackets().contains(aClass))
            .visible(clientPackets::get)
            .build()
    );

    private final Setting<Boolean> serverPackets = sgServer.add(new BoolSetting.Builder()
            .name("log-received-packets")
            .description("Log packets received from server.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Set<Class<? extends Packet<?>>>> s2cPackets = sgServer.add(new PacketListSetting.Builder()
            .name("whitelist-packets")
            .description("Server-to-client packets to log.")
            .filter(aClass -> PacketUtils.getS2CPackets().contains(aClass))
            .visible(serverPackets::get)
            .build()
    );

    public PacketLogger() {
        super(
                MeteorClientUtils.FUN_CATEGORY,
                "packet-logger",
                "Logs all packets sent and received."
        );
    }

    @EventHandler
    public void onPacketSent(PacketEvent.Send event) {
        if (
                clientPackets.get() &&
                        c2sPackets.get().contains(event.packet.getClass())
        ) {

            String packetStr = formatPacket(event.packet);

            LOGGER.info("Sent packet: " + packetStr);

            if(mc.player != null)
            {
                Text text = Text.literal(
                                "Sent packet: "
                        )
                        .setStyle(
                                Style.EMPTY.withColor(
                                        0x29f500
                                )
                        )
                        .append(
                                Text.literal(packetStr)
                                        .setStyle(
                                                Style.EMPTY.withColor(
                                                        0xf5a300
                                                )
                                        )
                        );

                mc.player.sendMessage(
                        text,
                        false
                );
            }
        }
    }

    @EventHandler
    public void onPacketReceived(PacketEvent.Receive event)
    {
        if (
                serverPackets.get() &&
                        s2cPackets.get().contains(event.packet.getClass())
        )
        {
            String packetStr = formatPacket(event.packet);

            LOGGER.info("Received packet: " + packetStr);

            if(mc.player != null)
            {
                Text text = Text.literal(
                                "Received packet: "
                        )
                        .setStyle(
                                Style.EMPTY.withColor(
                                        0x29f500
                                )
                        )
                        .append(
                                Text.literal(packetStr)
                                        .setStyle(
                                                Style.EMPTY.withColor(
                                                        0xf5a300
                                                )
                                        )
                        );

                mc.player.sendMessage(
                        text,
                        false
                );
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Packet<?>> String formatPacket(T packet)
    {
        StringBuilder builder = new StringBuilder(
                PacketUtils.getName((Class<? extends Packet<?>>) packet.getClass())
        ).append("[");

        Arrays.stream(packet.getClass().getDeclaredFields()).filter(
                field -> !Modifier.isStatic(field.getModifiers())
        ).forEach(
                field -> {
                    try
                    {
                        field.setAccessible(true);
                        builder.append(field.get(packet)).append(", ");
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                }
        );

        // remove last comma
        builder.setLength(builder.length() - 2);

        return builder.append("]").toString();
    }

}
