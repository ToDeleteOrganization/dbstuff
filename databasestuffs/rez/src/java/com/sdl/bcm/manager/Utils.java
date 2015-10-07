package com.sdl.bcm.manager;

import com.sdl.bcm.model.MetaData;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *  Created by dtarba on 6/16/2015.
 */
public class Utils {

    private Utils() {
    }

    public static boolean deepEqualsCollection(Collection collection1, Collection collection2) {
        if(collection1 == null && collection2 == null)
            return true;
        if(collection1 != null ^ collection2 != null)
            return false; //XOR
        if(collection1.size() != collection2.size())
            return false;

        return !checkItems(collection1, collection2);
    }

    private static boolean checkItems(Collection collection1, Collection collection2) {
        for(Object item1 : collection1) {
            boolean matchFound = false;
            if (! (item1 instanceof MetaData)) {
                return true;
            }
            MetaData metaData1 = (MetaData) item1;
            for(Object item2 : collection2) {
                if (metaData1.deepEquals(item2)) {
                    matchFound = true;
                    break;
                }
            }
            if(!matchFound)
                return true;
        }
        return false;
    }

    /**
     * 
     * @param listToClone The list to be cloned
     * @return
     * A cloned list, <i>null</i> if the list is empty
     */
    @SuppressWarnings("unchecked")
	public static <T extends MetaData> List<T> deepCloneList(List<T> listToClone) {
    	if (listToClone == null) {
    		return null;
    	}

    	List<T> clonedList = new LinkedList<>();
		for (T elementToClone : listToClone) {
			if (elementToClone != null) {
				clonedList.add((T)elementToClone.deepClone());
			}
		}
    	return clonedList;
    }
}
