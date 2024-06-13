package com.dbf.excel.records;

import java.util.Map;
import java.util.function.Supplier;

import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

/*
 * Similar to org.apache.poi.hssf.record.DimensionsRecord but support the older formats
 */
public class OldDimensionsRecord extends StandardRecord {

	public static final short sid = 0x200;
    private short field_1_first_row;
    private short field_2_last_row;
    private short field_3_first_col;
    private short field_4_last_col;
    private short field_5_zero;

    public OldDimensionsRecord(RecordInputStream in) {
        field_1_first_row = in.readShort();
        field_2_last_row  = in.readShort();
        field_3_first_col = in.readShort();
        field_4_last_col  = in.readShort();
        field_5_zero      = in.readShort();
    }
    
    public OldDimensionsRecord(OldDimensionsRecord oldDimensionsRecord) {
    	super(oldDimensionsRecord);
        field_1_first_row = oldDimensionsRecord.field_1_first_row;
        field_2_last_row  = oldDimensionsRecord.field_2_last_row;
        field_3_first_col = oldDimensionsRecord.field_3_first_col;
        field_4_last_col  = oldDimensionsRecord.field_4_last_col;
        field_5_zero      = oldDimensionsRecord.field_5_zero;
	}

	/**
     * get the first row number for the sheet
     * @return row - first row on the sheet
     */

    public int getFirstRow()
    {
        return field_1_first_row;
    }

    /**
     * get the last row number for the sheet
     * @return row - last row on the sheet
     */

    public int getLastRow()
    {
        return field_2_last_row;
    }

    /**
     * get the first column number for the sheet
     * @return column - first column on the sheet
     */

    public short getFirstCol()
    {
        return field_3_first_col;
    }

    /**
     * get the last col number for the sheet
     * @return column - last column on the sheet
     */

    public short getLastCol()
    {
        return field_4_last_col;
    }

	@Override
	public Map<String, Supplier<?>> getGenericProperties() {
		return GenericRecordUtil.getGenericProperties(
	            "firstRow", this::getFirstRow,
	            "lastRow", this::getLastRow,
	            "firstColumn", this::getFirstCol,
	            "lastColumn", this::getLastCol,
	            "zero", () -> field_5_zero
	        );
	}

	@Override
	protected int getDataSize() {
		return 10;
	}

	@Override
	protected void serialize(LittleEndianOutput out) {
		out.writeInt(getFirstRow());
        out.writeInt(getLastRow());
        out.writeShort(getFirstCol());
        out.writeShort(getLastCol());
        out.writeShort(( short ) 0);
	}

	@Override
	public StandardRecord copy() {
		return new OldDimensionsRecord(this);
	}

	@Override
	public short getSid() {
		return sid;
	}

	@Override
	public HSSFRecordTypes getGenericRecordType() {
		return HSSFRecordTypes.DIMENSIONS;
	}
}
