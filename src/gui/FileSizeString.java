package gui;

import java.text.DecimalFormat;

public class FileSizeString {
	public String unit(long fileSize) {
		String unit;
		if (fileSize >= 1024) {
			fileSize /= 1024;
			if (fileSize >= 1024) {
				fileSize /= 1024;
				if (fileSize >= 1024) {
					fileSize /= 1024;
					unit = " Gb";
				} else {
					unit = " Mb";
				}
			} else {
				unit = " Kb";
			}
		} else {
			unit = " Bit";
		}
		return unit;
	}

	public String number(long size,String unit) {
		double sizeD = size;
		if(unit.equals(" Kb")){
			sizeD = sizeD / 1024;
		}else if(unit.equals(" Mb")){
			sizeD = sizeD / 1024 / 1024;
		}else if(unit.equals(" Gb")){
			sizeD = sizeD / 1024 / 1024 / 1024;
		}else{
			return size+"";
		}
		DecimalFormat df = new DecimalFormat("#.0");
		return df.format(sizeD);
	}
	
}
