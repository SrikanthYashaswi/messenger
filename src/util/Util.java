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
}
