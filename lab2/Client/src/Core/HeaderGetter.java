package Core;
import Core.Client.Mode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

class HeaderGetter {
    // if mode equals 'TEST' - this file contains header data
    private final String inputFileName = "";
    // delimiter
    private String delimiter = "####";

    byte[] getHeader(Mode mode, File file) {
        switch (mode) {
            case TEST: {
                Scanner sc = null;
                FileInputStream input;
                try {
                    input = new FileInputStream(inputFileName);
                    sc = new Scanner(input);
                }
                catch (FileNotFoundException e) {
                    assert false;
                }
                String str = sc.nextLine();
                assert str != null;
                return str.getBytes();
            }
            case NORMAL: {
                //System.out.println(Integer.toString((file.getName() + delimiter + file.length() + delimiter).length()));
                return (file.getName() + delimiter + file.length() + delimiter).getBytes();
            }
            default:
                assert false;
        }
        // invalid case
        return null;
    }
}
