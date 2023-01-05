package net.kyrptonaught.tooltipfix.mixin;

import com.google.common.collect.Lists;
import net.kyrptonaught.tooltipfix.Helper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.StringVisitable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mixin(Screen.class)
public abstract class FixToolTipMixin {
    @Shadow
    protected TextRenderer textRenderer;
    @Shadow
    public int width;

    @Shadow
    public abstract void renderOrderedTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y);

    @Redirect(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderOrderedTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V"))
    public void fixText(Screen screen, MatrixStack matrices, List<? extends OrderedText> stupidLines, int x, int y, MatrixStack matrices2, Text text) {
        Helper.set(x, width);
        this.renderOrderedTooltip(matrices, Lists.transform(Helper.doFix(Collections.singletonList(text), textRenderer), Text::asOrderedText), Helper.x, y);
    }

    @Redirect(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderTooltipFromComponents(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V"))
    public void fixTexts(Screen screen, MatrixStack matrices, List<? extends OrderedText> stupidLines, int x, int y, MatrixStack matrices2, List<Text> lines) {
        Helper.set(x, width);
        this.renderOrderedTooltip(matrices, Lists.transform(Helper.doFix(lines, textRenderer), Text::asOrderedText), Helper.x, y);
    }

    @Redirect(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;gatherTooltipComponents(Lnet/minecraft/item/ItemStack;Ljava/util/List;Ljava/util/Optional;IIILnet/minecraft/client/font/TextRenderer;Lnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"))
    public List<TooltipComponent> fixWTTData(ItemStack stack, List<? extends StringVisitable> textElements, Optional<TooltipData> itemComponent, int mouseX, int screenWidth, int screenHeight, @Nullable TextRenderer forcedFont, TextRenderer fallbackFont) {
        Helper.set(mouseX, width);
        List<Text> list = Helper.doFix((List<Text>)textElements, textRenderer);
        return ForgeHooksClient.gatherTooltipComponents(
                stack, list, itemComponent, mouseX, screenWidth, screenHeight, forcedFont, fallbackFont
        );
    }
}