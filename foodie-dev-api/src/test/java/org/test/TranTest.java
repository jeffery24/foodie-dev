package org.test;

import org.jeff.service.StuService;
import org.jeff.service.TestTransService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;

//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
public class TranTest {


    @Autowired
    private StuService stuService;

    @Autowired
    private TestTransService testTransService;

    private static String  filePath = "D:\\Project\\personal\\record\\notes\\Development Tool IDEA.md";

    //    @Test
    public void myTest() {
//        stuService.testPropagationTrans();
        testTransService.testPropagationTrans();
    }

    @Test
    public void writeFileURLTest() {
        String sourceFile = "D:\\Project\\personal\\record\\notes\\Development Tool IDEA.md";
        String targetFile = "D:\\Project\\personal\\record\\notes\\New_Development Tool IDEA.md";

        try (
                //FileInputStream fis = new FileInputStream(sourceFile);
                final FileReader fr = new FileReader(new File(sourceFile));
                final BufferedReader br = new BufferedReader(fr);
                final BufferedWriter bw = new BufferedWriter(new FileWriter(new File(targetFile)))
                //final FileOutputStream fos = new FileOutputStream(targetFile)
        ) {
            int r;
            String data;
            while ((data = br.readLine()) != null) {
                if (data.contains("<img src=")) {
                    continue;
                }

                bw.write(data + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void change(String oldStr, String newStr) {
        try {
            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
            String line;
            while (null != (line = raf.readLine())) {
                if (line.contains(oldStr)) {
                    String[] split = line.split(oldStr);
                    raf.seek(split[0].length());
                    raf.writeBytes(newStr);
                    raf.writeBytes(split[1]);
                }
            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
