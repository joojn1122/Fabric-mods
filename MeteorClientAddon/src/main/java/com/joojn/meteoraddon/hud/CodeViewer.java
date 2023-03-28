package com.joojn.meteoraddon.hud;

import com.joojn.meteoraddon.MeteorClientUtils;
import com.joojn.meteoraddon.hud.highlighter.CodeHighlighter;
import com.joojn.meteoraddon.hud.highlighter.theme.ITheme;
import com.joojn.meteoraddon.hud.highlighter.theme.ThemeFactory;
import com.joojn.meteoraddon.utils.BytecodeUtils;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;
import java.util.ArrayList;
import java.util.HashMap;

public class CodeViewer extends Screen {

    public static CodeViewer INSTANCE;

    private ITheme theme;
    private ArrayList<Pair<Integer, String>> codeParts;

    private final static HashMap<String, String> loadedClasses = new HashMap<>();

    public CodeViewer(String className) throws Exception {
        super(Text.literal("Code Viewer"));

        setTheme(ThemeFactory.ONE_DARK);
        updateSource(className);

        INSTANCE = this;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // super.render(matrices, mouseX, mouseY, delta);
        this.renderBackground(matrices);

        HudRenderer.INSTANCE.text(
                "Text",
                0,
                0,
                meteordevelopment.meteorclient.utils.render.color.Color.WHITE,
                true
        );

        if(true) return;

        int startX = this.width / 3;
        int startY = 100;

        int width = this.width - (startX * 2);
        int height = this.height - (startY * 2);

        startX = 0;
        startY = 0;
        width = this.width;
        height = this.height;

        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

        DrawableHelper.fill(
                matrices,
                startX,
                startY,
                startX + width,
                startY + height,
                theme.background()
        );

        int currentX = startX;
        int currentY = startY;

        for(Pair<Integer, String> pair : codeParts) {
            String[] texts = pair.getRight().split("\n");

            for(int i = 0; i < texts.length; i++) {
                String text = texts[i];
//                String[] words = text.split(" ");

//                for(int j = 0; j < words.length; j++) {
//                    String word = words[j] + (j != words.length - 1 ? " " : "");
//
//                    int len = renderer.getWidth(word);
//                    if(currentX + len > width) {
//                        currentX = startX;
//                        currentY += renderer.fontHeight + 2;
//                    }
//
//                    renderer.drawWithShadow(
//                            matrices,
//                            word,
//                            currentX,
//                            currentY,
//                            theme.getColor(pair.getLeft())
//                    );
//
//                    currentX += len;
//                }

                renderer.drawWithShadow(
                        matrices,
                        text,
                        currentX,
                        currentY,
                        theme.getColor(pair.getLeft())
                );

                if(i != texts.length - 1) {
                    currentY += renderer.fontHeight + 2;
//                    currentX = startX;
                }
            }
        }
    }

    private final static ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();
    private static final class PrinterIml implements Printer {
        private static final String TAB = "    ";
        private static final String NEWLINE = "\n";

        private int indentationCount = 0;
        private final StringBuilder sb = new StringBuilder();

        @Override public String toString() { return sb.toString(); }

        @Override public void start(int maxLineNumber, int majorVersion, int minorVersion) {}
        @Override public void end() {}

        @Override public void printText(String text) { sb.append(text); }
        @Override public void printNumericConstant(String constant) { sb.append(constant); }
        @Override public void printStringConstant(String constant, String ownerInternalName) { sb.append(constant); }
        @Override public void printKeyword(String keyword) { sb.append(keyword); }
        @Override public void printDeclaration(int type, String internalTypeName, String name, String descriptor) { sb.append(name); }
        @Override public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) { sb.append(name); }

        @Override public void indent() { this.indentationCount++; }
        @Override public void unindent() { this.indentationCount--; }

        @Override public void startLine(int lineNumber) {
            sb.append(TAB.repeat(Math.max(0, indentationCount)));
        }
        @Override public void endLine() { sb.append(NEWLINE); }
        @Override public void extraLine(int count) { while (count-- > 0) sb.append(NEWLINE); }

        @Override public void startMarker(int type) {}
        @Override public void endMarker(int type) {}

        public void reset() {
            indentationCount = 0;
            sb.setLength(0);
        }
    }
    private final static PrinterIml printer = new PrinterIml();
    private final static Loader loader = new Loader() {
        @Override
        public byte[] load(String internalName) throws LoaderException {
            try{
                return BytecodeUtils.getBytecode(internalName);
            }
            catch (Exception e) {
                throw new LoaderException(e);
            }
        }

        @Override
        public boolean canLoad(String internalName) {
            return BytecodeUtils.classExists(internalName);
        }
    };

    public static String getCode(String className) throws Exception {

        // byte[] byteCode = BytecodeUtils.getBytecode(className);
        // MeteorClientUtils.LOGGER.info("Found class bytecode, length: " + byteCode.length);

        decompiler.decompile(loader, printer, className);

//        decompiler.decompileContext(new DecompilerContext(), new IBytecodeProvider() {
//            @Override
//            public byte[] getBytecode(String externalPath, String internalPath) throws IOException {
//                if (internalPath.equals(ContextUnit.DEFAULT_CLASS_NAME + ".class")) {
//                    return bytecode;
//                }
//                return null;
//            }
//        });

        String result = printer.toString();
        printer.reset();

        MeteorClientUtils.LOGGER.info("Found source code for %s, code:\n%s".formatted(className, result));

        return result;
    }

    public void updateSource(String className) throws Exception {
        String code;
        if(!loadedClasses.containsKey(className)) {

            code = getCode(className);

            // class can be without a package
            if(className.contains("."))
            {
                String packageName = className.substring(0, className.lastIndexOf("."));

                // for some reason, the decompiler doesn't show the package name
                code = "package %s;\n\n%s".formatted(packageName, code);
            }

            loadedClasses.put(className, code);
        }
        else
        {
            code = loadedClasses.get(className);
        }

        codeParts = CodeHighlighter.highlightCode(
                code
        );
    }

    public void setTheme(ITheme theme) {
        this.theme = theme;
    }
}
