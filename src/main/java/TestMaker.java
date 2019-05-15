import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class TestMaker {
    private static final int TEST_SIZE = 100;

    public static void main(String[] args){
        File file = new File("test/input" + TEST_SIZE + ".txt");
        file.getParentFile().mkdir();
        try(PrintWriter writer = new PrintWriter(new FileOutputStream(file))) {
            for(int i=0; i<TEST_SIZE; i++){
                writer.println("publish file_" + i + " content_of_file_" + i);
            }
            for(int i=0; i<TEST_SIZE; i++){
                writer.println("find file_" + i);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
