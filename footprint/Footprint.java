import caustic.runtime.*;
import java.util.HashMap;
import objectexplorer.MemoryMeasurer;

/**
 * A memory footprint calculator.
 */
public class Footprint {

    public static void main(String[] args) throws Exception {
        // String Footprint.
        StringBuilder builder = new StringBuilder();
        for (char i = 'A'; i <= 'Z'; i++) {
            sizeof("string", builder.toString());
            builder.append(i);
        }
  
        // Literal Footprint.
        sizeof("literal", new Flag(true));
        sizeof("literal", new Flag(false));
        sizeof("literal", new Real(Double.MAX_VALUE));
        sizeof("literal", new Real(Double.MIN_VALUE));
      
        builder = new StringBuilder();
        for (char i = 'A'; i <= 'Z'; i++) {
            sizeof("literal", new Text(builder.toString()));
            builder.append(i);
        }

        // Revision Footprint.
        sizeof("revision", new Revision(1L, new Flag(true)));
        sizeof("revision", new Revision(1L, new Flag(false))); 
        sizeof("revision", new Revision(1L, new Real(Double.MAX_VALUE)));
        sizeof("revision", new Revision(1L, new Real(Double.MIN_VALUE)));

        builder = new StringBuilder();
        for (char i = 'A'; i <= 'Z'; i++) {
            sizeof("revision", new Revision(1L, new Text(builder.toString())));
            builder.append(i);
        }
    }
    
    /**
     * Prints the size of specified object in bytes.
     *
     * @param x Object.
     */
    private static void sizeof(String category, Object x) {
        System.out.printf("%-10s%-50s%d\n", category, x.toString(), MemoryMeasurer.measureBytes(x));
    }
    
}
