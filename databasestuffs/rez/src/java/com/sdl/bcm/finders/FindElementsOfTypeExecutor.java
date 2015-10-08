package com.sdl.bcm.finders;

import java.util.List;

import com.sdl.bcm.finders.options.BCMExecutorOptions;
import com.sdl.bcm.model.MetaData;
import com.sdl.bcm.visitor.BCMCompositeElement;

@SuppressWarnings("rawtypes")
public class FindElementsOfTypeExecutor implements BCMExecutor<MetaData> {

	BCMCompositeElement el;

	public FindElementsOfTypeExecutor(BCMCompositeElement el) {
		this.el = el;
	}

	public <MetaData> List<MetaData> execute(BCMExecutorOptions executorOptions, Class<MetaData> clas) {
		return null;
	}

//	@Override
//	public <RESULT> List<RESULT> execute(BCMExecutorOptions executorOptions, Class<RESULT> clas) {
//
////		List<RESULT> all = new ArrayList<RESULT>();
////
////		List<BCMCompositeElement> children = el.getChildren();
////		for (BCMCompositeElement bcmchild : children) {
////			if (bcmchild.getClass().equals(cl)) {
////				all.add(RESULT)bcmchild);
////			}
////		}
////		
////		return all;
////	
//	}

}
