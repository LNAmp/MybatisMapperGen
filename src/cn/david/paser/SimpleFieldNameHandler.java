package cn.david.paser;

public class SimpleFieldNameHandler implements FieldNameHandler {

	
	public String getColumnName(String fieldName) {
		if(fieldName == null || fieldName.trim().equals("")) {
			return "";
		}
		if(fieldName.equals(fieldName.toLowerCase())) {
			return fieldName;
		}else {
			StringBuilder sb = new StringBuilder();
			char[] cName = fieldName.toCharArray();
			for(char c : cName) {
				if(Character.isUpperCase(c)) {
					sb.append('_');				
					sb.append(Character.toLowerCase(c));
				}else {
					sb.append(c);
				}
			}
			return sb.toString();
		}
	}
}
