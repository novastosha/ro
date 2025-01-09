package zodalix.ro.engine.screen.ui.elements;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface SelectableGUIElement extends GUIElement {

    /// If this set is null / empty, `this` element will be skipped and the logic will move on the next element in the parent [zodalix.ro.engine.screen.ui.GUIScreen]
    /// Otherwise, the selector stores this set and iterates through its children until finished tabulating to move on to the next element.
    /// e.g. Imagine a ListGUIElement, tabulating through the [zodalix.ro.engine.screen.ui.GUIScreen] until we reach it, but we cant just skip it, we must iterate through the list's elements.
    ///
    /// @return a nullable set of child elements.
    @Nullable Set<GUIElement> elementChildren();

}
