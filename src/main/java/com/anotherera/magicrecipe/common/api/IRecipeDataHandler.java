package com.anotherera.magicrecipe.common.api;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface IRecipeDataHandler {

	void save(DataOutputStream dos) throws IOException;

	void load(DataInputStream dis) throws IOException;

}
