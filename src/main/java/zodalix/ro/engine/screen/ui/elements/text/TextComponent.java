package zodalix.ro.game.screen.ui.elements.text;

import zodalix.ro.game.utils.BoundingBox;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class TextComponent {

    private final ArrayList<TextComponent> children;
    private Color color = Color.WHITE;

    TextComponent() {
        this.children = new ArrayList<>();
    }
    
    public static TextComponent text(String string) {
        return new StringTextComponent(string);
    }

    public static TextComponent composedText(String string, Object... objects) {
        var builder = new StringBuilder(string);
        int atObject = 0;

        for (int i = 0; i < builder.length(); i++) {
            if(atObject >= objects.length) break;

            var character = builder.charAt(i);
            if(character != '{') continue;

            var before = accessWithoutViolation(builder.toString(), i-1);
            var after = accessWithoutViolation(builder.toString(), i+1);

            if(after == '}') {
                if(before == '\\') {
                    builder.deleteCharAt(i-1);
                    i++;
                    continue;
                }

                builder.delete(i,i+2);
                builder.insert(i,objects[atObject].toString());

                atObject++;
            }
        }

        return parse(builder.toString());
    }

    public static TextComponent parse(String unparsedString) {
        var component = new TextComponent();
        final var basicStringStringBuilder = new StringBuilder();

        var parseBuffer = new StringBuilder();
        List<Consumer<TextComponent>> parsedActions = new ArrayList<>();

        final Runnable pushComponent = () ->
        {
            var addedComponent = text(basicStringStringBuilder.toString());
            parsedActions.forEach(consumer -> consumer.accept(addedComponent));

            component.append(addedComponent);
            parsedActions.clear();
            basicStringStringBuilder.setLength(0);
        };

        boolean isParsing = true, clauseOpen = false;

        for (int i = 0; i < unparsedString.length(); i++) {
            var character = unparsedString.charAt(i);
            if(character == '\\' && accessWithoutViolation(unparsedString, i+1) == '<') {
                isParsing = false;
                continue;
            }

            if (character == '>' && !isParsing) isParsing = true;
            if (character == '>' && clauseOpen) {
                var bufferFull = parseBuffer.toString();

                for (var buffer : bufferFull.split(" ")) {
                    var color = lookupColorFieldSafe(buffer);
                    if(color != null) parsedActions.add(textComponent -> textComponent.color(color));
                    else if(buffer.charAt(0) == '#') parsedActions.add(textComponent -> textComponent.color(Color.decode(buffer)));

                    else if(buffer.equalsIgnoreCase("break")) pushComponent.run();
                    else if(buffer.equalsIgnoreCase("shadow")) parsedActions.add(textComponent -> ((StringTextComponent) textComponent).shadow());
                    else if(buffer.equalsIgnoreCase("bold")) parsedActions.add(textComponent -> ((StringTextComponent) textComponent).bold());
                }

                parseBuffer.setLength(0);
                clauseOpen = false;
                continue;
            }

            if (character == '<' && isParsing) {
                clauseOpen = true;
                pushComponent.run();
                continue;
            }

            if (!isParsing || !clauseOpen) basicStringStringBuilder.append(character);
            else parseBuffer.append(character);
        }

        if(!basicStringStringBuilder.isEmpty()) {
            pushComponent.run();
        }
        return component;
    }

    private static char accessWithoutViolation(String original, int index) {
        try {
            return original.charAt(index);
        } catch(Throwable _) { return Character.MAX_VALUE; }
    }

    private static Color lookupColorFieldSafe(String color) {
        try {
            return (Color) Color.class.getDeclaredField(color.toUpperCase(Locale.ROOT)).get(null);
        } catch (Throwable _ ) { return null; }
    }

    public TextComponent append(TextComponent other) {
        this.children.add(other);
        return this;
    }

    public TextComponent append(String string, Object... objects) {
        return append(composedText(string, objects));
    }

    public TextComponent color(Color color) {
        this.color = color;
        return this;
    }

    public List<TextComponent> children() {
        return Collections.unmodifiableList(children);
    }

    public Color color() {
        return color;
    }

    public BoundingBox boundingBox() {
        float leftX = 0,rightX = 0,topY = 0 ,bottomY = 0;

        for (var textComponent : this.children) {
            var boundingBox = textComponent.boundingBox();

            {
               leftX += boundingBox.leftX();
               rightX += boundingBox.rightX();
            }

            {
                topY = Math.max(topY, boundingBox.topY());
                bottomY = Math.min(bottomY, boundingBox.bottomY());
            }
        }

        return new BoundingBox(leftX,rightX,topY,bottomY);
    }
}
