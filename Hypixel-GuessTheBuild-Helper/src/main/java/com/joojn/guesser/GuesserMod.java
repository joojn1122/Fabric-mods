package com.joojn.guesser;

import com.joojn.guesser.guess.GuessTheBuild;
import com.joojn.guesser.guess.SuggestCommander;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuesserMod implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("GuesserMod");

	public static CharSequence getPlainText(Text message)
	{
		boolean skip = false;
		StringBuilder builder = new StringBuilder();

		for(char c : message.getString().toCharArray())
		{
			if(c == '§')
			{
				skip = true;
			}
			else if(skip)
			{
				skip = false;
			}
			else
			{
				builder.append(c);
			}
		}

		return builder.toString();
	}

	@Override
	public void onInitialize() {

		// register commands
		ClientCommandRegistrationCallback.EVENT.register(SuggestCommander::register);

	}

	public static void sendChatMessage(String message)
	{
		if(MinecraftClient.getInstance().player == null) return;

		MinecraftClient.getInstance()
				.player.sendChatMessage(
						message,
						Text.literal(message)
				);
	}

	public static void addChatMessage(String message)
	{
		if(MinecraftClient.getInstance().player == null) return;

		MinecraftClient.getInstance().player
				.sendMessage(
						Text.literal(GuessTheBuild.PREFIX + message),
						false
				);
	}

}
