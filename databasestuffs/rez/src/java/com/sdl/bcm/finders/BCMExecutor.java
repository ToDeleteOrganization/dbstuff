package com.sdl.bcm.finders;

import java.util.List;

import com.sdl.bcm.finders.options.BCMExecutorOptions;

public interface BCMExecutor<RESULT> {

	public <RESULT> List<RESULT> execute(BCMExecutorOptions executorOptions, Class<RESULT> clas);

}
