public final class Util {

    public static String SYN = "SYN";
    public static String END = "END";
    public static String DEL = "DEL";
    public static String ACK = "ACK";

    public static int TYPE_IND = 0;
    public static int CMD1_IND= 1;
    public static int CMD2_IND = 4;
    public static int IP_AFTER_CMD1_IND = CMD2_IND;
    // maybe not the best decision but
    // knowing both names might help with understanding
    public static int IP_AFTER_CMD2_IND = 7;


}
