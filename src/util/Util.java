package util;


import java.util.List;

public class Util {
    public static byte[] toArray(List<Byte> array){
        byte filler[] = new byte[array.size()];
        int index = 0;
        for(Byte b: array){
            filler[index++] = b.byteValue();
        }
        return filler;
    }
    
    public static String formatStringWithLineBreaks(String message){
    	StringBuilder builder = new StringBuilder();
    	builder.append("<p>");
    	String[] withBreaks = message.split("\n");
    	for(String str : withBreaks){
    		builder.append(str);
    		builder.append("</br>");
    	}
    	builder.append("</p>");
    	return builder.toString();
    }
}
