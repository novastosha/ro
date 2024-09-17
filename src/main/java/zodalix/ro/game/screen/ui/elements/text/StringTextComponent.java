package zodalix.ro.game.screen.ui.elements.text;

import zodalix.ro.game.utils.BoundingBox;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class StringTextComponent extends TextComponent {
    public static final char CURSOR_CHARACTER = 240;

    public static final Set<Character> LOW_OFFSET_CHARACTERS = Set.of(
            ' ', 'i', 'l', 'I', 't', 'f', '\'', ',', ':', CURSOR_CHARACTER
    );

    public static final Set<Character> LOW_HANGING_CHARACTERS = Set.of('g', 'y', 'j', 'p', 'q');

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
        AtomicReference<Float> lr = new AtomicReference<>(normalizedWidth * this.string.length()); // MULTIPLY BY SCALE IN TEXT CLASS
        this.string.chars().forEach(i -> {
            if (LOW_OFFSET_CHARACTERS.contains((char) i)) {
                lr.updateAndGet(v -> (v - normalizedWidth * .575f));
            }
        });

        return BoundingBox.rectangle(lr.get(), normalizedWidth + .25f);
    }

    public boolean shouldDrawShadow() {
        return shadow;
    }

    public boolean isBold() {
        return bold;
    }
}
