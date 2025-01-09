package zodalix.ro.engine.screen.ui.elements.text;

import zodalix.ro.engine.utils.BoundingBox;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class StringTextComponent extends TextComponent {
    public static final char CURSOR_CHARACTER = 240;

    // remove asap
    public static final Set<Character> LOW_OFFSET_CHARACTERS = Set.of(
            ' ', 'i', 'l', 'I', 't', 'f', '\'', ',', ':', CURSOR_CHARACTER
    );

    public static final float normalizedWidth = 10f / 16f;

    private final String string;
    private boolean shadow = false, bold = false;

    public StringTextComponent(String string) {
        super();
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public StringTextComponent shadow() {
        this.shadow = true;
        return this;
    }

    public StringTextComponent bold() {
        this.bold = true;
        return this;
    }

    @Override
    public BoundingBox boundingBox() {
        float lr = normalizedWidth * this.string.length();
        for (final char i : this.string.toCharArray())
            if (LOW_OFFSET_CHARACTERS.contains(i)) lr -= normalizedWidth * .575f;

        return BoundingBox.rectangle(lr, normalizedWidth + .25f);
    }

    public boolean shouldDrawShadow() {
        return shadow;
    }

    public boolean isBold() {
        return bold;
    }
}
