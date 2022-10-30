package me.joojn.nickchanger.mixin;

import me.joojn.nickchanger.NickChanger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Session;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Field;

@Mixin({MultiplayerScreen.class})
public abstract class MultiplayerScreenMixin extends Screen{

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("HEAD"), method="init()V")
    protected void init(CallbackInfo info)
    {
        TextFieldWidget textField = this.addDrawableChild(new TextFieldWidget(
                MinecraftClient.getInstance().textRenderer,
                5,
                5,
                100,
                20,
                Text.empty()
        ));

        this.addDrawableChild(new ButtonWidget(
                110,
                5,
                50,
                20,
                Text.literal("Change"),
                (button) -> {

                    NickChanger.LOGGER.info(
                            "Changing name to '%s'".formatted(textField.getText())
                    );

                    try
                    {
                        Field field = Session.class.getDeclaredField("username");
                        field.setAccessible(true);
                        field.set(
                                MinecraftClient.getInstance().getSession(),
                                textField.getText()
                        );

                        textField.setText("");
                    }
                    catch (NoSuchFieldException | IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
        }));
    }

}
