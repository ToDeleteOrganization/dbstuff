package com.sdl.bcm;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sdl.bcm.model.fileskeleton.AbstractSkeletonItem;

public interface ISkeletonItemReference<T extends AbstractSkeletonItem> {

	/**
	 * It is used to get the corresponding definition from the skeleton.
	 * 
	 * @return The definition.
	 */
	@JsonIgnore
	T getSkeletonDefinition();

	/**
	 * Inner class specific used to find an AbstractSkeletonItem in the context of a SkeletonItemReference.
	 * 
	 * @author mariuscocoi
	 *
	 * @param <Q> The skeleton item (definition).
	 */
	class FileReferenceFinder<Q extends AbstractSkeletonItem> {
		private Integer definitionId;
		
		private List<Q> definitions;

		public FileReferenceFinder(List<Q> skel, Integer definitionId) {
			this.definitionId = definitionId;
			this.definitions = skel;
		}
		
		public Q find() {
			if ((definitionId == null) || CollectionUtils.isEmpty(definitions)) {
				return null;
			}

			Q skeletonItem = null;
			for (Q absSkeletonItem : definitions) {
				if (absSkeletonItem.getId() == definitionId) {
					skeletonItem = absSkeletonItem;
					break;
				}
			}
			return skeletonItem;
		}
	}

}
