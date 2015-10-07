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

	// by default static
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

			// if we have an id and definitions, search for the first def with that id.
			Q skeletonItem = definitions.stream().filter((skelItem) -> {
				return (skelItem.getId() == definitionId);
			}).findFirst().get();;
			
			return skeletonItem;
		}
	}

}
