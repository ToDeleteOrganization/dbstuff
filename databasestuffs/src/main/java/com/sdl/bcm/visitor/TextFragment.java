package com.sdl.bcm.visitor;

import com.sdl.bcm.model.MarkupData;
import com.sdl.bcm.visitor.helper.OrderedList;
import com.sdl.bcm.visitor.helper.OrderedListInterface;

/**
 * @author vnastase
 * @since 2.5
 * an entry in the collapsed tree representation of a segment;
 * used for applying terminology
 * each text fragment has an associated location in containers given by their order in containers list
 * data represents a Markup leaf in the segment's tree representation (TextMarkup or PlaceholderTag)
 */
public class TextFragment {

    private int startIndex, endIndex; //absolute indexes of the text fragment
    private OrderedList<MarkupData> containers;     //shows in which containers this text fragment is included; order shows hierarchy in tree
    private MarkupData data; //the actual information; has no children: can be TextMarkup, PlaceHolderTag

    @SuppressWarnings("unchecked")
    public TextFragment(int startIndex, int endIndex, OrderedListInterface<MarkupData> containers, MarkupData data) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.containers = (OrderedList) containers;
        this.data = data;
    }
    OrderedListInterface<MarkupData> getContainers() {
        return containers;
    }
    int getStartIndex() {
        return startIndex;
    }
    int getEndIndex() {
        return endIndex;
    }
    MarkupData getData() {
        return data;
    }
    void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }
    void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
    void setData(MarkupData data) {
        this.data = data;
    }
}
