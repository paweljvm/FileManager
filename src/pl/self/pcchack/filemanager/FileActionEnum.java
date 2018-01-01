package pl.self.pcchack.filemanager;

import self.model.datatype.IEnum;

public enum FileActionEnum implements IEnum<Byte> {

	
	;
	private byte value;
	private String stringValue;
	@Override
	public Byte getValue() {
		
		return value;
	}
	@Override
	public String toString() {
		return stringValue;
	}

}
